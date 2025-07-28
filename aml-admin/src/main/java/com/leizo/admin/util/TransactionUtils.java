package com.leizo.admin.util;

import com.leizo.pojo.entity.Transaction;
import java.util.Map;

/**
 * Utility class for transaction-related operations to eliminate code duplication
 */
public class TransactionUtils {
    
    /**
     * Checks if a transaction has a manual flag in its metadata
     * 
     * @param transaction the transaction to check
     * @return true if the transaction has a manual flag, false otherwise
     */
    public static boolean hasManualFlag(Transaction transaction) {
        if (transaction.getMetadata() == null) {
            System.out.println("[TransactionUtils] hasManualFlag: metadata is null for transaction [" + transaction.getSender() + "]");
            return false;
        }
        
        Map<String, String> metadata = (Map<String, String>) transaction.getMetadata();
        System.out.println("[TransactionUtils] hasManualFlag: checking metadata for [" + transaction.getSender() + "]: " + metadata);
        
        // Check for "manualFlag" key (from CSV parsing)
        if (metadata.containsKey("manualFlag")) {
            String manualFlagValue = metadata.get("manualFlag");
            boolean result = "true".equalsIgnoreCase(manualFlagValue);
            System.out.println("[TransactionUtils] hasManualFlag: found manualFlag key with value [" + manualFlagValue + "], result: " + result);
            return result;
        }
        
        // Also check for "flagged" key (legacy)
        if (metadata.containsKey("flagged")) {
            String flaggedValue = metadata.get("flagged");
            boolean result = "true".equalsIgnoreCase(flaggedValue);
            System.out.println("[TransactionUtils] hasManualFlag: found flagged key with value [" + flaggedValue + "], result: " + result);
            return result;
        }
        
        System.out.println("[TransactionUtils] hasManualFlag: no manual flag found for [" + transaction.getSender() + "]");
        return false;
    }
    
    /**
     * Checks if a transaction is valid for processing
     * 
     * @param transaction the transaction to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTransaction(Transaction transaction) {
        return transaction != null &&
               transaction.getSender() != null && !transaction.getSender().trim().isEmpty() &&
               transaction.getReceiver() != null && !transaction.getReceiver().trim().isEmpty() &&
               transaction.getAmount() != null && transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0 &&
               transaction.getCurrency() != null && !transaction.getCurrency().trim().isEmpty() &&
               transaction.getCountry() != null && !transaction.getCountry().trim().isEmpty();
    }
    
    /**
     * Checks if a transaction involves a high-risk currency
     * 
     * @param transaction the transaction to check
     * @return true if high-risk currency, false otherwise
     */
    public static boolean isHighRiskCurrency(Transaction transaction) {
        String currency = transaction.getCurrency();
        return currency != null && 
               (currency.equalsIgnoreCase("XBT") || // Bitcoin
                currency.equalsIgnoreCase("BTC") ||
                currency.equalsIgnoreCase("ETH") ||
                currency.equalsIgnoreCase("XRP"));
    }
} 