package com.leizo.admin.controller;

import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.admin.service.MockAlertDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private MockAlertDataService mockAlertDataService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAlertById(@PathVariable Integer id) {
        Optional<Alert> alert = alertRepository.findById(id);
        if (alert.isPresent()) {
            return ResponseEntity.ok(alert.get());
        } else {
            Map<String, Object> resp = new HashMap<>();
            resp.put("error", "Alert not found");
            return ResponseEntity.status(404).body(resp);
        }
    }

    @PatchMapping("/{id}/dismiss")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> dismissAlert(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setReason(body.getOrDefault("reason", "Dismissed by admin"));
            alertRepository.save(alert);
            return ResponseEntity.ok(alert);
        } else {
            Map<String, Object> resp = new HashMap<>();
            resp.put("error", "Alert not found");
            return ResponseEntity.status(404).body(resp);
        }
    }

    @PatchMapping("/{id}/false-positive")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> tagAsFalsePositive(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setReason(body.getOrDefault("reason", "Marked as false positive"));
            alertRepository.save(alert);
            return ResponseEntity.ok(alert);
        } else {
            Map<String, Object> resp = new HashMap<>();
            resp.put("error", "Alert not found");
            return ResponseEntity.status(404).body(resp);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateAlertStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            String status = body.get("status");
            if (status != null) {
                alert.setPriorityLevel(status);
                alertRepository.save(alert);
                return ResponseEntity.ok(alert);
            } else {
                Map<String, Object> resp = new HashMap<>();
                resp.put("error", "Missing status in request body");
                return ResponseEntity.badRequest().body(resp);
            }
        } else {
            Map<String, Object> resp = new HashMap<>();
            resp.put("error", "Alert not found");
            return ResponseEntity.status(404).body(resp);
        }
    }

    /**
     * Populate database with 70 mock alerts
     */
    @PostMapping("/populate-mock")
    public ResponseEntity<?> populateMockAlerts() {
        try {
            logger.info("Received request to populate mock alerts");
            
            // Get current alert count
            long currentCount = mockAlertDataService.getAlertCount();
            
            // Populate mock alerts
            mockAlertDataService.populateMockAlerts();
            
            // Get new count
            long newCount = mockAlertDataService.getAlertCount();
            long addedCount = newCount - currentCount;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mock alerts populated successfully");
            response.put("previousCount", currentCount);
            response.put("newCount", newCount);
            response.put("addedCount", addedCount);
            
            logger.info("Successfully populated {} mock alerts. Total alerts: {}", addedCount, newCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to populate mock alerts: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to populate mock alerts: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Clear all alerts from database
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<?> clearAllAlerts() {
        try {
            logger.info("Received request to clear all alerts");
            
            long currentCount = mockAlertDataService.getAlertCount();
            mockAlertDataService.clearMockAlerts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All alerts cleared successfully");
            response.put("clearedCount", currentCount);
            
            logger.info("Successfully cleared {} alerts", currentCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to clear alerts: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to clear alerts: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get current alert count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getAlertCount() {
        try {
            long count = mockAlertDataService.getAlertCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get alert count: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get alert count: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 