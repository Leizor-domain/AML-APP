package com.leizo.admin.auth;

import com.leizo.admin.auth.UserRequest;
import com.leizo.admin.auth.UserResponse;
import com.leizo.admin.auth.Users;
import com.leizo.repository.UserRepository;
import com.leizo.common.security.JwtUtil;
import com.leizo.service.UserService;
import com.leizo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Users user) {
        try {
            logger.info("Registration attempt for user: {}", user.getUsername());
            
            // Check if user already exists
            Optional<Users> existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser.isPresent()) {
                logger.warn("Registration failed: Username {} already exists", user.getUsername());
                return ResponseEntity.badRequest().body("Username already exists");
            }
            
            // Set default role if not provided
            if (user.getRole() == null) {
                user.setRole("VIEWER");
                logger.debug("Setting default role VIEWER for user: {}", user.getUsername());
            }
            
            // Encode password
            user.setPassword(encoder.encode(user.getPassword()));
            
            // Save user
            Users savedUser = userService.register(user);
            logger.info("User registered successfully: {} with ID: {}", savedUser.getUsername(), savedUser.getId());
            
            // Verify user was actually saved
            Optional<Users> verifyUser = userRepository.findByUsername(savedUser.getUsername());
            if (verifyUser.isPresent()) {
                logger.info("User verification successful: {} found in database", savedUser.getUsername());
                return ResponseEntity.ok(savedUser);
            } else {
                logger.error("User verification failed: {} not found in database after save", savedUser.getUsername());
                return ResponseEntity.internalServerError().body("User registration failed - verification error");
            }
            
        } catch (Exception e) {
            logger.error("Registration error for user {}: {}", user.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest request) {
        try {
            Users user = userService.findByUsername(request.getUsername());
            
            if (encoder.matches(request.getPassword(), user.getPassword())) {
                String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
                return ResponseEntity.ok(new UserResponse(token));
            }
            
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
