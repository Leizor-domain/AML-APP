package com.leizo.service;

import com.leizo.pojo.entity.Alert;
import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Rule;

import java.util.List;
import java.util.Map;

/**
 * Alert Decision Engine
 * 
 * Handles the complete alert lifecycle including:
 * - Alert creation and validation
 * - Duplicate detection and suppression
 * - Cooldown management
 * - Alert persistence and routing
 * - Audit trail maintenance
 */
public interface AlertDecisionEngine {
    
    /**
     * Creates an alert from a transaction evaluation result
     * 
     * @param transaction the transaction that triggered the alert
     * @param rule the rule that matched (null for sanctions alerts)
     * @param reason the reason for the alert
     * @param priorityScore the calculated priority score
     * @param sanctionsResult sanctions match details (null for rule alerts)
     * @return the created alert
     */
    Alert createAlert(Transaction transaction, Rule rule, String reason, 
                     int priorityScore, SanctionsMatchResult sanctionsResult);
    
    /**
     * Checks if an alert should be suppressed due to duplication
     * 
     * @param transaction the transaction being evaluated
     * @param reason the alert reason
     * @return true if alert should be suppressed
     */
    boolean shouldSuppressAlert(Transaction transaction, String reason);
    
    /**
     * Checks if an alert should be suppressed due to cooldown
     * 
     * @param sender the transaction sender
     * @param ruleId the rule identifier
     * @return true if alert should be suppressed
     */
    boolean isInCooldown(String sender, String ruleId);
    
    /**
     * Registers a cooldown period for a sender-rule combination
     * 
     * @param sender the transaction sender
     * @param ruleId the rule identifier
     */
    void registerCooldown(String sender, String ruleId);
    
    /**
     * Processes an alert through the complete lifecycle
     * 
     * @param alert the alert to process
     * @return true if alert was successfully processed
     */
    boolean processAlert(Alert alert);
    
    /**
     * Gets alert statistics and metrics
     * 
     * @return map containing alert statistics
     */
    Map<String, Object> getAlertStats();
    
    /**
     * Gets the current cooldown status for all sender-rule combinations
     * 
     * @return map of cooldown status
     */
    Map<String, Long> getCooldownStatus();
    
    /**
     * Clears all cooldowns (for testing/admin purposes)
     */
    void clearAllCooldowns();
    
    /**
     * Gets duplicate alert hashes for debugging
     * 
     * @return set of alert hashes
     */
    List<String> getDuplicateAlertHashes();
} 