package com.user_service.exception;

public class InsufficientPermissionException extends UserServiceException {
    public InsufficientPermissionException(String message) {
        super(message);
    }
}