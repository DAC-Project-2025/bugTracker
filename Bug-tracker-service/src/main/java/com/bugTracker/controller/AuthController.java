package com.bugTracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugTracker.dto.LoginRequestDTO;
import com.bugTracker.dto.SignupRequestDTO;
import com.bugTracker.response.AuthResponse;
import com.bugTracker.service.AuthService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;

	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequestDTO request) throws Exception{
		AuthResponse response = authService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequestDTO loginRequest) throws Exception{
		AuthResponse response = authService.LoginUser(loginRequest);
		return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
	}
	
}
