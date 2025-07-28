package com.leizo.portal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    
    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object message = request.getAttribute("javax.servlet.error.message");
        Object path = request.getAttribute("javax.servlet.error.request_uri");
        
        logger.warn("Error occurred - Status: {}, Message: {}, Path: {}", status, message, path);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", message != null ? message.toString() : "An error occurred");
        response.put("path", path != null ? path.toString() : "Unknown");
        response.put("timestamp", LocalDateTime.now().toString());
        
        // Map status codes to appropriate HTTP status
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            response.put("statusCode", statusCode);
            
            if (statusCode == 404) {
                response.put("status", "NOT_FOUND");
                response.put("message", "Endpoint not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (statusCode == 403) {
                response.put("status", "FORBIDDEN");
                response.put("message", "Access denied");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            } else if (statusCode == 401) {
                response.put("status", "UNAUTHORIZED");
                response.put("message", "Authentication required");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else if (statusCode >= 500) {
                response.put("status", "INTERNAL_ERROR");
                response.put("message", "Internal server error");
                // Return 200 OK to prevent client crashes
                return ResponseEntity.ok(response);
            }
        }
        
        // Default fallback - return 200 OK with error status
        return ResponseEntity.ok(response);
    }
} 