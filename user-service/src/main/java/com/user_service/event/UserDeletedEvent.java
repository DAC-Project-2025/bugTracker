package com.user_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent extends BaseEvent {
	private UUID userId;
    private String email;
    private LocalDateTime deletedAt;
}
