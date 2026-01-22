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
public abstract class BaseEvent {
	private UUID eventId;
    private LocalDateTime timestamp;
    private String eventType;
    private UUID aggregateId;
    private String aggregateType;
}
