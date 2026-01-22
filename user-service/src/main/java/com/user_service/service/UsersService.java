package com.user_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.user_service.controller.UpdateProfileRequest;
import com.user_service.dto.response.ProfileDTO;
import com.user_service.dto.response.UserResponse;
import com.user_service.enums.UserStatus;

public interface UsersService {
	ProfileDTO getUserProfile(String jwt);
	List<UserResponse> getAllUsers();
	UserResponse getUserById(UUID id);
    String getUserNameById(Long id);
    void updateAvatar(Long userId, String avatarUrl);
    List<ProfileDTO> getAllUsersByRole();
    UserResponse getUserByEmail(String email);
    List<UserResponse> getUsersByProject(UUID projectId);
    List<UserResponse> getUsersByStatus(UserStatus status);
    UserResponse updateProfile(UUID userId, UpdateProfileRequest request);
    String getUserName(UUID userId);
    Map<String, Object> getUserStatistics();
}
