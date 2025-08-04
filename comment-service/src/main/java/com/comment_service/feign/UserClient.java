package com.comment_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", url = "")
public interface UserClient {

	 @GetMapping("/users/{userId}/name")
	 String getUserNameById(@PathVariable Long userId);
}
