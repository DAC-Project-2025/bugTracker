package com.project_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.project_service.config.FeignClientConfig;
import com.project_service.dto.UserDTO;


@FeignClient(name = "user-service", url = "http://localhost:8081/api/user", configuration = FeignClientConfig.class)
public interface UserClient {
	@GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long userId, @RequestHeader("Authorization") String token);
}
