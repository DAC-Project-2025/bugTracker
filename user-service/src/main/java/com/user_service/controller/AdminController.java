package com.user_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.dto.request.AssignRoleRequest;
import com.user_service.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Admin user management endpoints")
public class AdminController {
	private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/assign-role")
    @Operation(summary = "Assign role to user", description = "Assign role to user for specific project or system-wide")
    public ResponseEntity<String> assignRole(
            @Valid @RequestBody AssignRoleRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        // Get admin user ID from token or database
        UUID adminId = UUID.randomUUID(); // Replace with actual admin ID extraction
        
        userService.assignRole(request, adminId);
        return ResponseEntity.ok("Role assigned successfully");
    }

    @DeleteMapping("/{userId}/projects/{projectId}")
    @Operation(summary = "Remove user from project")
    public ResponseEntity<String> removeUserFromProject(
            @PathVariable UUID userId,
            @PathVariable UUID projectId) {
        
        userService.removeUserFromProject(userId, projectId);
        return ResponseEntity.ok("User removed from project successfully");
    }

    @PutMapping("/{userId}/suspend")
    @Operation(summary = "Suspend user account")
    public ResponseEntity<String> suspendUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        
        UUID adminId = UUID.randomUUID(); // Replace with actual admin ID
        userService.suspendUser(userId, adminId);
        return ResponseEntity.ok("User suspended successfully");
    }

    @PutMapping("/{userId}/activate")
    @Operation(summary = "Activate suspended user")
    public ResponseEntity<String> activateUser(@PathVariable UUID userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok("User activated successfully");
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user (soft delete)")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
