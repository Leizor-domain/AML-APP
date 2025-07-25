package com.leizo.admin.auth;

import com.leizo.common.repository.UserRepository;
import com.leizo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import com.leizo.common.entity.Users;
import com.leizo.common.security.JwtUtil;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // In-memory refresh token store (for demo; use persistent store in production)
    private final ConcurrentHashMap<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            Users user = userRepository.findByUsername(username).orElse(null);
            Map<String, Object> resp = new HashMap<>();
            if (user != null && user.isEnabled() && passwordEncoder.matches(password, user.getPassword())) {
                user.setLastLoginAt(java.time.LocalDateTime.now());
                user.setLastLoginIp(request.getRemoteAddr());
                userRepository.save(user);
                System.out.println("[AUDIT] Login: " + username + " from IP: " + request.getRemoteAddr());
                // Generate JWT access token
                String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
                // Generate refresh token (simple UUID for demo)
                String refreshToken = java.util.UUID.randomUUID().toString();
                refreshTokenStore.put(refreshToken, user.getUsername());
                // Set refresh token as HttpOnly cookie
                Cookie cookie = new Cookie("refreshToken", refreshToken);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                response.addCookie(cookie);
                resp.put("success", true);
                resp.put("message", "Login successful");
                resp.put("token", accessToken);
                Map<String, Object> userObj = new HashMap<>();
                userObj.put("userId", user.getId());
                userObj.put("username", user.getUsername());
                userObj.put("role", user.getRole());
                resp.put("user", userObj);
                return ResponseEntity.ok(resp);
            } else {
                resp.put("success", false);
                if (user != null && !user.isEnabled()) {
                    resp.put("message", "User account is disabled");
                } else {
                    resp.put("message", "Invalid username or password");
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No refresh token provided", "code", 401));
        }
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        if (refreshToken == null || !refreshTokenStore.containsKey(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token", "code", 401));
        }
        String username = refreshTokenStore.get(refreshToken);
        Users user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found", "code", 401));
        }
        String newAccessToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(Map.of("token", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshTokenStore.remove(cookie.getValue());
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing or invalid Authorization header", "code", 401));
        }
        String token = authHeader.substring(7);
        try {
            var claims = jwtUtil.validateToken(token);
            String username = claims.getSubject();
            Users user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found", "code", 401));
            }
            return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole(),
                "email", user.getEmail(),
                "name", user.getName(),
                "createdAt", user.getCreatedAt(),
                "lastLoginAt", user.getLastLoginAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token", "code", 401));
        }
    }

    @GetMapping("/auth/health")
    public ResponseEntity<String> authHealth() {
        return ResponseEntity.ok("OK");
    }
}
