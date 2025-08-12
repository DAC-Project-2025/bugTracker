package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

<<<<<<< HEAD
import com.project.dto.UserDto;

@FeignClient(name="USER-SERVICE",url="http://localhost:8080")
=======
import com.project.config.FeignClientConfig;
import com.project.dto.UserDto;

@FeignClient(name="user-service",url="http://localhost:8081", configuration = FeignClientConfig.class)
>>>>>>> b5a9c0e (security changed for cors)
public interface UserService {
	
	@GetMapping("/api/user/profile")
	public UserDto getUserProfile(@RequestHeader("Authorization") String jwt);
}
