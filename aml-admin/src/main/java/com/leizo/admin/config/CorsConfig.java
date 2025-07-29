package com.leizo.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // Production frontend (main React app)
            .allowedOriginPatterns(
                "https://aml-app.onrender.com",         // Main app (prod)
                "https://aml-portal.onrender.com",      // Portal (prod)
                "https://aml-admin.onrender.com",       // Admin (prod, if accessed from browser)
                // Local development
                "http://localhost:3000",                // React dev server
                "http://localhost:5173",                // Vite dev server
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization", "Content-Type")
            .allowCredentials(true);
    }
}
