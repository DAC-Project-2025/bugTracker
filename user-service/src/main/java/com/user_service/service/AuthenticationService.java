package com.user_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.dto.request.LoginRequest;
import com.user_service.dto.request.RefreshTokenRequest;
import com.user_service.dto.request.RegisterRequest;
import com.user_service.dto.request.VerifyEmailRequest;
import com.user_service.dto.request.VerifyOtpRequest;
import com.user_service.dto.response.AuthResponse;
import com.user_service.dto.response.VerificationResponse;
import com.user_service.enums.AuthProvider;
import com.user_service.enums.RoleType;
import com.user_service.enums.UserStatus;
import com.user_service.event.UserCreatedEvent;
import com.user_service.event.UserLoginEvent;
import com.user_service.event.UserVerifiedEvent;
import com.user_service.exception.EmailAlreadyExistsException;
import com.user_service.exception.InvalidOtpException;
import com.user_service.exception.InvalidTokenException;
import com.user_service.exception.UnverifiedUserException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UserSuspendedException;
import com.user_service.mapper.EventMapper;
import com.user_service.mapper.UserMapper;
import com.user_service.models.Role;
import com.user_service.models.User;
import com.user_service.models.UserRole;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final OutboxService outboxService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    /**
     * Register new user (local authentication)
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .avatarUrl(request.getAvatarUrl())
                .authProvider(AuthProvider.LOCAL)
                .status(UserStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .emailVerificationToken(UUID.randomUUID().toString())
                .emailVerificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        // Assign default DEVELOPER role (system-wide, no project)
        Role developerRole = roleRepository.findByName(RoleType.DEVELOPER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(developerRole)
                .projectId(null)  // System-wide
                .build();

        user.getUserRoles().add(userRole);
        userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFullName(),
                user.getEmailVerificationToken()
        );

        // Publish event to Kafka via Outbox
        UserCreatedEvent event = eventMapper.toUserCreatedEvent(user);
        outboxService.saveEvent(event);

        log.info("User registered successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .message("Registration successful. Please check your email to verify your account.")
                .requiresOtp(false)
                .user(userMapper.toResponse(user))
                .build();
    }

    /**
     * Verify email with token
     */
    @Transactional
    public VerificationResponse verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmailVerificationToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token"));

        if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Verification token has expired");
        }

        user.markEmailAsVerified();
        userRepository.save(user);

        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());

        // Publish event
        UserVerifiedEvent event = eventMapper.toUserVerifiedEvent(user);
        outboxService.saveEvent(event);

        log.info("Email verified for user: {}", user.getEmail());

        return VerificationResponse.builder()
                .success(true)
                .message("Email verified successfully! You can now log in.")
                .build();
    }

    /**
     * Login (Step 1) - Validate credentials and send OTP
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new UnverifiedUserException("Please verify your email before logging in");
        }

        // Check if user is active
        if (!user.isActive()) {
            throw new UserSuspendedException("Your account has been suspended");
        }

        // Generate and send OTP
        String otp = otpService.generateOtp(user.getEmail());
        emailService.sendOtpEmail(
                user.getEmail(),
                user.getFullName(),
                otp,
                otpService.getExpirationMinutes()
        );

        log.info("OTP sent to user: {}", user.getEmail());

        return AuthResponse.builder()
                .message("OTP sent to your email. Please verify to complete login.")
                .requiresOtp(true)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Verify OTP and generate tokens
     */
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        log.info("OTP verification for email: {}", request.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate OTP
        if (!otpService.validateOtp(user.getEmail(), request.getOtp())) {
            int remaining = otpService.getRemainingAttempts(user.getEmail());
            throw new InvalidOtpException(
                    "Invalid OTP. Remaining attempts: " + remaining);
        }

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Publish login event
        UserLoginEvent loginEvent = UserLoginEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .eventType("UserLoginEvent")
                .aggregateId(user.getId())
                .aggregateType("User")
                .userId(user.getId())
                .email(user.getEmail())
                .loginAt(LocalDateTime.now())
                .success(true)
                .build();
        outboxService.saveEvent(loginEvent);

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)  // 24 hours
                .user(userMapper.toResponse(user))
                .requiresOtp(false)
                .message("Login successful")
                .build();
    }

    /**
     * Refresh access token
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        User user = jwtService.getUserFromRefreshToken(request.getRefreshToken());

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Revoke old refresh token
        jwtService.revokeRefreshToken(request.getRefreshToken());

        log.info("Token refreshed for user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userMapper.toResponse(user))
                .message("Token refreshed successfully")
                .build();
    }

    /**
     * Logout - Revoke refresh token
     */
    @Transactional
    public void logout(String refreshToken) {
        jwtService.revokeRefreshToken(refreshToken);
        log.info("User logged out successfully");
    }

    /**
     * OAuth2 Google login/register
     */
    @Transactional
    public AuthResponse handleOAuth2Success(String email, String name, String googleId, String avatarUrl) {
        log.info("OAuth2 login attempt for email: {}", email);

        // Find or create user
        User user = userRepository.findByOauthProviderIdAndAuthProvider(googleId, AuthProvider.GOOGLE)
                .orElseGet(() -> {
                    // Check if email exists with different provider
                    Optional<User> existingUser = userRepository.findByEmail(email);
                    if (existingUser.isPresent()) {
                        throw new EmailAlreadyExistsException(
                                "Email already registered with different authentication method");
                    }

                    // Create new user
                    User newUser = User.builder()
                            .email(email.toLowerCase().trim())
                            .fullName(name)
                            .avatarUrl(avatarUrl)
                            .authProvider(AuthProvider.GOOGLE)
                            .oauthProviderId(googleId)
                            .status(UserStatus.ACTIVE)
                            .emailVerified(true)  // Google already verified
                            .build();

                    // Assign default role
                    Role developerRole = roleRepository.findByName(RoleType.DEVELOPER)
                            .orElseThrow(() -> new RuntimeException("Default role not found"));

                    UserRole userRole = UserRole.builder()
                            .user(newUser)
                            .role(developerRole)
                            .build();

                    newUser.getUserRoles().add(userRole);
                    userRepository.save(newUser);

                    // Send welcome email
                    emailService.sendWelcomeEmail(newUser.getEmail(), newUser.getFullName());

                    // Publish event
                    UserCreatedEvent event = eventMapper.toUserCreatedEvent(newUser);
                    outboxService.saveEvent(event);

                    log.info("New OAuth2 user created: {}", newUser.getEmail());
                    return newUser;
                });

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Publish login event
        UserLoginEvent loginEvent = UserLoginEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .eventType("UserLoginEvent")
                .aggregateId(user.getId())
                .aggregateType("User")
                .userId(user.getId())
                .email(user.getEmail())
                .loginAt(LocalDateTime.now())
                .success(true)
                .build();
        outboxService.saveEvent(loginEvent);

        log.info("OAuth2 user logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userMapper.toResponse(user))
                .message("OAuth2 login successful")
                .build();
    }

    /**
     * Create Spring Security Authentication object
     */
    public Authentication createAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName().name()))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities
        );
    }
}
