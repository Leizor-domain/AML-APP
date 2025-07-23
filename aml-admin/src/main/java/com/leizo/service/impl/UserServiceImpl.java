package com.leizo.service.impl;

import com.leizo.admin.auth.Users;
import com.leizo.admin.repository.UserRepository;
import com.leizo.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Users register(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Users verify(String username, String rawpassword) {
        Optional<Users> user = userRepository.findByUsername(username);
        if(user.isPresent() && encoder.matches(rawpassword, user.get().getPassword())) {
            return user.orElse(null);
        }
        return null;
    }

    @Override
    public Users findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @PostConstruct
    public void ensureDefaultAdmin() {
        String defaultUsername = "amladmin";
        String defaultPassword = "admin123";
        String defaultRole = "ADMIN";
        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
            Users admin = new Users();
            admin.setUsername(defaultUsername);
            admin.setPassword(encoder.encode(defaultPassword));
            admin.setRole(defaultRole);
            admin.setEnabled(true);
            admin.setName("Default Admin");
            admin.setEmail("amladmin@example.com");
            admin.setCreatedBy("system");
            admin.setCreatedAt(java.time.LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("[BOOTSTRAP] Default admin user created: " + defaultUsername);
        }
    }
}
