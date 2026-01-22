package com.user_service.exception;

public class UnverifiedUserException extends UserServiceException {
    public UnverifiedUserException(String message) {
        super(message);
    }
}