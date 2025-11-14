package com.moura.bitroute.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ShortlinkGenerationException.class)
    public ResponseEntity<Map<String, Object>> handleShortlinkGenerationException(ShortlinkGenerationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(PasteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePasteNotFoundException(PasteNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(PasteExpiredException.class)
    public ResponseEntity<Map<String, Object>> handlePasteExpiredException(PasteExpiredException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.GONE);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        
        return new ResponseEntity<>(errorResponse, status);
    }
}