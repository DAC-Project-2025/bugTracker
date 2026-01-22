package com.user_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class UserLoginEvent extends BaseEvent {
	private UUID userId;
    private String email;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginAt;
    private boolean success;
}
