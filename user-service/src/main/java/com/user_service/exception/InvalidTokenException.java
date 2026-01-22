package com.user_service.exception;

public class InvalidTokenException extends UserServiceException {
    public InvalidTokenException(String message) {
        super(message);
    }
}