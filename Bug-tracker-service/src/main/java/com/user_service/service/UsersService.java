package com.user_service.service;

import java.util.List;

import com.user_service.dto.ProfileDTO;

public interface UsersService {
	ProfileDTO getUserProfile(String jwt);
	List<ProfileDTO> getAllUsers();
	ProfileDTO getUserById(Long id);
    String getUserNameById(Long id);
    void updateAvatar(Long userId, String avatarUrl);
    List<ProfileDTO> getAllUsersByRole();
}
