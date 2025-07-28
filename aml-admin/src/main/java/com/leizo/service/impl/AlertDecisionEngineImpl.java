package com.leizo.service.impl;

import com.leizo.pojo.entity.Alert;
import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Rule;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.service.AlertDecisionEngine;
import com.leizo.service.LoggerService;
import com.leizo.service.CaseManager;
import com.leizo.service.AlertHistoryService;
import com.leizo.service.SanctionsMatchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Alert Decision Engine Implementation
 * 
 * Provides comprehensive alert lifecycle management including:
 * - Alert creation with proper metadata
 * - Duplicate detection using SHA-256 hashing
 * - Cooldown management with configurable periods
 * - Alert persistence and routing to case management
 * - Comprehensive audit trails
 */
@Service
public class AlertDecisionEngineImpl implements AlertDecisionEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertDecisionEngineImpl.class);
    
    // Core services
    private final AlertRepository alertRepository;
    private final LoggerService loggerService;
    private final CaseManager caseManager;
    private final AlertHistoryService alertHistoryService;
    
    // Duplicate detection
    private final Set<String> alertHashes = ConcurrentHashMap.newKeySet();
    
    // Cooldown management
    private final Map<String, Long> cooldownMap = new ConcurrentHashMap<>();
    private static final long DEFAULT_COOLDOWN_TIME_MS = 10 * 60 * 1000; // 10 minutes
    private static final long SANCTIONS_COOLDOWN_TIME_MS = 30 * 60 * 1000; // 30 minutes
    private static final long HIGH_PRIORITY_COOLDOWN_TIME_MS = 5 * 60 * 1000; // 5 minutes
    
    // Statistics tracking
    private final AtomicLong totalAlertsCreated = new AtomicLong(0);
    private final AtomicLong totalAlertsSuppressed = new AtomicLong(0);
    private final AtomicLong totalDuplicatesDetected = new AtomicLong(0);
    private final AtomicLong totalCooldownSuppressions = new AtomicLong(0);
    private final Map<String, Long> alertTypeCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> ruleMatchCounts = new ConcurrentHashMap<>();
    
    public AlertDecisionEngineImpl(AlertRepository alertRepository,
                                 LoggerService loggerService,
                                 CaseManager caseManager,
                                 AlertHistoryService alertHistoryService) {
        this.alertRepository = alertRepository;
        this.loggerService = loggerService;
        this.caseManager = caseManager;
        this.alertHistoryService = alertHistoryService;
    }
    
    @Override
    public Alert createAlert(Transaction transaction, Rule rule, String reason, 
                           int priorityScore, SanctionsMatchResult sanctionsResult) {
        
        Alert alert = new Alert();
        
        // Set basic alert information
        alert.setTransactionId(transaction.getId());
        alert.setReason(reason);
        alert.setTimestamp(LocalDateTime.now());
        alert.setPriorityScore(priorityScore);
        alert.updatePriorityLevel();
        
        // Set alert type based on source
        if (sanctionsResult != null) {
            alert.setAlertType("SANCTIONS");
            alert.setMatchedEntityName(sanctionsResult.getMatchedEntityName());
            alert.setMatchedList(sanctionsResult.getMatchedList());
            alert.setMatchReason(sanctionsResult.getFormattedReason());
        } else if (rule != null) {
            alert.setAlertType("RULE_MATCH");
            alert.setMatchReason("Rule matched: " + rule.getDescription());
        } else {
            alert.setAlertType("GENERIC");
        }
        
        // Generate unique alert ID
        alert.setAlertId(generateAlertId(transaction, reason));
        
        // Set transaction reference
        alert.setTransaction(transaction);
        
        // Update statistics
        totalAlertsCreated.incrementAndGet();
        alertTypeCounts.merge(alert.getAlertType(), 1L, Long::sum);
        
        if (rule != null) {
            ruleMatchCounts.merge(rule.getDescription(), 1L, Long::sum);
        }
        
        logger.info("Created alert: {} for transaction: {} with priority: {}", 
                   alert.getAlertId(), transaction.getId(), priorityScore);
        
        return alert;
    }
    
    @Override
    public boolean shouldSuppressAlert(Transaction transaction, String reason) {
        String alertHash = generateAlertHash(transaction, reason);
        
        if (alertHashes.contains(alertHash)) {
            totalDuplicatesDetected.incrementAndGet();
            totalAlertsSuppressed.incrementAndGet();
            
            logger.debug("Suppressing duplicate alert for transaction: {} with reason: {}", 
                        transaction.getId(), reason);
            return true;
        }
        
        // Add hash to prevent future duplicates
        alertHashes.add(alertHash);
        return false;
    }
    
    @Override
    public boolean isInCooldown(String sender, String ruleId) {
        String key = sender + "|" + ruleId;
        Long lastAlertTime = cooldownMap.get(key);
        
        if (lastAlertTime == null) {
            return false;
        }
        
        long cooldownTime = getCooldownTimeForRule(ruleId);
        boolean inCooldown = (System.currentTimeMillis() - lastAlertTime) < cooldownTime;
        
        if (inCooldown) {
            totalCooldownSuppressions.incrementAndGet();
            totalAlertsSuppressed.incrementAndGet();
            
            logger.debug("Suppressing alert due to cooldown for sender: {} and rule: {}", 
                        sender, ruleId);
        }
        
        return inCooldown;
    }
    
    @Override
    public void registerCooldown(String sender, String ruleId) {
        String key = sender + "|" + ruleId;
        cooldownMap.put(key, System.currentTimeMillis());
        
        logger.debug("Registered cooldown for sender: {} and rule: {}", sender, ruleId);
    }
    
    @Override
    public boolean processAlert(Alert alert) {
        try {
            // Persist alert to database
            Alert savedAlert = alertRepository.save(alert);
            
            // Log alert creation
            loggerService.logAlert(savedAlert);
            
            // Route to case management
            if (caseManager != null) {
                caseManager.reviewAlert(savedAlert);
            }
            
            // Save to alert history
            if (alertHistoryService != null) {
                alertHistoryService.saveAlert(savedAlert);
            }
            
            // Log comprehensive audit trail
            logAlertAudit(savedAlert);
            
            logger.info("Successfully processed alert: {} for transaction: {}", 
                       savedAlert.getAlertId(), savedAlert.getTransactionId());
            
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to process alert: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getAlertStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAlertsCreated", totalAlertsCreated.get());
        stats.put("totalAlertsSuppressed", totalAlertsSuppressed.get());
        stats.put("totalDuplicatesDetected", totalDuplicatesDetected.get());
        stats.put("totalCooldownSuppressions", totalCooldownSuppressions.get());
        stats.put("alertTypeCounts", new HashMap<>(alertTypeCounts));
        stats.put("ruleMatchCounts", new HashMap<>(ruleMatchCounts));
        stats.put("activeCooldowns", cooldownMap.size());
        stats.put("duplicateHashesCount", alertHashes.size());
        return stats;
    }
    
    @Override
    public Map<String, Long> getCooldownStatus() {
        Map<String, Long> status = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        
        for (Map.Entry<String, Long> entry : cooldownMap.entrySet()) {
            String key = entry.getKey();
            Long lastAlertTime = entry.getValue();
            long timeRemaining = Math.max(0, getCooldownTimeForRule(key) - (currentTime - lastAlertTime));
            status.put(key, timeRemaining);
        }
        
        return status;
    }
    
    @Override
    public void clearAllCooldowns() {
        cooldownMap.clear();
        logger.info("Cleared all cooldowns");
    }
    
    @Override
    public List<String> getDuplicateAlertHashes() {
        return new ArrayList<>(alertHashes);
    }
    
    // Helper methods
    
    private String generateAlertId(Transaction transaction, String reason) {
        return "ALT-" + System.currentTimeMillis() + "-" + 
               Math.abs(transaction.getId().hashCode()) + "-" +
               Math.abs(reason.hashCode());
    }
    
    private String generateAlertHash(Transaction transaction, String reason) {
        return com.leizo.admin.util.AlertUtils.generateAlertHash(transaction, reason);
    }
    
    private long getCooldownTimeForRule(String ruleId) {
        if (ruleId == null) {
            return DEFAULT_COOLDOWN_TIME_MS;
        }
        
        // Different cooldown periods based on rule type
        if (ruleId.toLowerCase().contains("sanction")) {
            return SANCTIONS_COOLDOWN_TIME_MS;
        } else if (ruleId.toLowerCase().contains("high") || 
                   ruleId.toLowerCase().contains("priority")) {
            return HIGH_PRIORITY_COOLDOWN_TIME_MS;
        } else {
            return DEFAULT_COOLDOWN_TIME_MS;
        }
    }
    
    private void logAlertAudit(Alert alert) {
        StringBuilder auditLog = new StringBuilder();
        auditLog.append("Alert Audit - ID: ").append(alert.getAlertId())
                .append(", Type: ").append(alert.getAlertType())
                .append(", Priority: ").append(alert.getPriorityLevel())
                .append(", Score: ").append(alert.getPriorityScore())
                .append(", Transaction: ").append(alert.getTransactionId())
                .append(", Reason: ").append(alert.getReason())
                .append(", Timestamp: ").append(alert.getTimestamp());
        
        if (alert.getMatchedEntityName() != null) {
            auditLog.append(", Matched Entity: ").append(alert.getMatchedEntityName());
        }
        
        if (alert.getMatchedList() != null) {
            auditLog.append(", Matched List: ").append(alert.getMatchedList());
        }
        
        loggerService.logEvent("ALERT_AUDIT", "SYSTEM", auditLog.toString());
    }
} 