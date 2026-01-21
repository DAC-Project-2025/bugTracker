package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.project.config.FeignClientConfig;
import com.project.dto.UserDto;

@FeignClient(name="user-service",url="http://localhost:8081", configuration = FeignClientConfig.class)

public interface UserService {
	
	@GetMapping("/api/user/profile")
	public UserDto getUserProfile(@RequestHeader("Authorization") String jwt);
}
