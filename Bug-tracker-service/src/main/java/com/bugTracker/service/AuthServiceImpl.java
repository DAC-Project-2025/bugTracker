package com.bugTracker.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bugTracker.config.JwtProvider;
import com.bugTracker.dto.LoginRequestDTO;
import com.bugTracker.dto.SignupRequestDTO;
import com.bugTracker.models.User;
import com.bugTracker.response.AuthResponse;

@Service
public class AuthServiceImpl implements AuthService {
	
	 @Autowired
	    private UserRepository userRepository;

	 @Autowired
	   	private PasswordEncoder passwordEncoder;
	 
	 @Autowired
	    private ModelMapper modelMapper;
	 
	 @Autowired
	 	private CustomUserServiceImpl customUserDetails;
	 
	 

	@Override
	public AuthResponse registerUser(SignupRequestDTO request) throws Exception {
		if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new Exception("Email Already Exist");
        }

        // Map DTO to Entity
        User newUser = modelMapper.map(request, User.class);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            request.getEmail(), request.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        // can also map entity to response if needed
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Registration success");
        authResponse.setStatus(true);

        return authResponse;
	}

	@Override
	public Authentication authenticate(String username, String password) throws Exception {
		UserDetails userDetails = customUserDetails.loadUserByUsername(username);
		
		if(userDetails==null) {
			throw new BadCredentialsException("Invalid username and password");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid username and password");
		}
		return new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
	}

	@Override
	public AuthResponse LoginUser(LoginRequestDTO request) throws Exception {
		// TODO Auto-generated method stub
		String username = request.getEmail();
		String password = request.getPassword();
		
		System.out.println("username "+username+"\n password "+password);
		
		Authentication authentication = authenticate(username,password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = JwtProvider.generateToken(authentication);
		
	     AuthResponse authResponse = new AuthResponse();
	     
	     authResponse.setMessage("Login Success");
	     authResponse.setJwt(token);
	     
	     authResponse.setStatus(true);
		
		return authResponse;
	}

}
