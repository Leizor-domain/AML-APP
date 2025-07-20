package com.leizo.admin.controller;

import com.leizo.admin.auth.Users;
import com.leizo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AMLAdminController {
    
    @Autowired
    private UserRepository userRepository;
    
    //Check server health on dedicated port
    @GetMapping("/admin/health")
    public String health() {
        return " Admin Health OK";
    }
    
    //check server status on dedicated port
    @GetMapping("/admin/status")
    public String status() {
        return " Admin Module is working!";
    }
    
    // Database health check endpoint - moved to public path
    @GetMapping("/public/db-health")
    public Map<String, Object> dbHealth() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Users> users = userRepository.findAll();
            response.put("status", "OK");
            response.put("message", "Database connection successful");
            response.put("userCount", users.size());
            response.put("users", users.stream().map(u -> Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "role", u.getRole(),
                "createdAt", u.getCreatedAt()
            )).toList());
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("userCount", 0);
            response.put("users", List.of());
        }
        return response;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Hello Admin!";
    }

    @GetMapping("/analyst")
    @PreAuthorize("hasRole('ANALYST')")
    public String analystEndpoint() {
        return "Hello Analyst!";
    }

    @GetMapping("/supervisor")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public String supervisorEndpoint() {
        return "Hello Supervisor!";
    }

    @GetMapping("/viewer")
    @PreAuthorize("hasRole('VIEWER')")
    public String viewerEndpoint() {
        return "Hello Viewer!";
    }

    @GetMapping("/shared")
    @PreAuthorize("hasAnyRole('ANALYST', 'SUPERVISOR')")
    public String sharedEndpoint() {
        return "Hello Analyst or Supervisor!";
    }
}
