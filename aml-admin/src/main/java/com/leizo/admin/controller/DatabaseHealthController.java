package com.leizo.admin.controller;

import com.leizo.admin.repository.UserRepository;
import com.leizo.admin.repository.TransactionRepository;
import com.leizo.admin.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/db")
public class DatabaseHealthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkDatabaseHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection by counting records
            long userCount = userRepository.count();
            long transactionCount = transactionRepository.count();
            long alertCount = alertRepository.count();
            
            response.put("status", "OK");
            response.put("message", "Database connection successful");
            response.put("data", Map.of(
                "users_count", userCount,
                "transactions_count", transactionCount,
                "alerts_count", alertCount,
                "database_type", "PostgreSQL",
                "connection_time", System.currentTimeMillis()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getTableInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> tables = new HashMap<>();
            
            // Get table statistics
            tables.put("users", Map.of(
                "count", userRepository.count(),
                "table_name", "users",
                "description", "User accounts and authentication"
            ));
            
            tables.put("transactions", Map.of(
                "count", transactionRepository.count(),
                "table_name", "transactions", 
                "description", "Financial transactions and transfers"
            ));
            
            tables.put("alerts", Map.of(
                "count", alertRepository.count(),
                "table_name", "alerts",
                "description", "AML alerts and suspicious activity reports"
            ));
            
            response.put("status", "OK");
            response.put("tables", tables);
            response.put("total_tables", 3);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to get table information: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 