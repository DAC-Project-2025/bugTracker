package com.user_service.exception;

public class InvalidOtpException extends UserServiceException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
