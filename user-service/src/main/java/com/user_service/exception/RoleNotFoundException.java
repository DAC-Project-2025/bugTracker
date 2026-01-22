package com.user_service.exception;

public class RoleNotFoundException extends UserServiceException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
