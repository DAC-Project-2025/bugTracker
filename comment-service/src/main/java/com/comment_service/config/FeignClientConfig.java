package com.comment_service.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignClientConfig implements RequestInterceptor{
	 @Override
	    public void apply(RequestTemplate template) {
	        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	        if (attrs != null) {
	            String token = attrs.getRequest().getHeader("Authorization");
	            if (token != null && !token.isBlank()) {
	                template.header("Authorization", token);
	            }
	        }
	    }
}
