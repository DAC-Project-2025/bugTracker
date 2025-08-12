package com.user_service.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user_service.dao.UserRepository;
import com.user_service.models.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomUserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	User user = userRepository.findByEmail(username)
    	        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    	    return new CustomUserDetails(
    	        user,
    	        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    	    );
    }
}
