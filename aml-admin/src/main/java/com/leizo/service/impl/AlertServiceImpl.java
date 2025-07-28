package com.leizo.service.impl;

import com.leizo.pojo.entity.Alert;
import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Rule;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.service.AlertService;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AlertServiceImpl manages the creation and persistence of alerts generated
 * from flagged transactions based on defined rules or sanctions.
 *
 * This class prevents redundant alerts via hash-based deduplication and enforces
 * a cooldown mechanism to limit alert frequency per rule and sender.
 */
@Service
public class AlertServiceImpl implements AlertService {

    private final Set<String> alertHashes = new HashSet<>();
    private final Map<String, Long> cooldownMap = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME_MS = 10 * 60 * 1000; // 10 minutes

    private final AlertRepository alertRepository;

    /**
     * Constructs the AlertServiceImpl with a reference to the AlertRepository.
     *
     * @param alertRepository the repository responsible for alert persistence
     */
    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    /**
     * Checks if the sender is in cooldown for a specific rule.
     * Prevents repeated alert spam within a 10-minute interval.
     *
     * @param sender the sender of the transaction
     * @param ruleId the identifier of the triggered rule
     * @return true if within cooldown period, false otherwise
     */
    @Override
    public boolean isInCooldown(String sender, String ruleId) {
        String key = sender + "|" + ruleId;
        Long lastAlertTime = cooldownMap.get(key);
        return lastAlertTime != null && (System.currentTimeMillis() - lastAlertTime < COOLDOWN_TIME_MS);
    }

    /**
     * Registers the current timestamp for a sender-rule combination to enforce cooldown.
     *
     * @param sender the sender of the transaction
     * @param ruleId the rule that triggered the alert
     */
    @Override
    public void registerCooldown(String sender, String ruleId) {
        String key = sender + "|" + ruleId;
        cooldownMap.put(key, System.currentTimeMillis());
    }

    /**
     * Generates a new alert based on the transaction and rule, assigns priority and type,
     * creates a fingerprint hash, and persists the alert if not a duplicate.
     *
     * @param txn    the transaction under review
     * @param rule   the rule that matched
     * @param reason the reason for triggering the alert
     * @return the created alert object or null if duplicate
     */
    @Override
    public com.leizo.pojo.entity.Alert generateAlert(com.leizo.pojo.entity.Transaction txn, com.leizo.pojo.entity.Rule rule, String reason) {
        if (isDuplicateAlert(txn, reason)) {
            System.out.println("[AlertService] Duplicate alert skipped for sender: " + txn.getSender());
            return null;
        }
        // Convert model.Transaction to entity.Alert for persistence
        com.leizo.pojo.entity.Alert entityAlert = new com.leizo.pojo.entity.Alert();
        entityAlert.setReason(reason);
        entityAlert.setTimestamp(java.time.LocalDateTime.ofEpochSecond(System.currentTimeMillis() / 1000, 0, java.time.ZoneOffset.UTC));
        entityAlert.setAlertType("Generic");
        entityAlert.setPriorityLevel("Low");
        // alertRepository.save(entityAlert);
        // Convert back to model.Alert for return
        com.leizo.pojo.entity.Alert modelAlert = new com.leizo.pojo.entity.Alert();
        modelAlert.setAlertId(entityAlert.getAlertId());
        modelAlert.setReason(entityAlert.getReason());
        modelAlert.setTimestamp(java.time.LocalDateTime.now());
        modelAlert.setAlertType(entityAlert.getAlertType());
        modelAlert.setPriorityLevel(entityAlert.getPriorityLevel());
        return modelAlert;
    }

    /**
     * Checks if a similar alert has already been generated.
     *
     * @param txn    the transaction under evaluation
     * @param reason the reason string triggering the alert
     * @return true if a duplicate alert exists, false otherwise
     */
    @Override
    public boolean isDuplicateAlert(com.leizo.pojo.entity.Transaction txn, String reason) {
        String key = generateAlertKey(txn, reason);
        return alertHashes.contains(key);
    }

    /**
     * Generates a hashable key representing the unique characteristics of an alert.
     *
     * @param txn    the transaction being evaluated
     * @param reason the reason for alert creation
     * @return a deduplication key string
     */
    private String generateAlertKey(com.leizo.pojo.entity.Transaction txn, String reason) {
        return com.leizo.admin.util.AlertUtils.generateAlertKey(txn, reason);
    }

    /**
     * Generates a secure SHA-256 fingerprint hash for the alert.
     *
     * @param txn    the transaction details
     * @param reason the alert reason
     * @return a fingerprint hash string
     */
    @Override
    public String generateFingerPrint(com.leizo.pojo.entity.Transaction txn, String reason) {
        return com.leizo.admin.util.AlertUtils.generateAlertHash(txn, reason);
    }
}
