package com.user_service.event;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent extends BaseEvent{
	private UUID userId;
    private String email;
    private Map<String, Object> changedFields;
}
