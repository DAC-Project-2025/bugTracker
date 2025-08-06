package com.bugTracker.config;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtTokenValidator is a custom filter that validates JWT tokens for incoming HTTP requests.
 * It extends OncePerRequestFilter to ensure it runs once per request.
 * 
 * Responsibilities:
 * - Extract JWT token from request headers
 * - Validate and parse the JWT token using the secret key
 * - Extract user details (email and roles) from the token
 * - Set the authenticated user into Spring Security's context
 */
public class JwtTokenValidator extends OncePerRequestFilter {

    /**
     * Filters incoming requests to validate JWT tokens.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException in case of servlet exceptions
     * @throws IOException      in case of I/O exceptions
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Retrieve the JWT token from the Authorization header
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Remove 'Bearer ' prefix

            try {
                // Create secret key for HMAC signing
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

                // Parse the JWT token and extract claims
                Claims claim = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                // Extract email and authorities (roles) from the token
                String email = String.valueOf(claim.get("email"));
                String authorities = String.valueOf(claim.get("authorities")); // Fix spelling here

                // Convert comma-separated authorities to GrantedAuthority list
                List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                // Create an Authentication object and set it in the SecurityContext
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auths);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // If any exception occurs (expired, tampered token), reject request
                throw new BadCredentialsException("Invalid token");
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
