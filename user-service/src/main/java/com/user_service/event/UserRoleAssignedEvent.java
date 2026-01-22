package com.user_service.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAssignedEvent extends BaseEvent {
	private UUID userId;
    private String email;
    private String roleName;
    private UUID projectId;
    private UUID assignedBy;
}
