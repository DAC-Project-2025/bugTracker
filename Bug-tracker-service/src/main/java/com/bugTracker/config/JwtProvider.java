package com.bugTracker.config;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JwtProvider is responsible for:
 * - Generating JWT tokens after successful authentication.
 * - Parsing JWT tokens to extract user information like email.
 * - Handling user roles/authorities.
 */
public class JwtProvider {

    // Secret key for signing and verifying the JWT
    static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    /**
     * Generates a JWT token based on the authenticated user's details.
     *
     * @param auth the authenticated user.
     * @return a signed JWT token as String.
     */
    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        // Build and return JWT
        String jwt = Jwts.builder()
                .issuedAt(new Date()) // Token issue time
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .claim("email", auth.getName()) // Add user email to payload
                .claim("authorities", roles) // Add roles as comma-separated string
                .signWith(key) // Sign using the secret key
                .compact();

        return jwt;
    }

    /**
     * Extracts email from the JWT token.
     *
     * @param jwt the raw JWT string (with "Bearer " prefix).
     * @return the email contained in the token.
     */
    public static String getEmailFromJwtToken(String jwt) {
        jwt = jwt.substring(7); // Remove "Bearer " prefix

        Claims claim = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        return String.valueOf(claim.get("email"));
    }

    /**
     * Converts a collection of GrantedAuthority to a comma-separated string.
     *
     * @param collection the authorities to convert.
     * @return comma-separated roles.
     */
    public static String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
