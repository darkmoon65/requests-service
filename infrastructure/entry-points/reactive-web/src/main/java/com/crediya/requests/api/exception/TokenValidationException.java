package com.crediya.requests.api.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenValidationException extends AuthenticationException {
    public TokenValidationException() {
        super("Unauthorized");
    }


    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}