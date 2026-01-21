package com.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.dto.LoginRequestDTO;
import com.user_service.dto.SignupRequestDTO;
import com.user_service.response.AuthResponse;
import com.user_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    
    @GetMapping("test")
	public ResponseEntity<?> testGateway(){
		return ResponseEntity.ok("AuthController has been called");
	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequestDTO request) throws Exception{
		AuthResponse response = authService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@Valid @RequestBody LoginRequestDTO loginRequest) throws Exception{
		AuthResponse response = authService.LoginUser(loginRequest);
		return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
	}
	
}
