package com.leizo.admin.util;

import com.leizo.pojo.entity.Alert;
import com.leizo.pojo.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for alert-related operations to eliminate code duplication
 */
public class AlertUtils {
    
    private static final Logger log = LoggerFactory.getLogger(AlertUtils.class);
    
    /**
     * Handles alert skipping with consistent logging
     * 
     * @param type the type of skip (e.g., "DUPLICATE", "COOLDOWN")
     * @param actor the actor causing the skip
     * @param reason the reason for skipping
     * @return null (to indicate no alert should be created)
     */
    public static Alert handleSkip(String type, String actor, String reason) {
        log.warn("ALERT_SKIPPED [{}]: {} - {}", type, actor, reason);
        return null;
    }
    
    /**
     * Generates a consistent alert hash for deduplication
     * 
     * @param transaction the transaction details
     * @param reason the alert reason
     * @return a hash string for deduplication
     */
    public static String generateAlertHash(Transaction transaction, String reason) {
        try {
            String data = transaction.getSender() + "|" +
                         transaction.getReceiver() + "|" +
                         transaction.getAmount() + "|" +
                         transaction.getCurrency() + "|" +
                         transaction.getCountry() + "|" +
                         reason.trim().toLowerCase();
            
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate alert hash", e);
        }
    }
    
    /**
     * Generates a simple alert key for basic deduplication
     * 
     * @param transaction the transaction details
     * @param reason the alert reason
     * @return a simple key string for deduplication
     */
    public static String generateAlertKey(Transaction transaction, String reason) {
        return transaction.getSender() + "|" +
               transaction.getReceiver() + "|" +
               transaction.getAmount() + "|" +
               transaction.getCurrency() + "|" +
               transaction.getCountry() + "|" +
               reason.trim().toLowerCase();
    }
    
    /**
     * Gets the risk score level based on numeric score
     * 
     * @param score the risk score
     * @return the risk level string (HIGH, MEDIUM, LOW)
     */
    public static String getRiskScoreLevel(int score) {
        if (score >= 75) return "HIGH";
        if (score >= 40) return "MEDIUM";
        return "LOW";
    }
} 