package com.user_service.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.config.JwtProvider;
import com.user_service.dao.UserRepository;
import com.user_service.dto.LoginRequestDTO;
import com.user_service.dto.SignupRequestDTO;
import com.user_service.enums.Role;
import com.user_service.models.User;
import com.user_service.response.AuthResponse;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
	
	 @Autowired
	    private UserRepository userRepository;

	 @Autowired
	   	private PasswordEncoder passwordEncoder;
	 
	 @Autowired
	    private ModelMapper modelMapper;
	 
	 @Autowired
	 	private CustomUserServiceImpl customUserDetails;
	 
	 

	@Override
	public AuthResponse registerUser(SignupRequestDTO request) throws Exception {
		String normalizedEmail = request.getEmail().trim().toLowerCase();
		
		System.out.println("Entered mail ----------------> "+ request.getEmail());
		System.out.println("repo method "+userRepository.findByEmail(normalizedEmail));
		if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new Exception("Email Already Exist");
        }
		Role role = Role.valueOf(request.getRole().toUpperCase());
        // Map DTO to Entity
        User newUser = modelMapper.map(request, User.class);
        newUser.setRole(role);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            request.getEmail(), request.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        // can also map entity to response if needed
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Registration success");
        authResponse.setStatus(true);

        return authResponse;
	}

	@Override
	public Authentication authenticate(String username, String password) throws Exception {
		UserDetails userDetails = customUserDetails.loadUserByUsername(username);
		
		if(userDetails==null) {
			throw new BadCredentialsException("Invalid username and password");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid username and password");
		}
		return new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
	}

	@Override
	public AuthResponse LoginUser(LoginRequestDTO request) throws Exception {
		// TODO Auto-generated method stub
		String username = request.getEmail();
		String password = request.getPassword();
		
		System.out.println("username "+username+"\n password "+password);
		
		Authentication authentication = authenticate(username,password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = JwtProvider.generateToken(authentication);
		
	     AuthResponse authResponse = new AuthResponse();
	     
	     authResponse.setMessage("Login Success");
	     authResponse.setJwt(token);
	     
	     authResponse.setStatus(true);
		
		return authResponse;
	}

}
