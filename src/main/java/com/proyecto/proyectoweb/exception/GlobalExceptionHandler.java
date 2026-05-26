package com.proyecto.proyectoweb.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

   
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";


    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(Map.of(
                TIMESTAMP, LocalDateTime.now().toString(),
                STATUS, status.value(),
                ERROR, error,
                MESSAGE, message
        ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }
}