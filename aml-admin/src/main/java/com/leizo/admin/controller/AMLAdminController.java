package com.leizo.admin.controller;

import com.leizo.common.entity.Users;
import com.leizo.common.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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
}
