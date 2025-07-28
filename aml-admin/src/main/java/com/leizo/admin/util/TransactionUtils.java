package com.leizo.admin.util;

import com.leizo.admin.entity.Transaction;
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
        return transaction.getMetadata() != null && 
               transaction.getMetadata() instanceof Map &&
               ((Map<?,?>) transaction.getMetadata()).keySet().stream()
                   .anyMatch(k -> k.toString().equalsIgnoreCase("flagged"));
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