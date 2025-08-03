package com.project_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project_service.dto.UserDTO;


@FeignClient(name = "auth-service", url = "http://localhost:8081/api/users")
public interface UserClient {
	@GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long userId);
}
