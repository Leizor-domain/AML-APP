package com.leizo.admin.service;

import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
public class AlertBootstrapService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertBootstrapService.class);
    private static final String BOOTSTRAP_FILE = "data/bootstrap-alerts.json";
    
    @Autowired
    private AlertRepository alertRepository;
    
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Mock data arrays for realistic alert generation
    private final String[] mockSenders = {
        "John Smith", "Maria Garcia", "Ahmed Hassan", "Li Wei", "Sarah Johnson",
        "Carlos Rodriguez", "Anna Kowalski", "Mohammed Al-Rashid", "Emma Wilson",
        "David Chen", "Fatima Zahra", "James Brown", "Sofia Petrov", "Michael Lee",
        "Aisha Patel", "Robert Taylor", "Yuki Tanaka", "Jennifer Davis", "Ali Reza",
        "Lisa Anderson", "Hassan Mahmoud", "Amanda White", "Omar Khalil", "Rachel Green",
        "Tariq Aziz", "Jessica Martinez", "Khalid Al-Zahra", "Nicole Thompson",
        "Abdullah Rahman", "Stephanie Clark", "Mehmet Yilmaz", "Ashley Lewis",
        "Rashid Al-Mansouri", "Brittany Hall", "Hussein Ali", "Megan Turner",
        "Karim El-Sayed", "Lauren Scott", "Nasser Al-Qahtani", "Victoria Adams",
        "Tarek Hassan", "Samantha Baker", "Ziad Khalil", "Amber Carter",
        "Faisal Al-Rashid", "Danielle Evans", "Malik Johnson", "Courtney Foster",
        "Rami El-Hassan", "Tiffany Garcia", "Samir Al-Zahra", "Monica Rodriguez"
    };
    
    private final String[] mockReceivers = {
        "Global Trading Corp", "International Bank Ltd", "Merchant Solutions Inc",
        "Cross-Border Exchange", "Digital Payment Systems", "Financial Services Group",
        "International Remittance", "Global Transfer Co", "Secure Payment Network",
        "International Commerce", "Digital Banking Solutions", "Global Finance Ltd",
        "Cross-Border Services", "International Exchange", "Digital Remittance",
        "Global Payment Systems", "International Transfer", "Secure Banking Corp",
        "Digital Commerce Ltd", "Global Exchange Network", "International Solutions",
        "Cross-Border Banking", "Digital Transfer Co", "Global Remittance Ltd",
        "International Payment", "Secure Exchange Corp", "Digital Banking Network",
        "Global Commerce Ltd", "International Transfer Co", "Cross-Border Solutions",
        "Digital Finance Corp", "Global Banking Network", "International Exchange Ltd",
        "Secure Transfer Co", "Digital Commerce Corp", "Global Payment Network",
        "International Banking", "Cross-Border Exchange", "Digital Remittance Corp",
        "Global Transfer Network", "International Solutions Ltd", "Secure Commerce Co",
        "Digital Exchange Corp", "Global Banking Solutions", "International Network",
        "Cross-Border Transfer", "Digital Payment Corp", "Global Finance Network",
        "International Commerce Ltd", "Secure Banking Solutions", "Digital Transfer Network"
    };
    
    private final String[] mockCountries = {
        "USA", "Canada", "UK", "Germany", "France", "Spain", "Italy", "Netherlands",
        "Belgium", "Switzerland", "Austria", "Sweden", "Norway", "Denmark", "Finland",
        "Poland", "Czech Republic", "Hungary", "Romania", "Bulgaria", "Greece",
        "Portugal", "Ireland", "Luxembourg", "Slovenia", "Croatia", "Slovakia",
        "Estonia", "Latvia", "Lithuania", "Malta", "Cyprus", "Iceland", "Liechtenstein",
        "Monaco", "San Marino", "Vatican City", "Andorra", "Malta", "Cyprus"
    };
    
    private final String[] mockAlertReasons = {
        "High value transaction exceeding threshold",
        "Suspicious transaction pattern detected",
        "Entity matched against sanctions list",
        "Unusual frequency of transactions",
        "High-risk country transfer",
        "Structuring behavior detected",
        "PEP (Politically Exposed Person) transaction",
        "Suspicious description keywords",
        "Manual flag raised by analyst",
        "Risk score exceeded threshold",
        "Unusual transaction timing",
        "Cross-border transfer to high-risk jurisdiction",
        "Large cash transaction",
        "Suspicious beneficiary relationship",
        "Transaction amount inconsistent with profile",
        "Geographic risk factor",
        "Currency risk indicator",
        "Account behavior anomaly",
        "Network analysis alert",
        "Compliance rule violation"
    };
    
    private final String[] mockAlertTypes = {
        "HIGH_VALUE", "SANCTIONS", "STRUCTURING", "PEP", "MANUAL_FLAG",
        "RISK_SCORE", "BEHAVIORAL", "GEOGRAPHIC", "CURRENCY", "NETWORK"
    };
    
    private final String[] mockPriorityLevels = {
        "HIGH", "MEDIUM", "LOW"
    };
    
    private final String[] mockMatchedLists = {
        "OFAC SDN List", "UN Sanctions", "EU Sanctions", "Local Sanctions",
        "PEP Database", "High-Risk Countries", "FATF Grey List", "Internal Watchlist"
    };
    
    private final String[] mockMatchReasons = {
        "Exact name match", "Fuzzy name match", "Address match", "Date of birth match",
        "Phone number match", "Email address match", "Bank account match",
        "Transaction pattern match", "Geographic location match", "Behavioral pattern match"
    };

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Alert Bootstrap Service...");
        
        // Check if bootstrap is needed
        if (!shouldBootstrap()) {
            logger.info("Bootstrap not needed. Skipping alert creation.");
            return;
        }
        
        logger.info("Creating 50 bootstrap alerts...");
        
        // Create 50 mock alerts
        for (int i = 1; i <= 50; i++) {
            Alert alert = createBootstrapAlert(i);
            alertRepository.save(alert);
        }
        
        // Update bootstrap status
        updateBootstrapStatus();
        
        logger.info("Successfully created 50 bootstrap alerts.");
    }
    
    private boolean shouldBootstrap() {
        try {
            // Check if alerts already exist in database
            long existingCount = alertRepository.count();
            if (existingCount > 0) {
                logger.info("Alerts already exist in database ({} alerts). Skipping bootstrap.", existingCount);
                return false;
            }
            
            // Check bootstrap status file
            Path bootstrapPath = getBootstrapFilePath();
            if (Files.exists(bootstrapPath)) {
                ObjectNode bootstrapData = objectMapper.readValue(bootstrapPath.toFile(), ObjectNode.class);
                String lastBootstrap = bootstrapData.get("lastBootstrap").asText(null);
                if (lastBootstrap != null) {
                    logger.info("Bootstrap already completed on: {}. Skipping.", lastBootstrap);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            logger.warn("Error checking bootstrap status: {}. Proceeding with bootstrap.", e.getMessage());
            return true;
        }
    }
    
    private void updateBootstrapStatus() {
        try {
            Path bootstrapPath = getBootstrapFilePath();
            ObjectNode bootstrapData = objectMapper.createObjectNode();
            bootstrapData.put("bootstrapEnabled", true);
            bootstrapData.put("alertCount", 50);
            bootstrapData.put("lastBootstrap", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            bootstrapData.putArray("alerts");
            
            // Ensure directory exists
            Files.createDirectories(bootstrapPath.getParent());
            
            // Write bootstrap status
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(bootstrapPath.toFile(), bootstrapData);
            
            logger.info("Bootstrap status updated successfully.");
        } catch (Exception e) {
            logger.error("Failed to update bootstrap status: {}", e.getMessage());
        }
    }
    
    private Path getBootstrapFilePath() {
        return Paths.get("data", "bootstrap-alerts.json");
    }
    
    private Alert createBootstrapAlert(int alertId) {
        Alert alert = new Alert();
        
        // Set basic alert information
        alert.setId(alertId);
        alert.setAlertId("ALERT-" + String.format("%06d", alertId));
        alert.setTransactionId(random.nextInt(10000) + 1000);
        
        // Set sender and receiver
        String sender = getRandomElement(mockSenders);
        String receiver = getRandomElement(mockReceivers);
        String country = getRandomElement(mockCountries);
        
        // Set reason based on alert type
        String alertType = getRandomElement(mockAlertTypes);
        String reason = getRandomElement(mockAlertReasons);
        
        // Create detailed reason with transaction details
        String detailedReason = String.format("%s - Transaction from %s to %s in %s", 
            reason, sender, receiver, country);
        
        alert.setReason(detailedReason);
        alert.setAlertType(alertType);
        
        // Set timestamp (within last 30 days, newest first)
        LocalDateTime baseTime = LocalDateTime.now();
        LocalDateTime alertTime = baseTime.minus(random.nextInt(30), ChronoUnit.DAYS)
                                        .minus(random.nextInt(24), ChronoUnit.HOURS)
                                        .minus(random.nextInt(60), ChronoUnit.MINUTES);
        alert.setTimestamp(alertTime);
        
        // Set priority information
        String priorityLevel = getRandomElement(mockPriorityLevels);
        alert.setPriorityLevel(priorityLevel);
        
        // Set priority score based on level
        int priorityScore;
        switch (priorityLevel) {
            case "HIGH":
                priorityScore = random.nextInt(30) + 70; // 70-100
                break;
            case "MEDIUM":
                priorityScore = random.nextInt(30) + 40; // 40-70
                break;
            default:
                priorityScore = random.nextInt(30) + 10; // 10-40
        }
        alert.setPriorityScore(priorityScore);
        
        // Set sanctions-related fields for sanctions alerts
        if ("SANCTIONS".equals(alertType)) {
            alert.setMatchedEntityName(sender);
            alert.setMatchedList(getRandomElement(mockMatchedLists));
            alert.setMatchReason(getRandomElement(mockMatchReasons));
        }
        
        return alert;
    }
    
    private <T> T getRandomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }
}