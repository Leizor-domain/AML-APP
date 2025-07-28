package com.leizo.admin.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle all uncaught exceptions and return 200 OK with error status
     * This prevents 500 errors from reaching the client
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "An unexpected error occurred");
        response.put("error", ex.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        // Return 200 OK instead of 500 to prevent client-side crashes
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle validation exceptions from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
                ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "VALIDATION_ERROR");
        response.put("message", "Validation failed");
        response.put("fieldErrors", fieldErrors);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "VALIDATION_ERROR");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle file upload size exceeded
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, WebRequest request) {
        logger.warn("File upload size exceeded: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FILE_TOO_LARGE");
        response.put("message", "File size exceeds maximum allowed limit");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle 404 errors
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
        logger.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "NOT_FOUND");
        response.put("message", "Endpoint not found");
        response.put("path", ex.getRequestURL());
        response.put("method", ex.getHttpMethod());
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * Handle database connection errors
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(org.springframework.dao.DataAccessException ex, WebRequest request) {
        logger.error("Database access error: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "DATABASE_ERROR");
        response.put("message", "Database operation failed");
        response.put("error", "Database temporarily unavailable");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        // Return 200 OK instead of 500
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle JSON parsing errors
     */
    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleJsonProcessingException(com.fasterxml.jackson.core.JsonProcessingException ex, WebRequest request) {
        logger.error("JSON processing error: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "INVALID_JSON");
        response.put("message", "Invalid JSON format");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle null pointer exceptions
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex, WebRequest request) {
        logger.error("Null pointer exception: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "INTERNAL_ERROR");
        response.put("message", "Internal processing error");
        response.put("error", "Service temporarily unavailable");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        // Return 200 OK instead of 500
        return ResponseEntity.ok(response);
    }
} 