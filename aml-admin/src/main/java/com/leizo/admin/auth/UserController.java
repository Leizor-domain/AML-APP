package com.leizo.admin.auth;

import com.leizo.admin.repository.UserRepository;
import com.leizo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("VIEWER");
        }
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        Users user = userRepository.findByUsername(username).orElse(null);
        Map<String, Object> response = new HashMap<>();
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            user.setLastLoginAt(java.time.LocalDateTime.now());
            user.setLastLoginIp(request.getRemoteAddr());
            userRepository.save(user);
            System.out.println("[AUDIT] Login: " + username + " from IP: " + request.getRemoteAddr());
            response.put("success", true);
            response.put("message", "Login successful");
            Map<String, Object> userObj = new HashMap<>();
            userObj.put("userId", user.getId());
            userObj.put("username", user.getUsername());
            userObj.put("role", user.getRole());
            response.put("user", userObj);
            return ResponseEntity.ok(response);
        } else {
            System.out.println("[AUDIT] Failed login attempt: " + username + " from IP: " + request.getRemoteAddr());
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean status
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Users> userPage;
        if (status != null) {
            userPage = userRepository.findByEnabled(status, pageable);
        } else if (search != null && !search.isEmpty()) {
            userPage = userRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        Users user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Users updatedUser) {
        Users user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setRole(updatedUser.getRole());
        user.setEnabled(updatedUser.isEnabled());
        user.setEmail(updatedUser.getEmail());
        user.setName(updatedUser.getName());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setUserStatus(@PathVariable Integer id, @RequestParam boolean enabled) {
        Users user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setEnabled(enabled);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> softDeleteUser(@PathVariable Integer id) {
        Users user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/login-history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserLoginHistory(@PathVariable Integer id) {
        Users user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        Map<String, Object> history = new HashMap<>();
        history.put("lastLoginAt", user.getLastLoginAt());
        history.put("lastLoginIp", user.getLastLoginIp());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/role-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRoleDistribution() {
        List<Object[]> counts = userRepository.countUsersByRole();
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : counts) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUsersToCsv() {
        List<Users> users = userRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Username,Role,Email,Name,CreatedAt,Enabled\n");
        for (Users u : users) {
            sb.append(u.getUsername()).append(",")
              .append(u.getRole()).append(",")
              .append(u.getEmail() != null ? u.getEmail() : "").append(",")
              .append(u.getName() != null ? u.getName() : "").append(",")
              .append(u.getCreatedAt() != null ? u.getCreatedAt() : "").append(",")
              .append(u.isEnabled() ? "Active" : "Disabled").append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    // Utility endpoint for admin: delete users with plain text passwords (for cleanup)
    @DeleteMapping("/admin/cleanup-plaintext-users")
    public ResponseEntity<?> cleanupPlaintextUsers() {
        List<Users> allUsers = userRepository.findAll();
        int deleted = 0;
        for (Users u : allUsers) {
            if (!u.getPassword().startsWith("$2a$")) {
                userRepository.delete(u);
                deleted++;
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", deleted);
        response.put("message", "Plain text users deleted");
        return ResponseEntity.ok(response);
    }
}
