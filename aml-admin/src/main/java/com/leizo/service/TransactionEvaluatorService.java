package com.leizo.service;

import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Rule;
import com.leizo.pojo.entity.Alert;
import com.leizo.model.IngestionResult;
import com.leizo.enums.IngestionStatus;
import com.leizo.enums.RiskScore;
import com.leizo.enums.RuleSensitivity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unified Transaction Evaluator Service
 * 
 * This service consolidates all transaction processing logic including:
 * - Rule engine evaluation
 * - Sanctions screening
 * - Risk scoring
 * - Alert decision making
 * - Duplicate suppression
 * - Cooldown management
 */
public interface TransactionEvaluatorService {
    
    /**
     * Evaluates a transaction through the complete AML pipeline
     * 
     * @param transaction the transaction to evaluate
     * @return IngestionResult with status and alert information
     */
    IngestionResult evaluateTransaction(Transaction transaction);
    
    /**
     * Checks if a transaction should trigger an alert
     * 
     * @param transaction the transaction to check
     * @return Alert if triggered, null otherwise
     */
    AlertDecisionResult evaluateForAlert(Transaction transaction);
    
    /**
     * Gets the current risk score for a transaction
     * 
     * @param transaction the transaction to score
     * @return calculated risk score
     */
    int calculateRiskScore(Transaction transaction);
    
    /**
     * Checks if a transaction involves sanctioned entities
     * 
     * @param transaction the transaction to check
     * @return SanctionsMatchResult with match details
     */
    SanctionsMatchResult checkSanctions(Transaction transaction);
    
    /**
     * Gets all active rules for evaluation
     * 
     * @return list of active rules
     */
    List<Rule> getActiveRules();
    
    /**
     * Adds a new rule to the evaluation engine
     * 
     * @param rule the rule to add
     */
    void addRule(Rule rule);
    
    /**
     * Removes a rule from the evaluation engine
     * 
     * @param ruleDescription the description of the rule to remove
     */
    void removeRule(String ruleDescription);
    
    /**
     * Gets evaluation statistics
     * 
     * @return Map containing evaluation metrics
     */
    Map<String, Object> getEvaluationStats();
} 