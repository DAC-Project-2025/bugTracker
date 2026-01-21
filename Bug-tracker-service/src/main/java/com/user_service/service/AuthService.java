package com.user_service.service;

import org.springframework.security.core.Authentication;

import com.user_service.dto.LoginRequestDTO;
import com.user_service.dto.SignupRequestDTO;
import com.user_service.response.AuthResponse;

public interface AuthService {
	AuthResponse registerUser(SignupRequestDTO request) throws Exception;
	AuthResponse LoginUser(LoginRequestDTO request) throws Exception;
	Authentication authenticate(String username, String password) throws Exception; 
}
