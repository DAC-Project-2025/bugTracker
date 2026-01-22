package com.user_service.exception;

public class UserSuspendedException extends UserServiceException {
    public UserSuspendedException(String message) {
        super(message);
    }
}