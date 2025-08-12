package com.project.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientConfig implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			Object credentials = authentication.getCredentials();
			if (credentials instanceof String jwtToken && !jwtToken.isBlank()) {
				template.header("Authorization", "Bearer " + jwtToken);
			}
		}
	}
}
