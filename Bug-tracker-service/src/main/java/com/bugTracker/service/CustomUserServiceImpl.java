package com.bugTracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bugTracker.dao.UserRepository;
import com.bugTracker.models.User;

import jakarta.transaction.Transactional;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 * This class is used to fetch user details during the authentication process.
 * It retrieves user information from the database using the {@link UserRepository}.
 *
 * <p>
 * Spring Security will automatically use this implementation when a user attempts
 * to log in, matching the provided username (email in this case) to a user in the database.
 * </p>
 * 
 * <p>
 * Annotated with {@link Service} to indicate that this is a service component,
 * and {@link Transactional} to ensure that database operations are performed within a transaction.
 * </p>
 */
@Service
@Transactional
public class CustomUserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user from the database by their email (used as username).
     * This method is called automatically by Spring Security when a login request is made.
     *
     * @param username the email of the user trying to authenticate
     * @return a {@link UserDetails} object containing user credentials
     * @throws UsernameNotFoundException if no user is found with the provided email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user by email
    	Optional<User> userOptional = userRepository.findByEmail(username);
    	User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user == null) {
            throw new UsernameNotFoundException("Invalid user");
        }

        // Create a list of GrantedAuthority (roles/permissions), currently empty
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Return Spring Security's User object with email, password, and authorities
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    }
}
