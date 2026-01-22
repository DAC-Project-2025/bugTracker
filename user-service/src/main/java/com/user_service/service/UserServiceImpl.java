package com.user_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.config.JwtProvider;
import com.user_service.dto.response.ProfileDTO;
import com.user_service.enums.RoleType;
import com.user_service.models.User;
import com.user_service.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UsersService{
	

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	
	@Override
	public List<ProfileDTO> getAllUsers() {
		RoleType role = RoleType.valueOf("user".toUpperCase());
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


	@Override
	public ProfileDTO getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found") );

        return modelMapper.map(user, ProfileDTO.class);
	}

	@Override
	public String getUserNameById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));

        return user.getName();
        }


	@Override
	public void updateAvatar(Long userId, String avatarUrl) {
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new RuntimeException("User not found"));

		    user.setAvatarUrl(avatarUrl);
		    userRepository.save(user);
		
	}



	@Override
	public List<ProfileDTO> getAllUsersByRole() {
	    // Assuming Role is an enum, import it properly
	    RoleType role = RoleType.USER;  // or Role.valueOf("USER");
	    
	    // Fetch users by role (assuming the repo method takes Role or String)
	    List<User> users = userRepository.findByRole(role);
	    
	    // Convert List<User> to List<ProfileDTO>
	    List<ProfileDTO> userDtos = users.stream()
	            .map(user -> modelMapper.map(user, ProfileDTO.class))
	            .collect(Collectors.toList());

	        return userDtos;
	}
	
	
	
	
	

}
