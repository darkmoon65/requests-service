package com.crediya.requests.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("datetime", LocalDateTime.now());
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(SolicitudeValidationException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(SolicitudeValidationException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setDatetime(LocalDateTime.now());
        error.setErrors(ex.getFieldErrors());

        return ResponseEntity.badRequest().body(error);
    }
}