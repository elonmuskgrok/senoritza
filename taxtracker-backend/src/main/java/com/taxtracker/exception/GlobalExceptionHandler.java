package com.taxtracker.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String getTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    private Map<String, Object> createErrorEnvelope(HttpStatus status, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", getTimestamp());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> {
                    Map<String, String> fieldError = new HashMap<>();
                    fieldError.put("field", err.getField());
                    fieldError.put("message", err.getDefaultMessage());
                    return fieldError;
                })
                .collect(Collectors.toList());

        Map<String, Object> body = createErrorEnvelope(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI());
        body.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeExceptions(
            RuntimeException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getMessage() != null && ex.getMessage().contains("authorized")) {
            status = HttpStatus.FORBIDDEN;
        }

        Map<String, Object> body = createErrorEnvelope(status, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        
        Map<String, Object> body = createErrorEnvelope(HttpStatus.BAD_REQUEST, "The email address or password is incorrect.", request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
