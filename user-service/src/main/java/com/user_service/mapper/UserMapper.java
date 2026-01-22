package com.user_service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapping;

import com.user_service.dto.request.RegisterRequest;
import com.user_service.dto.response.RoleResponse;
import com.user_service.dto.response.UserResponse;
import com.user_service.models.User;
import com.user_service.models.UserRole;


public interface UserMapper {
	 // Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "authProvider", constant = "LOCAL")
    @Mapping(target = "status", constant = "PENDING_VERIFICATION")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);
    
    // Entity to Response
    @Mapping(target = "authProvider", source = "authProvider")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "roles", source = "userRoles")
    UserResponse toResponse(User user);
    
    // List mapping
    List<UserResponse> toResponseList(List<User> users);
    
    // UserRole to RoleResponse
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "projectName", ignore = true) // Will be fetched separately
    RoleResponse toRoleResponse(UserRole userRole);
    
    default List<RoleResponse> mapUserRoles(List<UserRole> userRoles) {
        if (userRoles == null) {
            return List.of();
        }
        return userRoles.stream()
            .map(this::toRoleResponse)
            .collect(Collectors.toList());
    }

}
