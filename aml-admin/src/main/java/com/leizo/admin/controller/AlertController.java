package com.leizo.admin.controller;

import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.admin.service.MockAlertDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANALYST') or hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<?> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String priorityLevel,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {

        try {
            logger.info("Fetching alerts with params: page={}, size={}, status={}, alertType={}, priorityLevel={}, riskLevel={}, dateFrom={}, dateTo={}",
                       page, size, status, alertType, priorityLevel, riskLevel, dateFrom, dateTo);

            // Create pageable with sorting by timestamp descending (newest first)
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

            Page<Alert> alertsPage;

            // Apply filters if provided - handle both frontend and backend parameter names
            if (status != null && !status.isEmpty()) {
                alertsPage = alertRepository.findByStatusContainingIgnoreCase(status, pageable);
            } else if (alertType != null && !alertType.isEmpty()) {
                alertsPage = alertRepository.findByAlertTypeContainingIgnoreCase(alertType, pageable);
            } else if (priorityLevel != null && !priorityLevel.isEmpty()) {
                alertsPage = alertRepository.findByPriorityLevelContainingIgnoreCase(priorityLevel, pageable);
            } else if (riskLevel != null && !riskLevel.isEmpty()) {
                alertsPage = alertRepository.findByRiskLevelContainingIgnoreCase(riskLevel, pageable);
            } else {
                // No filters - get all alerts
                alertsPage = alertRepository.findAll(pageable);
            }

            logger.info("Found {} alerts out of {} total", alertsPage.getContent().size(), alertsPage.getTotalElements());

            Map<String, Object> response = new HashMap<>();
            response.put("content", alertsPage.getContent());
            response.put("totalElements", alertsPage.getTotalElements());
            response.put("totalPages", alertsPage.getTotalPages());
            response.put("currentPage", alertsPage.getNumber());
            response.put("size", alertsPage.getSize());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching alerts: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch alerts");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testAlerts() {
        try {
            long count = alertRepository.count();
            List<Alert> sampleAlerts = alertRepository.findAll(PageRequest.of(0, 5)).getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("totalAlerts", count);
            response.put("sampleAlerts", sampleAlerts);
            response.put("message", "Alerts endpoint is working");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in test endpoint: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Test failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANALYST') or hasRole('ROLE_SUPERVISOR')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANALYST')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANALYST')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANALYST')")
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
     * Populate database with 50 mock alerts
     */
    @PostMapping("/populate-mock")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> populateMockAlerts() {
        try {
            logger.info("Received request to populate mock alerts");
            long currentCount = mockAlertDataService.getAlertCount();
            mockAlertDataService.populateMockAlerts();
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
     * Safely populate database with 50 mock alerts (clears existing first)
     */
    @PostMapping("/populate-mock-safe")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> populateMockAlertsSafely() {
        try {
            logger.info("Received request to safely populate mock alerts");
            long previousCount = mockAlertDataService.getAlertCount();
            mockAlertDataService.populateMockAlertsSafely();
            long newCount = mockAlertDataService.getAlertCount();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mock alerts safely populated (cleared existing first)");
            response.put("previousCount", previousCount);
            response.put("newCount", newCount);
            response.put("addedCount", newCount);
            logger.info("Successfully safely populated {} mock alerts", newCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to safely populate mock alerts: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to safely populate mock alerts: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Clear all alerts from database
     */
    @DeleteMapping("/clear-all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANALYST') or hasRole('ROLE_SUPERVISOR')")
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
