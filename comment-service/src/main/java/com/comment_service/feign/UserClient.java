package com.comment_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.comment_service.config.FeignClientConfig;

@FeignClient(name = "Bug-tracker-service", url = "http://localhost:8081" , configuration = FeignClientConfig.class)
public interface UserClient {

	 @GetMapping("/api/user/users/{userId}/name")
	 String getUserName(@PathVariable Long userId);
}
