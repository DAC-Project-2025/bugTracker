package com.bugTracker.service;

import java.util.List;

import com.bugTracker.dto.ProfileDTO;

public interface UsersService {
	ProfileDTO getUserProfile(String jwt);
	List<ProfileDTO> getAllUsers();
}
