package com.user_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.dto.AvatarUpdateRequestDto;
import com.user_service.dto.ProfileDTO;
import com.user_service.service.UserServiceImpl;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

	private final UserServiceImpl userServiceImpl;

	@GetMapping("test")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<?> testGateway() {
		return ResponseEntity.ok("UserController has been called");
	}

	@GetMapping("/profile")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<ProfileDTO> getUserProfile(@RequestHeader("Authorization") String jwt) {
		ProfileDTO profile = userServiceImpl.getUserProfile(jwt);
		return ResponseEntity.ok(profile);
	}

	@GetMapping("/users")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<List<ProfileDTO>> getAllUser(@RequestHeader("Authorization") String jwt) {

		List<ProfileDTO> users = userServiceImpl.getAllUsers();

		return ResponseEntity.ok(users);
	}

	@GetMapping("/users/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<ProfileDTO> getUserById(@PathVariable Long id) {
		ProfileDTO profile = userServiceImpl.getUserById(id);
		return ResponseEntity.ok(profile);
	}

	@GetMapping("/users/{userId}/name")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<String> getUserName(@PathVariable Long userId) {
		String username = userServiceImpl.getUserNameById(userId);
		return ResponseEntity.ok(username);
	}

	@PutMapping("/{userId}/avatar")
	public ResponseEntity<String> updateAvatar(@PathVariable Long userId, @RequestBody AvatarUpdateRequestDto request) {
		userServiceImpl.updateAvatar(userId, request.getAvatarUrl());
		return ResponseEntity.ok("Avatar updated successfully");
	}
	
	
	@GetMapping()
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<?> userByRole(){
		List<ProfileDTO> users = userServiceImpl.getAllUsersByRole();
		return ResponseEntity.ok(users);
	}

}
