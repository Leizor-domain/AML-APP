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
        "Rami El-Hassan", "Tiffany Garcia", "Samir Al-Zahra", "Monica Rodriguez",
        "Adel Khalil", "Heather Martinez", "Waleed Al-Mansouri", "Brittany Anderson",
        "Karim Hassan", "Melissa Taylor", "Tariq Al-Rashid", "Christina Moore",
        "Hassan El-Sayed", "Rebecca Jackson", "Omar Khalil", "Stephanie Martin",
        "Rashid Al-Zahra", "Nicole Lee", "Malik Al-Mansouri", "Amanda White",
        "Faisal Hassan", "Jessica Brown", "Samir Khalil", "Ashley Davis"
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
    
    // Mock alert reasons
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
        "Exact name match", "Fuzzy name match", "Address match", "Date of birth match",
        "Nationality match", "Document number match", "Phone number match",
        "Email address match", "IP address match", "Bank account match"
    };
    
    /**
     * Generate and populate 70 mock alerts
     */
    public void populateMockAlerts() {
        logger.info("Starting mock alert population - generating 70 alerts");
        
        List<Alert> mockAlerts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 1; i <= 70; i++) {
            Alert alert = createMockAlert(i, now);
            mockAlerts.add(alert);
        }
        
        // Save all alerts to database
        try {
            alertRepository.saveAll(mockAlerts);
            logger.info("Successfully populated {} mock alerts in database", mockAlerts.size());
        } catch (Exception e) {
            logger.error("Failed to save mock alerts to database: {}", e.getMessage(), e);
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
        alert.setAlertType(getRandomElement(mockAlertTypes));
        alert.setPriorityLevel(getRandomElement(mockPriorityLevels));
        alert.setReason(getRandomElement(mockAlertReasons));
        
        // Timestamp - spread over the last 30 days
        int daysAgo = random.nextInt(30);
        int hoursAgo = random.nextInt(24);
        int minutesAgo = random.nextInt(60);
        LocalDateTime alertTime = baseTime
            .minusDays(daysAgo)
            .minusHours(hoursAgo)
            .minusMinutes(minutesAgo);
        alert.setTimestamp(alertTime);
        
        // Transaction information
        alert.setTransactionId(random.nextInt(1000) + 1);
        
        // Priority score and level
        int priorityScore = random.nextInt(100) + 1;
        alert.setPriorityScore(priorityScore);
        alert.updatePriorityLevel(); // This will set priority level based on score
        
        return alert;
    }
    
    /**
     * Generate random transaction amount
     */
    private java.math.BigDecimal generateRandomAmount() {
        // Generate amounts between $1,000 and $500,000
        double amount = 1000 + (random.nextDouble() * 499000);
        return java.math.BigDecimal.valueOf(amount).setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Generate random currency
     */
    private String getRandomCurrency() {
        String[] currencies = {"USD", "EUR", "GBP", "CAD", "AUD", "JPY", "CHF", "SEK", "NOK", "DKK"};
        return getRandomElement(currencies);
    }
    
    /**
     * Generate random risk score
     */
    private String generateRandomRiskScore() {
        int score = random.nextInt(100) + 1;
        if (score >= 80) return "HIGH";
        else if (score >= 50) return "MEDIUM";
        else return "LOW";
    }
    
    /**
     * Generate mock notes based on alert type
     */
    private String generateMockNotes(Alert alert) {
        String alertType = alert.getAlertType();
        String entityName = alert.getMatchedEntityName();
        
        switch (alertType) {
            case "HIGH_VALUE":
                return String.format("High value transaction by %s requires immediate review", entityName);
            case "SANCTIONS":
                return String.format("Entity %s matched against %s. Requires immediate blocking and investigation", 
                    entityName, alert.getMatchedList());
            case "STRUCTURING":
                return String.format("Potential structuring behavior detected for %s. Multiple transactions below reporting threshold", 
                    entityName);
            case "PEP":
                return String.format("Politically Exposed Person transaction by %s. Enhanced due diligence required", 
                    entityName);
            case "MANUAL_FLAG":
                return String.format("Manual flag raised by analyst for %s. Additional investigation needed", 
                    entityName);
            default:
                return String.format("Alert generated for %s. Review required for compliance purposes", entityName);
        }
    }
    
    /**
     * Get random element from array
     */
    private <T> T getRandomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }
    
    /**
     * Clear all mock alerts from database
     */
    public void clearMockAlerts() {
        try {
            alertRepository.deleteAll();
            logger.info("Cleared all alerts from database");
        } catch (Exception e) {
            logger.error("Failed to clear alerts from database: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get count of alerts in database
     */
    public long getAlertCount() {
        return alertRepository.count();
    }
}