package com.leizo.admin.controller;

import com.leizo.common.entity.Users;
import com.leizo.common.repository.UserRepository;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.pojo.entity.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/admin")
public class AMLAdminController {

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired(required = false)
    private AlertRepository alertRepository;

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        try {
            if (userRepository != null) {
                return ResponseEntity.ok(userRepository.findAll());
            } else {
                // Mock response when database is not available
                List<Users> mockUsers = new ArrayList<>();
                Users mockUser = new Users();
                mockUser.setId(1);
                mockUser.setUsername("admin@example.com");
                mockUser.setRole("ADMIN");
                mockUser.setEnabled(true);
                mockUsers.add(mockUser);
                return ResponseEntity.ok(mockUsers);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        try {
            if (userRepository != null) {
                return ResponseEntity.ok(userRepository.count());
            } else {
                return ResponseEntity.ok(1L); // Mock count
            }
        } catch (Exception e) {
            return ResponseEntity.ok(0L);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AML Admin Service is running");
    }

    @GetMapping("/db-health")
    public ResponseEntity<?> dbHealth() {
        try {
            if (userRepository != null) {
                long userCount = userRepository.count();
                List<Users> users = userRepository.findAll();
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", "OK");
                response.put("message", "Database connection successful");
                response.put("userCount", userCount);
                response.put("users", users);
                
                return ResponseEntity.ok(response);
            } else {
                // Mock response when database is not available
                Map<String, Object> response = new HashMap<>();
                response.put("status", "MOCK");
                response.put("message", "Database not available - using mock data");
                response.put("userCount", 1);
                
                List<Users> mockUsers = new ArrayList<>();
                Users mockUser = new Users();
                mockUser.setId(1);
                mockUser.setUsername("admin@example.com");
                mockUser.setRole("ADMIN");
                mockUser.setEnabled(true);
                mockUsers.add(mockUser);
                response.put("users", mockUsers);
                
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Return 200 OK with error status instead of 500
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("userCount", 0);
            response.put("users", new ArrayList<>());
            response.put("reason", e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }

    // Enhanced User Management endpoints for the User Management page
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> userRequest) {
        try {
            if (userRepository != null) {
                Users user = new Users();
                user.setUsername((String) userRequest.get("email")); // Using email as username
                user.setRole((String) userRequest.get("role"));
                user.setEnabled("ACTIVE".equals(userRequest.get("status")));
                user.setCreatedAt(java.time.LocalDateTime.now());
                
                Users savedUser = userRepository.save(user);
                return ResponseEntity.status(201).body(savedUser);
            } else {
                // Mock response when database is not available
                Users mockUser = new Users();
                mockUser.setId(999);
                mockUser.setUsername((String) userRequest.get("email"));
                mockUser.setRole((String) userRequest.get("role"));
                mockUser.setEnabled("ACTIVE".equals(userRequest.get("status")));
                return ResponseEntity.status(201).body(mockUser);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> userRequest) {
        try {
            if (userRepository != null) {
                Users user = userRepository.findById(id).orElse(null);
                if (user == null) {
                    return ResponseEntity.notFound().build();
                }
                
                user.setUsername((String) userRequest.get("email"));
                user.setRole((String) userRequest.get("role"));
                user.setEnabled("ACTIVE".equals(userRequest.get("status")));
                
                Users savedUser = userRepository.save(user);
                return ResponseEntity.ok(savedUser);
            } else {
                // Mock response when database is not available
                Users mockUser = new Users();
                mockUser.setId(id);
                mockUser.setUsername((String) userRequest.get("email"));
                mockUser.setRole((String) userRequest.get("role"));
                mockUser.setEnabled("ACTIVE".equals(userRequest.get("status")));
                return ResponseEntity.ok(mockUser);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            if (userRepository != null) {
                if (!userRepository.existsById(id)) {
                    return ResponseEntity.notFound().build();
                }
                userRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
            } else {
                // Mock response when database is not available
                return ResponseEntity.ok(Map.of("message", "User deleted successfully (mock)"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusRequest) {
        try {
            if (userRepository != null) {
                Users user = userRepository.findById(id).orElse(null);
                if (user == null) {
                    return ResponseEntity.notFound().build();
                }
                
                user.setEnabled("ACTIVE".equals(statusRequest.get("status")));
                Users savedUser = userRepository.save(user);
                return ResponseEntity.ok(savedUser);
            } else {
                // Mock response when database is not available
                Users mockUser = new Users();
                mockUser.setId(id);
                mockUser.setUsername("user@example.com");
                mockUser.setRole("USER");
                mockUser.setEnabled("ACTIVE".equals(statusRequest.get("status")));
                return ResponseEntity.ok(mockUser);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to update user status: " + e.getMessage()));
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        try {
            if (alertRepository != null) {
                return ResponseEntity.ok(alertRepository.findAll());
            } else {
                // Mock response when database is not available
                return ResponseEntity.ok(new ArrayList<>());
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
