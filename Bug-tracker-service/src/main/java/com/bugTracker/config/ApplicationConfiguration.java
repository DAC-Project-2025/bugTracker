package com.bugTracker.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

/**
 * ApplicationConfiguration class configures the Spring Security for the bug tracker application.
 * It sets up stateless session management, custom JWT token validation, CORS, CSRF, and password encoding.
 */
@Configuration
public class ApplicationConfiguration {

    /**
     * Configures the security filter chain for HTTP requests.
     * 
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if there's an issue during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable session creation (stateless API using JWT)
            .sessionManagement(management -> 
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Secure specific endpoints and allow others
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/**").authenticated()  // Protect /api/** endpoints
                    .anyRequest().permitAll()                    // Allow all other requests
            )

            // Add custom JWT token validator filter before the basic auth filter
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)

            // Disable CSRF (useful for APIs)
            .csrf(csrf -> csrf.disable())

            // Enable and configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Enable basic authentication
            .httpBasic(Customizer.withDefaults())

            // Enable form-based login (optional, depending on your app)
            .formLogin(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Defines the CORS policy for cross-origin requests.
     * 
     * @return a CorsConfigurationSource with wide-open access (for development only)
     */
    private CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration cfg = new CorsConfiguration();
                cfg.setAllowedOrigins(Collections.singletonList("*")); // Allow all origins
                cfg.setAllowedMethods(Collections.singletonList("*")); // Allow all HTTP methods
                cfg.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers
                cfg.setAllowCredentials(true); // Allow credentials (cookies, headers)
                cfg.setExposedHeaders(Arrays.asList("Authorization")); // Expose Authorization header
                cfg.setMaxAge(3600L); // Cache pre-flight response for 1 hour
                return cfg;
            }
        };
    }

    /**
     * Bean for password encoding using BCrypt.
     * Used for encoding user passwords securely.
     * 
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
