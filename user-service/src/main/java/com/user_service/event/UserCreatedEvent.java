package com.user_service.event;

import java.util.UUID;

import com.user_service.enums.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCreatedEvent extends BaseEvent{
	private UUID userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private AuthProvider authProvider;
    private boolean emailVerified;
}
