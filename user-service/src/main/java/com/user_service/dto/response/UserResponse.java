package com.user_service.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	 	private UUID id;
	    private String email;
	    private String fullName;
	    private String avatarUrl;
	    private String authProvider;
	    private String status;
	    private boolean emailVerified;
	    private LocalDateTime lastLogin;
	    private LocalDateTime createdAt;
	    private List<RoleResponse> roles;
}
