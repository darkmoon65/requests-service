package com.crediya.requests.api.exception;

import java.util.Map;

public class SolicitudeValidationException extends RuntimeException  {
    private final Map<String, String> fieldErrors;

    public SolicitudeValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
