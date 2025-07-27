package com.leizo.admin.controller;

import com.leizo.common.entity.Users;
import com.leizo.common.repository.UserRepository;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.admin.entity.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AMLAdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userRepository.count());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AML Admin Service is running");
    }

    @GetMapping("/db-health")
    public ResponseEntity<?> dbHealth() {
        try {
            long userCount = userRepository.count();
            List<Users> users = userRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("message", "Database connection successful");
            response.put("userCount", userCount);
            response.put("users", users);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("userCount", 0);
            response.put("users", new ArrayList<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    // Enhanced User Management endpoints for the User Management page
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> userRequest) {
        try {
            Users user = new Users();
            user.setUsername((String) userRequest.get("email")); // Using email as username
            user.setRole((String) userRequest.get("role"));
            user.setEnabled("ACTIVE".equals(userRequest.get("status")));
            user.setCreatedAt(java.time.LocalDateTime.now());
            
            Users savedUser = userRepository.save(user);
            return ResponseEntity.status(201).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> userRequest) {
        try {
            Users user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            user.setUsername((String) userRequest.get("email"));
            user.setRole((String) userRequest.get("role"));
            user.setEnabled("ACTIVE".equals(userRequest.get("status")));
            
            Users savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusRequest) {
        try {
            Users user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            user.setEnabled("ACTIVE".equals(statusRequest.get("status")));
            Users savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to update user status: " + e.getMessage()));
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }
}
