package com.leizo.admin.controller;

import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

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
} 