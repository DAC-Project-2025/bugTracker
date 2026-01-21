package com.bug_submission_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.bug_submission_service.dto.UserDto;

@FeignClient(name = "User-service", url = "")
public interface UserService {

	@GetMapping("/api/user/profile")
	public UserDto getUserProfile(@RequestHeader("Authorization") String jwt);
}
