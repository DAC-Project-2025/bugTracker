package com.bugTracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bugTracker.config.JwtProvider;
import com.bugTracker.dao.UserRepository;
import com.bugTracker.dto.ProfileDTO;
import com.bugTracker.enums.Role;
import com.bugTracker.models.User;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UsersService{
	

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	
	@Override
	public List<ProfileDTO> getAllUsers() {
		Role role = Role.valueOf("user".toUpperCase());
		List<User> users = userRepository.findByRole(role);
		
		return users.stream()
				.map(user->modelMapper.map(user, ProfileDTO.class))
				.collect(Collectors.toList());
	}


	@Override
	public ProfileDTO getUserProfile(String jwt) {
			
		 String email = JwtProvider.getEmailFromJwtToken(jwt);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		return modelMapper.map(user, ProfileDTO.class);
	}
	
	

}
