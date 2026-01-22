package com.user_service.exception;

public class EmailAlreadyExistsException extends UserServiceException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}