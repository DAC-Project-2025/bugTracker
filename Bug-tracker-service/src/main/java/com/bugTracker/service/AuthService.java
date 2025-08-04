package com.bugTracker.service;

import org.springframework.security.core.Authentication;

import com.bugTracker.dto.LoginRequestDTO;
import com.bugTracker.dto.SignupRequestDTO;
import com.bugTracker.response.AuthResponse;

public interface AuthService {
	AuthResponse registerUser(SignupRequestDTO request) throws Exception;
	AuthResponse LoginUser(LoginRequestDTO request) throws Exception;
	Authentication authenticate(String username, String password) throws Exception; 
}
