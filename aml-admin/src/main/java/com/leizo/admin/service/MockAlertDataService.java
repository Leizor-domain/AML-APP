package com.leizo.admin.service;

import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MockAlertDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockAlertDataService.class);
    
    @Autowired
    private AlertRepository alertRepository;
    
    private final Random random = new Random();
    
    // Mock entity names for alerts
    private final String[] mockEntityNames = {
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
    
    // Mock countries for alerts
    private final String[] mockCountries = {
        "USA", "Canada", "UK", "Germany", "France", "Spain", "Italy", "Netherlands",
        "Belgium", "Switzerland", "Austria", "Sweden", "Norway", "Denmark", "Finland",
        "Poland", "Czech Republic", "Hungary", "Romania", "Bulgaria", "Greece",
        "Portugal", "Ireland", "Luxembourg", "Slovenia", "Croatia", "Slovakia",
        "Estonia", "Latvia", "Lithuania", "Malta", "Cyprus", "Iceland", "Liechtenstein",
        "Monaco", "San Marino", "Vatican City", "Andorra", "Malta", "Cyprus"
    };
    
    // Mock alert reasons with realistic AML conditions
    private final String[] mockAlertReasons = {
        "High value transaction exceeding $50,000 threshold",
        "Suspicious transaction pattern detected - multiple small transfers",
        "Entity matched against OFAC SDN sanctions list",
        "Unusual frequency of transactions - 15+ in 24 hours",
        "High-risk country transfer to Iran",
        "Structuring behavior detected - amounts under $10,000",
        "PEP (Politically Exposed Person) transaction identified",
        "Suspicious description keywords: 'urgent', 'confidential'",
        "Manual flag raised by analyst for review",
        "Risk score exceeded 85% threshold",
        "Unusual transaction timing - 3 AM transfers",
        "Cross-border transfer to high-risk jurisdiction",
        "Large cash transaction - $25,000 in cash",
        "Suspicious beneficiary relationship detected",
        "Transaction amount inconsistent with customer profile",
        "Geographic risk factor - sanctioned country",
        "Currency risk indicator - multiple currency exchanges",
        "Account behavior anomaly - sudden activity increase",
        "Network analysis alert - connected to known suspicious entities",
        "Compliance rule violation - missing documentation"
    };
    
    // Mock alert types
    private final String[] mockAlertTypes = {
        "HIGH_VALUE", "SANCTIONS", "STRUCTURING", "PEP", "MANUAL_FLAG",
        "RISK_SCORE", "BEHAVIORAL", "GEOGRAPHIC", "CURRENCY", "NETWORK"
    };
    
    // Mock priority levels
    private final String[] mockPriorityLevels = {
        "HIGH", "MEDIUM", "LOW"
    };
    
    // Mock matched lists
    private final String[] mockMatchedLists = {
        "OFAC SDN List", "UN Sanctions", "EU Sanctions", "Local Sanctions",
        "PEP Database", "High-Risk Countries", "FATF Grey List", "Internal Watchlist"
    };
    
    // Mock match reasons
    private final String[] mockMatchReasons = {
        "Exact name match", "Fuzzy name match (85% similarity)", "Address match", "Date of birth match",
        "Phone number match", "Email address match", "ID document match", "Bank account match",
        "IP address match", "Device fingerprint match", "Transaction pattern match", "Geographic location match"
    };

    /**
     * Populate database with 50 mock alerts for testing
     */
    public void populateMockAlerts() {
        try {
            logger.info("Starting to populate 50 mock alerts");
            
            List<Alert> alerts = new ArrayList<>();
            LocalDateTime baseTime = LocalDateTime.now().minusDays(7); // Start from 7 days ago
            
            for (int i = 1; i <= 50; i++) {
                Alert alert = createMockAlert(i, baseTime);
                alerts.add(alert);
                
                // Increment time by random intervals
                baseTime = baseTime.plusMinutes(random.nextInt(180) + 30); // 30-210 minutes
            }
            
            alertRepository.saveAll(alerts);
            logger.info("Successfully populated {} mock alerts", alerts.size());
            
        } catch (Exception e) {
            logger.error("Failed to populate mock alerts: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to populate mock alerts", e);
        }
    }

    /**
     * Create a single mock alert with realistic data
     */
    private Alert createMockAlert(int alertId, LocalDateTime baseTime) {
        Alert alert = new Alert();
        
        // Basic alert information
        alert.setId(alertId);
        alert.setAlertId("ALERT-" + String.format("%06d", alertId));
        alert.setMatchedEntityName(getRandomElement(mockEntityNames));
        alert.setMatchedList(getRandomElement(mockMatchedLists));
        alert.setMatchReason(getRandomElement(mockMatchReasons));
        
        // Alert type and priority
        String alertType = getRandomElement(mockAlertTypes);
        alert.setAlertType(alertType);
        
        // Priority score and level
        int priorityScore = random.nextInt(100) + 1;
        alert.setPriorityScore(priorityScore);
        alert.updatePriorityLevel(); // This will set priority level based on score
        
        // Transaction information
        alert.setTransactionId(random.nextInt(1000) + 1);
        
        // Reason and timestamp
        alert.setReason(getRandomElement(mockAlertReasons));
        alert.setTimestamp(baseTime);
        
        return alert;
    }

    /**
     * Get random element from array
     */
    private <T> T getRandomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }

    /**
     * Clear all alerts from database
     */
    public void clearMockAlerts() {
        try {
            long count = alertRepository.count();
            alertRepository.deleteAll();
            logger.info("Cleared {} alerts from database", count);
        } catch (Exception e) {
            logger.error("Failed to clear alerts: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear alerts", e);
        }
    }

    /**
     * Get current alert count
     */
    public long getAlertCount() {
        return alertRepository.count();
    }

    /**
     * Initialize mock alerts if database is empty (called at startup)
     */
    public void initializeMockAlertsIfEmpty() {
        try {
            long currentCount = alertRepository.count();
            if (currentCount == 0) {
                logger.info("No alerts found in database, initializing 50 mock alerts");
                populateMockAlerts();
            } else {
                logger.info("Database already contains {} alerts, skipping mock initialization", currentCount);
            }
        } catch (Exception e) {
            logger.error("Failed to initialize mock alerts: {}", e.getMessage(), e);
        }
    }
}