package com.user_service.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientConfig implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() instanceof String) {
            String jwtToken = (String) authentication.getCredentials();
            template.header("Authorization", "Bearer " + jwtToken);
        }
	}

}
