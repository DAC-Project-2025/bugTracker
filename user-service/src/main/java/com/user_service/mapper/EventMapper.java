package com.user_service.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.user_service.event.UserCreatedEvent;
import com.user_service.event.UserDeletedEvent;
import com.user_service.event.UserRoleAssignedEvent;
import com.user_service.event.UserVerifiedEvent;
import com.user_service.models.User;
import com.user_service.models.UserRole;

@Mapper(
	    componentModel = "spring",
	    unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    imports = {UUID.class, LocalDateTime.class}
	)
// translates database entities into Kafka events.
public interface EventMapper {
	@Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventType", constant = "UserCreatedEvent")
    @Mapping(target = "aggregateId", source = "id")
    @Mapping(target = "aggregateType", constant = "User")
    @Mapping(target = "userId", source = "id")
    UserCreatedEvent toUserCreatedEvent(User user);
    
    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventType", constant = "UserVerifiedEvent")
    @Mapping(target = "aggregateId", source = "id")
    @Mapping(target = "aggregateType", constant = "User")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "verifiedAt", expression = "java(LocalDateTime.now())")
    UserVerifiedEvent toUserVerifiedEvent(User user);
    
    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventType", constant = "UserDeletedEvent")
    @Mapping(target = "aggregateId", source = "id")
    @Mapping(target = "aggregateType", constant = "User")
    @Mapping(target = "userId", source = "id")
    UserDeletedEvent toUserDeletedEvent(User user);
    
    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventType", constant = "UserRoleAssignedEvent")
    @Mapping(target = "aggregateId", source = "user.id")
    @Mapping(target = "aggregateType", constant = "User")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "roleName", source = "role.name")
    UserRoleAssignedEvent toUserRoleAssignedEvent(UserRole userRole);
}
