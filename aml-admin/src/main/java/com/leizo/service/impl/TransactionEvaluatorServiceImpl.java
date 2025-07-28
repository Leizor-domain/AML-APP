package com.leizo.service.impl;

import com.leizo.admin.entity.Transaction;
import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.Alert;
import com.leizo.common.entity.Users;
import com.leizo.model.IngestionResult;
import com.leizo.enums.IngestionStatus;
import com.leizo.enums.RiskScore;
import com.leizo.enums.RuleSensitivity;
import com.leizo.service.*;
import com.leizo.loader.SanctionListLoader;
import com.leizo.loader.RuleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Unified Transaction Evaluator Service Implementation
 * 
 * This service consolidates all transaction processing logic including:
 * - Rule engine evaluation with both hardcoded and JSON-based rules
 * - Sanctions screening with OFAC and local lists
 * - Risk scoring with enhanced country risk assessment
 * - Alert decision making with duplicate suppression and cooldowns
 * - Comprehensive logging and audit trails
 */
@Service
public class TransactionEvaluatorServiceImpl implements TransactionEvaluatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionEvaluatorServiceImpl.class);
    
    // Core services
    private final RuleEngine ruleEngine;
    private final SanctionsChecker sanctionsChecker;
    private final RiskScoringService riskScoringService;
    private final AlertService alertService;
    private final LoggerService loggerService;
    private final SanctionListLoader sanctionListLoader;
    private final RuleLoader ruleLoader;
    
    // Enhanced rule management
    private final List<Rule> hardcodedRules = new ArrayList<>();
    private final List<Rule> jsonRules = new ArrayList<>();
    private final Map<String, Rule> ruleRegistry = new ConcurrentHashMap<>();
    
    // Performance tracking
    private final AtomicLong totalTransactionsEvaluated = new AtomicLong(0);
    private final AtomicLong totalAlertsGenerated = new AtomicLong(0);
    private final AtomicLong totalSanctionsMatches = new AtomicLong(0);
    private final Map<String, Long> ruleMatchCounts = new ConcurrentHashMap<>();
    
    // Enhanced high-risk countries list

    
    public TransactionEvaluatorServiceImpl(RuleEngine ruleEngine, 
                                         SanctionsChecker sanctionsChecker,
                                         RiskScoringService riskScoringService,
                                         AlertService alertService,
                                         LoggerService loggerService,
                                         SanctionListLoader sanctionListLoader,
                                         RuleLoader ruleLoader) {
        this.ruleEngine = ruleEngine;
        this.sanctionsChecker = sanctionsChecker;
        this.riskScoringService = riskScoringService;
        this.alertService = alertService;
        this.loggerService = loggerService;
        this.sanctionListLoader = sanctionListLoader;
        this.ruleLoader = ruleLoader;
        
        initializeRules();
    }
    
    /**
     * Initialize both hardcoded and JSON-based rules
     */
    private void initializeRules() {
        initializeHardcodedRules();
        loadJsonRules();
        registerAllRules();
    }
    
    /**
     * Initialize hardcoded rules from the original AMLEngine logic
     */
    private void initializeHardcodedRules() {
        // High Value Transfer Rule
        Rule highValueRule = new Rule(
            "High Value Transfer Rule",
            RuleSensitivity.HIGH,
            (txn, amount) -> amount.compareTo(new BigDecimal("10000")) > 0,
            Set.of("value", "priority", "large_txn")
        );
        
        // Medium Risk Region Transfer
        Rule mediumRiskRule = new Rule(
            "Medium Risk Region Transfer",
            RuleSensitivity.MEDIUM,
            (txn, amount) -> {
                String country = txn.getCountry();
                return country != null && (country.equalsIgnoreCase("Turkey") || 
                                         country.equalsIgnoreCase("Mexico") ||
                                         riskScoringService.getHighRiskCountries().contains(country));
            },
            Set.of("geo", "moderate_risk")
        );
        
        // Low Value Routine Transfer
        Rule lowValueRule = new Rule(
            "Low Value Routine Transfer",
            RuleSensitivity.LOW,
            (txn, amount) -> amount.compareTo(new BigDecimal("500")) < 0,
            Set.of("routine", "small_amount", "low_risk")
        );
        
        // Manual Flag Rule
        Rule manualFlagRule = new Rule(
            "Manual Flag Rule",
            RuleSensitivity.HIGH,
            (txn, amount) -> hasManualFlag(txn),
            Set.of("manual", "override", "flagged")
        );
        
        // Frequent Sender Rule
        Rule frequentSenderRule = new Rule(
            "Frequent Sender Rule",
            RuleSensitivity.MEDIUM,
            (txn, amount) -> isFrequentSender(txn),
            Set.of("behavior", "frequency", "pattern")
        );
        
        // Currency Risk Rule
        Rule currencyRiskRule = new Rule(
            "Currency Risk Rule",
            RuleSensitivity.MEDIUM,
            (txn, amount) -> isHighRiskCurrency(txn),
            Set.of("currency", "forex", "risk")
        );
        
        hardcodedRules.addAll(Arrays.asList(
            highValueRule, mediumRiskRule, lowValueRule, 
            manualFlagRule, frequentSenderRule, currencyRiskRule
        ));
        
        logger.info("Initialized {} hardcoded rules", hardcodedRules.size());
    }
    
    /**
     * Load JSON-based rules from the rules.json file
     */
    private void loadJsonRules() {
        try {
            // Load rules from JSON file
            ruleLoader.loadFromJson("classpath:rules.json");
            jsonRules.addAll(ruleEngine.getActiveRules());
            logger.info("Loaded {} JSON-based rules", jsonRules.size());
        } catch (Exception e) {
            logger.warn("Failed to load JSON rules: {}", e.getMessage());
        }
    }
    
    /**
     * Register all rules in the rule registry for quick lookup
     */
    private void registerAllRules() {
        List<Rule> allRules = new ArrayList<>();
        allRules.addAll(hardcodedRules);
        allRules.addAll(jsonRules);
        
        for (Rule rule : allRules) {
            ruleRegistry.put(rule.getDescription(), rule);
        }
        
        logger.info("Registered {} total rules in rule registry", ruleRegistry.size());
    }
    
    @Override
    public IngestionResult evaluateTransaction(Transaction transaction) {
        totalTransactionsEvaluated.incrementAndGet();
        
        try {
            // Validate transaction
            if (!isValidTransaction(transaction)) {
                return new IngestionResult(IngestionStatus.INVALID_INPUT, false, null, 0);
            }
            
            // Calculate base risk score
            int riskScore = calculateRiskScore(transaction);
            transaction.setRiskScore(RiskScore.valueOf(getRiskScoreLevel(riskScore)));
            
            // Evaluate for alerts
            AlertDecisionResult alertResult = evaluateForAlert(transaction);
            
            // Log evaluation
            logEvaluation(transaction, alertResult, riskScore);
            
            // Update statistics
            if (alertResult.shouldTriggerAlert()) {
                totalAlertsGenerated.incrementAndGet();
            }
            
            return new IngestionResult(
                IngestionStatus.SUCCESS,
                alertResult.shouldTriggerAlert(),
                alertResult.getAlert() != null ? alertResult.getAlert().getAlertId() : null,
                riskScore
            );
            
        } catch (Exception e) {
            logger.error("Error evaluating transaction: {}", e.getMessage(), e);
            return new IngestionResult(IngestionStatus.EVALUATION_FAILED, false, null, 0);
        }
    }
    
    @Override
    public AlertDecisionResult evaluateForAlert(Transaction transaction) {
        // Check for sanctions first (highest priority)
        SanctionsMatchResult sanctionsResult = checkSanctions(transaction);
        if (sanctionsResult.isSanctioned()) {
            totalSanctionsMatches.incrementAndGet();
            
            // Check for duplicate sanctions alert
            if (alertService.isDuplicateAlert(transaction, sanctionsResult.getFormattedReason())) {
                return AlertDecisionResult.duplicateAlert(transaction, "Duplicate sanctions alert");
            }
            
            // Create sanctions alert
            Alert alert = createSanctionsAlert(transaction, sanctionsResult);
            return AlertDecisionResult.sanctionsAlert(transaction, alert, sanctionsResult);
        }
        
        // Check for rule matches
        List<Rule> matchedRules = findMatchingRules(transaction);
        if (matchedRules.isEmpty()) {
            return AlertDecisionResult.noAlert(transaction, calculateRiskScore(transaction));
        }
        
        // Select the rule with highest sensitivity
        Rule selectedRule = matchedRules.stream()
            .max(Comparator.comparingInt(r -> r.getSensitivity().getWeight()))
            .orElse(null);
        
        if (selectedRule == null) {
            return AlertDecisionResult.noAlert(transaction, calculateRiskScore(transaction));
        }
        
        // Check for duplicate rule alert
        String ruleReason = "Rule matched: " + selectedRule.getDescription();
        if (alertService.isDuplicateAlert(transaction, ruleReason)) {
            return AlertDecisionResult.duplicateAlert(transaction, "Duplicate rule alert");
        }
        
        // Check cooldown
        String sender = transaction.getSender();
        String ruleId = selectedRule.getDescription();
        if (alertService.isInCooldown(sender, ruleId)) {
            return AlertDecisionResult.cooldownAlert(transaction, "Cooldown active for rule: " + ruleId);
        }
        
        // Calculate enhanced risk score
        int riskScore = calculateRiskScore(transaction);
        int priorityScore = calculatePriorityScore(transaction, selectedRule, riskScore);
        
        // Create rule alert
        Alert alert = createRuleAlert(transaction, selectedRule, ruleReason, priorityScore);
        
        // Register cooldown
        alertService.registerCooldown(sender, ruleId);
        
        // Update rule match statistics
        ruleMatchCounts.merge(selectedRule.getDescription(), 1L, Long::sum);
        
        return AlertDecisionResult.ruleAlert(
            transaction, alert, selectedRule, ruleReason, 
            riskScore, priorityScore, 
            matchedRules.stream().map(Rule::getDescription).collect(Collectors.toList())
        );
    }
    
    @Override
    public int calculateRiskScore(Transaction transaction) {
        return riskScoringService.calculateRiskScore(transaction);
    }
    
    @Override
    public SanctionsMatchResult checkSanctions(Transaction transaction) {
        try {
            // Check OFAC SDN list first
            if (sanctionsChecker.isSanctionedEntity(transaction.getSender(), 
                                                   transaction.getCountry(), 
                                                   transaction.getDob(), "Any")) {
                return SanctionsMatchResult.ofacMatch(
                    transaction.getSender(), 
                    transaction.getCountry(), 
                    "Entity matched in OFAC SDN list", 
                    1.0
                );
            }
            
            // Check country sanctions
            if (sanctionsChecker.checkCountry(transaction.getCountry())) {
                return SanctionsMatchResult.countryMatch(
                    transaction.getCountry(), 
                    "Country is under sanctions"
                );
            }
            
            // Check name-based sanctions
            if (sanctionsChecker.checkName(transaction.getSender())) {
                return SanctionsMatchResult.localMatch(
                    transaction.getSender(), 
                    transaction.getCountry(), 
                    "Name matched in sanctions list", 
                    0.9
                );
            }
            
            // Check partial name matches
            if (sanctionsChecker.checkPartialName(transaction.getSender())) {
                return SanctionsMatchResult.localMatch(
                    transaction.getSender(), 
                    transaction.getCountry(), 
                    "Partial name match in sanctions list", 
                    0.7
                );
            }
            
            return SanctionsMatchResult.noMatch();
            
        } catch (Exception e) {
            logger.error("Error during sanctions check: {}", e.getMessage(), e);
            return SanctionsMatchResult.noMatch();
        }
    }
    
    @Override
    public List<Rule> getActiveRules() {
        List<Rule> allRules = new ArrayList<>();
        allRules.addAll(hardcodedRules);
        allRules.addAll(jsonRules);
        return allRules;
    }
    
    @Override
    public void addRule(Rule rule) {
        if (rule != null) {
            ruleRegistry.put(rule.getDescription(), rule);
            hardcodedRules.add(rule);
            logger.info("Added new rule: {}", rule.getDescription());
        }
    }
    
    @Override
    public void removeRule(String ruleDescription) {
        Rule removed = ruleRegistry.remove(ruleDescription);
        if (removed != null) {
            hardcodedRules.remove(removed);
            jsonRules.remove(removed);
            logger.info("Removed rule: {}", ruleDescription);
        }
    }
    
    @Override
    public Map<String, Object> getEvaluationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransactionsEvaluated", totalTransactionsEvaluated.get());
        stats.put("totalAlertsGenerated", totalAlertsGenerated.get());
        stats.put("totalSanctionsMatches", totalSanctionsMatches.get());
        stats.put("activeRulesCount", ruleRegistry.size());
        stats.put("hardcodedRulesCount", hardcodedRules.size());
        stats.put("jsonRulesCount", jsonRules.size());
        stats.put("ruleMatchCounts", new HashMap<>(ruleMatchCounts));
        stats.put("highRiskCountriesCount", riskScoringService.getHighRiskCountries().size());
        return stats;
    }
    
    // Helper methods
    
    private boolean isValidTransaction(Transaction transaction) {
        return com.leizo.admin.util.TransactionUtils.isValidTransaction(transaction);
    }
    
    private List<Rule> findMatchingRules(Transaction transaction) {
        BigDecimal normalizedAmount = normalizeAmount(transaction);
        return getActiveRules().stream()
            .filter(rule -> rule != null && rule.appliesTo(transaction, normalizedAmount))
            .collect(Collectors.toList());
    }
    
    private BigDecimal normalizeAmount(Transaction transaction) {
        // TODO: Implement currency conversion when CurrencyConversionService is available
        return transaction.getAmount();
    }
    
    private boolean isHighRiskCountry(String country) {
        return country != null && riskScoringService.getHighRiskCountries().contains(country.trim());
    }
    
    private boolean hasManualFlag(Transaction transaction) {
        return com.leizo.admin.util.TransactionUtils.hasManualFlag(transaction);
    }
    
    private boolean isFrequentSender(Transaction transaction) {
        // TODO: Implement with TransactionHistoryService
        return false;
    }
    
    private boolean isHighRiskCurrency(Transaction transaction) {
        return com.leizo.admin.util.TransactionUtils.isHighRiskCurrency(transaction);
    }
    
    private int calculatePriorityScore(Transaction transaction, Rule rule, int baseRiskScore) {
        int priority = baseRiskScore;
        
        // Add rule sensitivity weight
        priority += com.leizo.admin.util.RuleUtils.getSensitivityWeight(rule);
        
        // Add manual flag bonus
        if (hasManualFlag(transaction)) {
            priority += 10;
        }
        
        // Add high-risk country bonus
        if (isHighRiskCountry(transaction.getCountry())) {
            priority += 5;
        }
        
        return Math.min(priority, 100);
    }
    
    private String getRiskScoreLevel(int score) {
        return com.leizo.admin.util.AlertUtils.getRiskScoreLevel(score);
    }
    
    private Alert createSanctionsAlert(Transaction transaction, SanctionsMatchResult sanctionsResult) {
        Alert alert = new Alert();
        alert.setMatchedEntityName(sanctionsResult.getMatchedEntityName());
        alert.setMatchedList(sanctionsResult.getMatchedList());
        alert.setMatchReason(sanctionsResult.getFormattedReason());
        alert.setTransactionId(transaction.getId());
        alert.setReason(sanctionsResult.getFormattedReason());
        alert.setTimestamp(LocalDateTime.now());
        alert.setAlertType("SANCTIONS");
        alert.setPriorityLevel("HIGH");
        alert.setPriorityScore(100);
        return alert;
    }
    
    private Alert createRuleAlert(Transaction transaction, Rule rule, String reason, int priorityScore) {
        Alert alert = new Alert();
        alert.setTransactionId(transaction.getId());
        alert.setReason(reason);
        alert.setTimestamp(LocalDateTime.now());
        alert.setAlertType("RULE_MATCH");
        alert.setPriorityScore(priorityScore);
        alert.updatePriorityLevel();
        return alert;
    }
    
    private void logEvaluation(Transaction transaction, AlertDecisionResult alertResult, int riskScore) {
        String eventType = alertResult.shouldTriggerAlert() ? "ALERT_TRIGGERED" : "TRANSACTION_CLEARED";
        String details = String.format("Risk Score: %d, Priority Score: %d, Reason: %s", 
                                     riskScore, alertResult.getPriorityScore(), alertResult.getReason());
        
        loggerService.logEvent(eventType, transaction.getSender(), details);
    }
} 