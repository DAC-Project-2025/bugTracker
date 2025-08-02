package com.bugTracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugTracker.dto.ProfileDTO;
import com.bugTracker.service.UserServiceImpl;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

	private final UserServiceImpl userServiceImpl;
	
	@GetMapping("/profile")
	public ResponseEntity<ProfileDTO> getUserProfile(@RequestHeader("Authorization") String jwt){
		ProfileDTO profile = userServiceImpl.getUserProfile(jwt);
		return ResponseEntity.ok(profile);
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<ProfileDTO>> getAllUser(@RequestHeader("Authorization") String jwt){
		
		List<ProfileDTO> users = userServiceImpl.getAllUsers();
		
		return ResponseEntity.ok(users);
	}
	
	
}
