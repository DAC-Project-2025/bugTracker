package com.user_service.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.dto.request.AssignRoleRequest;
import com.user_service.dto.request.UpdateProfileRequest;
import com.user_service.dto.response.UserResponse;
import com.user_service.enums.RoleType;
import com.user_service.enums.UserStatus;
import com.user_service.event.UserDeletedEvent;
import com.user_service.event.UserRoleAssignedEvent;
import com.user_service.event.UserUpdatedEvent;
import com.user_service.exception.RoleNotFoundException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.mapper.EventMapper;
import com.user_service.mapper.UserMapper;
import com.user_service.models.Role;
import com.user_service.models.User;
import com.user_service.models.UserRole;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;
import com.user_service.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final OutboxService outboxService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        return userMapper.toResponse(user);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email.toLowerCase().trim())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        return userMapper.toResponse(user);
    }

    /**
     * Get all active users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllActive();
        return userMapper.toResponseList(users);
    }

    /**
     * Get users by project
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByProject(UUID projectId) {
        List<User> users = userRepository.findByProjectId(projectId);
        return userMapper.toResponseList(users);
    }

    /**
     * Get users by role and project
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRoleAndProject(String roleName, UUID projectId) {
        List<User> users = userRepository.findByRoleAndProject(roleName, projectId);
        return userMapper.toResponseList(users);
    }

    /**
     * Get users by status
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByStatus(UserStatus status) {
        List<User> users = userRepository.findByStatus(status);
        return userMapper.toResponseList(users);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Map<String, Object> changedFields = new HashMap<>();

        if (request.getFullName() != null && !request.getFullName().equals(user.getFullName())) {
            changedFields.put("fullName", Map.of(
                "old", user.getFullName(),
                "new", request.getFullName()
            ));
            user.setFullName(request.getFullName());
        }

        if (request.getAvatarUrl() != null && !request.getAvatarUrl().equals(user.getAvatarUrl())) {
            changedFields.put("avatarUrl", Map.of(
                "old", user.getAvatarUrl(),
                "new", request.getAvatarUrl()
            ));
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (!changedFields.isEmpty()) {
            userRepository.save(user);

            // Publish update event
            UserUpdatedEvent event = UserUpdatedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .timestamp(LocalDateTime.now())
                    .eventType("UserUpdatedEvent")
                    .aggregateId(user.getId())
                    .aggregateType("User")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .changedFields(changedFields)
                    .build();
            outboxService.saveEvent(event);

            log.info("User profile updated: {}", userId);
        }

        return userMapper.toResponse(user);
    }

    /**
     * Assign role to user (project-scoped or system-wide)
     */
    @Transactional
    public void assignRole(AssignRoleRequest request, UUID assignedBy) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        RoleType roleType = RoleType.valueOf(request.getRoleName().toUpperCase());
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + request.getRoleName()));

        // Check if role already assigned
        if (userRoleRepository.existsByUserIdAndRoleIdAndProjectId(
                user.getId(), role.getId(), request.getProjectId())) {
            throw new IllegalArgumentException("Role already assigned to user for this project");
        }

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .projectId(request.getProjectId())
                .assignedBy(assignedBy)
                .build();

        userRoleRepository.save(userRole);

        // Publish event
        UserRoleAssignedEvent event = eventMapper.toUserRoleAssignedEvent(userRole);
        outboxService.saveEvent(event);

        log.info("Role {} assigned to user {} for project {}", 
            roleType, user.getEmail(), request.getProjectId());
    }

    /**
     * Remove user from project
     */
    @Transactional
    public void removeUserFromProject(UUID userId, UUID projectId) {
        userRoleRepository.deleteByUserIdAndProjectId(userId, projectId);
        log.info("User {} removed from project {}", userId, projectId);
    }

    /**
     * Suspend user
     */
    @Transactional
    public void suspendUser(UUID userId, UUID suspendedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("User is already suspended");
        }

        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);

        log.info("User suspended: {} by {}", userId, suspendedBy);
    }

    /**
     * Activate user
     */
    @Transactional
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("User is not suspended");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        log.info("User activated: {}", userId);
    }

    /**
     * Soft delete user
     */
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.softDelete();
        userRepository.save(user);

        // Publish delete event
        UserDeletedEvent event = eventMapper.toUserDeletedEvent(user);
        outboxService.saveEvent(event);

        log.info("User soft deleted: {}", userId);
    }

    /**
     * Get user name by ID (for other services via Feign)
     */
    @Transactional(readOnly = true)
    public String getUserName(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getFullName();
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatusAndCreatedAtAfter(UserStatus.ACTIVE, lastMonth);
        long pendingVerification = userRepository.countByStatus(UserStatus.PENDING_VERIFICATION);
        long suspendedUsers = userRepository.countByStatus(UserStatus.SUSPENDED);
        long activeLastMonth = userRepository.countActiveUsersSince(lastMonth);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("pendingVerification", pendingVerification);
        stats.put("suspendedUsers", suspendedUsers);
        stats.put("activeLastMonth", activeLastMonth);

        return stats;
    }

    /**
     * Check if user has role in project
     */
    @Transactional(readOnly = true)
    public boolean hasRoleInProject(UUID userId, RoleType role, UUID projectId) {
        Role roleEntity = roleRepository.findByName(role)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        
        return userRoleRepository.existsByUserIdAndRoleIdAndProjectId(
                userId, roleEntity.getId(), projectId);
    }

    /**
     * Check if user is admin
     */
    @Transactional(readOnly = true)
    public boolean isAdmin(UUID userId) {
        List<UserRole> systemRoles = userRoleRepository.findSystemWideRolesByUserId(userId);
        return systemRoles.stream()
                .anyMatch(ur -> ur.getRole().getName() == RoleType.ADMIN);
    }
}
