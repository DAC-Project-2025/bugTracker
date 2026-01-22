package com.user_service.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.dto.response.AuthResponse;
import com.user_service.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final AuthenticationService authenticationService;
	private final ObjectMapper objectMapper;

	@Value("${app.cors.allowed-origins[0]}")
	private String frontendUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");
		String googleId = oAuth2User.getAttribute("sub");
		String picture = oAuth2User.getAttribute("picture");

		log.info("OAuth2 authentication successful for: {}", email);

		try {
			AuthResponse authResponse = authenticationService.handleOAuth2Success(email, name, googleId, picture);

			// Redirect to frontend with tokens
			String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
					.queryParam("token", authResponse.getAccessToken())
					.queryParam("refreshToken", authResponse.getRefreshToken()).build().toUriString();

			getRedirectStrategy().sendRedirect(request, response, targetUrl);

		} catch (Exception e) {
			log.error("OAuth2 success handler error", e);
			response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
		}
	}
}
