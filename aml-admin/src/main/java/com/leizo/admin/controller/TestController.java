package com.leizo.admin.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "pong", "status", "ok");
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "healthy",
            "timestamp", System.currentTimeMillis(),
            "service", "aml-admin"
        );
    }
}
