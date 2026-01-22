package com.user_service.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleRequest {
	@NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotBlank(message = "Role name is required")
    private String roleName;
    
    private UUID projectId;  // Nullable for system-wide roles
}
