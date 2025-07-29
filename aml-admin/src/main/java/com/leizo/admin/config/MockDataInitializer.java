package com.leizo.admin.config;

import com.leizo.admin.service.MockAlertDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component to initialize mock data at application startup
 */
@Component
public class MockDataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(MockDataInitializer.class);
    
    @Autowired
    private MockAlertDataService mockAlertDataService;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting mock data initialization...");
        
        try {
            // Initialize mock alerts if database is empty
            mockAlertDataService.initializeMockAlertsIfEmpty();
            
            logger.info("Mock data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize mock data: {}", e.getMessage(), e);
            // Don't throw exception to prevent application startup failure
        }
    }
}