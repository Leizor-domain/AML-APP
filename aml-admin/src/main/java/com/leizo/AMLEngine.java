//Dear Programmer:
//When I wrote this code. Only God and I (pretty sure chatGPT lost its mind at some point)
//Knew how it worked.
//Now only God knows it!
//
//Therefore, if you are trying to optimize
//this routine, and it fails (most certainly)
//please increase this counter as a warning
//to the next person
//total_wasted_hours_here = 13
package com.leizo;

import java.math.*;
import java.util.*;

import com.leizo.enums.IngestionStatus;
import com.leizo.enums.RuleSensitivity;
import com.leizo.exception.UnauthorizedAccessException;
import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.SanctionedEntity;
import com.leizo.admin.entity.Transaction;
import com.leizo.admin.entity.Alert;
import com.leizo.admin.entity.User;
import com.leizo.model.IngestionResult;
import com.leizo.loader.SanctionListLoader;
import com.leizo.service.*;
import com.leizo.admin.util.AlertFSS;
import com.leizo.admin.util.RuleFSS;
import com.leizo.admin.util.SanctionedEntityFSS;
import com.leizo.admin.util.TransactionFSS;


public class AMLEngine {

    // All backend services used by the engine
    private final TransactionService transactionService;
    private final RuleEngine ruleEngine;
    private final SanctionsChecker sanctionsChecker;
    private final AlertService alertService;
    // ExchangeRateService removed - will be replaced with new CurrencyConversionService
    private final CaseManager caseManager;
    private final LoggerService loggerService;
    private final RiskScoringService riskScoringService;
    private final BehavioralPatternDetector behavioralPatternDetector;
    private final TransactionHistoryService transactionHistoryService;
    private final AlertHistoryService alertHistoryService;
    private final SanctionListLoader sanctionListLoader;


    // Constructor - ensures all services are available at runtime
    public AMLEngine(TransactionService transactionService,
                     RuleEngine ruleEngine,
                     SanctionsChecker sanctionsChecker,
                     AlertService alertService,
                     // ExchangeRateService parameter removed - will be replaced with new CurrencyConversionService
                     CaseManager caseManager,
                     LoggerService loggerService,
                     RiskScoringService riskScoringService,
                     BehavioralPatternDetector behavioralPatternDetector,
                     TransactionHistoryService transactionHistoryService,
                     AlertHistoryService alertHistoryService,
                     SanctionListLoader sanctionListLoader
    ) {

        this.transactionService = Objects.requireNonNull(transactionService, "Transaction Service is required");
        this.ruleEngine = ruleEngine;
        this.sanctionsChecker = sanctionsChecker;
        this.alertService = alertService;
        // this.exchangeRateService = exchangeRateService; // Removed - will be replaced with new CurrencyConversionService
        this.caseManager = caseManager;
        this.loggerService = loggerService;
        this.riskScoringService = Objects.requireNonNull(riskScoringService);
        this.behavioralPatternDetector = behavioralPatternDetector;
        this.transactionHistoryService = transactionHistoryService;
        this.alertHistoryService = alertHistoryService;
        this.sanctionListLoader = sanctionListLoader;
    }

    //Service 1
    public IngestionResult ingestTransaction(Transaction txn, User user) {
        if (user == null || (!user.isAnalyst() && !user.isAdmin())) {
            return logAndThrowUnauthorized(user);
        }
        if (isValid(txn)) {
            return logAndReturn(IngestionStatus.INVALID_INPUT, false, "SYSTEM", "INVALID_INPUT", "Rejected or malformed transaction", 0);
        }
        try {
            transactionService.saveTransaction(txn);
            // Save to history
            transactionHistoryService.saveTransaction(txn);

            // Get sender history
            List<Transaction> history = transactionHistoryService.getTransactionHistory(txn.getSender());

            // Detect deviation
            boolean abnormal = behavioralPatternDetector.detectDeviations(txn, history);

            if (abnormal) {
                logEvent("BEHAVIORAL_ALERT", txn.getSender(), "Deviation detected from historical pattern!");
            }

            txn.setRiskScore(riskScoringService.assessRisk(txn));
            int score = riskScoringService.calculateRiskScore(txn);
            if (abnormal) score += 10;

            logEvent("RISK_SCORE_CALCULATED", txn.getSender(), "Risk score: " + score);
            logEvent("TRANSACTION_RECEIVED", txn.getSender(), "Transaction saved and queued for evaluation");

            Alert triggeredAlert = evaluateTransaction(txn);
            logEvent("TRANSACTION_SUCCESS", txn.getSender(), "Transaction evaluated successfully");

            return new IngestionResult(IngestionStatus.SUCCESS, triggeredAlert != null,
                    triggeredAlert != null ? triggeredAlert.getAlertId() : null, score);
        } catch (Exception e) {
            return logAndReturn(IngestionStatus.EVALUATION_FAILED, false, txn.getSender(), "Evaluation_Error", "Transaction evaluation failed" + e.getMessage(), 0);
        }
    }

    private IngestionResult logAndReturn(IngestionStatus status, boolean alertTriggered, String actor, String eventType, String details, int riskScore) {
        logEvent(eventType, actor, details);
        return new IngestionResult(status, alertTriggered, null, riskScore);
    }

    private IngestionResult logAndThrowUnauthorized(User user) {
        String name = (user != null) ? user.getUserName() : "Unknown";
        logEvent("ACCESS_DENIED", "SYSTEM", "Unauthorized user: " + name);
        throw new UnauthorizedAccessException("Access Denied: Only analysts and admins can ingest Transactions");
    }

    // Basic validation before processing a transaction
    private boolean isValid(Transaction txn) {
        return txn != null &&
                txn.getSender() != null &&
                txn.getReceiver() != null &&
                txn.getAmount() != null &&
                txn.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
                txn.getCurrency() != null &&
                txn.getCountry() != null;
    }

    //Service 2
    // Evaluation logic (sanction screening and rule engine matching)
    public Alert evaluateTransaction(Transaction txn) {
        if (isSanctioned(txn)) {
            return triggerAlert(txn, null, "Sanction violation detected.");
        }

        BigDecimal normalizedAmount = normalizeAmount(txn);
        List<Rule> rules = ruleEngine.getActiveRules();

        if (rules == null || rules.isEmpty()) {
            logEvent("RULE_ENGINE_WARNING", "SYSTEM", "No active rules available for evaluation.");
            return null;
        }

        List<Rule> matchedRules = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule != null && rule.appliesTo(txn, normalizedAmount)) {
                matchedRules.add(rule);
            }
        }

        // Log all matched rule descriptions + tags
        if (!matchedRules.isEmpty()) {
            StringBuilder log = new StringBuilder("Matched Rules:");
            for (Rule rule : matchedRules) {
                log.append("\n• ").append(rule.getDescription())
                        .append(" | Sensitivity: ").append(rule.getSensitivity())
                        .append(" | Tags: ").append(rule.getTags());
            }
            logEvent("RULE_MATCH_LOG", txn.getSender(), log.toString());
        }

        // Choose the rule with the highest sensitivity
        Rule selectedRule = matchedRules.stream()
                .max(Comparator.comparingInt(r -> r.getSensitivity().getWeight()))
                .orElse(null);

        return selectedRule != null
                ? triggerAlert(txn, selectedRule, "Rule matched: " + selectedRule.getDescription())
                : null;
    }

    private Rule findMatchingRule(Transaction txn, BigDecimal normalizedAmount) {
        for (Rule rule : ruleEngine.getActiveRules()) {
            if (rule != null && rule.appliesTo(txn, normalizedAmount)) {
                return rule;
            }
        }
        return null;
    }

    // Triggers an alert and pushes to case manager
    private Alert triggerAlert(Transaction txn, Rule rule, String reason) {
        // Duplicate alert check
        if (alertService.isDuplicateAlert(txn, reason)) {
            return logAndReturnNull("ALERT_SKIPPED", txn.getSender(), "Duplicate alert ignored: " + reason);
        }

        // Cooldown suppression check
        String sender = txn.getSender();
        String ruleId = (rule != null && rule.getDescription() != null) ? rule.getDescription() : "sanction";
        if (alertService.isInCooldown(sender, ruleId)) {
            return logAndReturnNull("ALERT_SKIPPED", sender, "Cooldown active: similar alert suppressed for [" + ruleId + "]");
        }
        //check for duplicates
        if (alertService.isDuplicateAlert(txn, reason)) {
            return logAndReturnNull("ALERT_SKIPPED", sender, "Duplicate alert ignored: " + reason);
        }

        // Base risk score calculation
        int score = riskScoringService.calculateRiskScore(txn);

        // Rule sensitivity weight
        int ruleWeight = (rule != null && rule.getSensitivity() != null)
                ? rule.getSensitivity().getWeight()
                : 0;

        // Manual override flag check
        boolean isManuallyFlagged = txn.getMetadata() != null && txn.getMetadata() instanceof Map && ((Map<?,?>)txn.getMetadata()).keySet().stream().anyMatch(k -> k.toString().equalsIgnoreCase("flagged"));

        // Country risk check
        boolean fromHighRiskCountry = riskScoringService.getHighRiskCountries()
                .contains(txn.getCountry() != null ? txn.getCountry().trim() : "");

        // Priority score computation
        int totalPriority = score + ruleWeight;
        if (isManuallyFlagged) totalPriority += 10;
        if (fromHighRiskCountry) totalPriority += 5;

        int priority = Math.min(totalPriority, 100);  // cap the priority to 100

        // Generate alert
        Alert alert = alertService.generateAlert(txn, rule, reason);
        alert.setPriorityScore(priority);
        alert.updatePriorityLevel();

        // Register cooldown after alert is generated
        alertService.registerCooldown(sender, ruleId);

        // Log all critical metadata
        StringBuilder logDetails = new StringBuilder();
        logDetails.append("Alert ID: ").append(alert.getAlertId())
                .append(" | Priority Score: ").append(priority)
                .append(" | Risk Score: ").append(score)
                .append(" | Reason: ").append(reason);

        if (rule != null && rule.getSensitivity() != null) {
            logDetails.append(" | Rule Sensitivity: ")
                    .append(rule.getSensitivity().name())
                    .append(" (Weight: ").append(rule.getSensitivity().getWeight()).append(")");
        }

        if (isManuallyFlagged) {
            logDetails.append(" | Flag: MANUAL_OVERRIDE");
        }

        if (fromHighRiskCountry) {
            logDetails.append(" | Origin: HIGH_RISK_COUNTRY");
        }

        logEvent("ALERT_TRIGGERED", txn.getSender(), logDetails.toString());
        loggerService.logAlert(alert);
        caseManager.reviewAlert(alert);
        alertHistoryService.saveAlert(alert);

        return alert;
    }

    private Alert logAndReturnNull(String eventType, String actor, String details) {
        logEvent(eventType, actor, details);
        return null;
    }

    // Central logging utility
    private void logEvent(String eventType, String actor, String details) {
        loggerService.logEvent(eventType, actor, details);
    }

    // Checks if sender, receiver, or origin country is sanctioned
    private boolean isSanctioned(Transaction txn) {
        try {
            return sanctionsChecker.isSanctionedEntity(txn.getSender(), txn.getCountry(), txn.getDob(), "Any")
                    || sanctionsChecker.checkCountry(txn.getCountry())
                    || sanctionsChecker.checkName(txn.getSender())
                    || sanctionsChecker.checkPartialName(txn.getSender());
        } catch (Exception e) {
            logEvent("SANCTIONS_CHECK_ERROR", "SYSTEM", "Error during sanctions check: " + e.getMessage());
            return false;
        }
    }

    private void logSanctionError(Exception e) {
        logEvent("SANCTIONS_CHECK_ERROR", "SYSTEM", "Error during sanctions check: " + e.getMessage());
    }

    // Converts transaction amount to EUR
    private BigDecimal normalizeAmount(Transaction txn) {
        try {
            // TODO: Replace with new CurrencyConversionService when implemented
            // For now, return the original amount to avoid breaking existing functionality
            logEvent("CURRENCY_CONVERSION_WARNING", "SYSTEM",
                    "Currency conversion temporarily disabled - using original amount");
            return txn.getAmount();
        } catch (Exception e) {
            logEvent("CURRENCY_CONVERSION_ERROR", "SYSTEM",
                    "Failed to convert amount: " + e.getMessage());
            return txn.getAmount();
        }
    }


    //FSS Methods: Filtering Sorting and Searching
    // ======================
    // Transaction FSS: Filter, Search, Sort using TransactionFSS
    // ======================

    //Filter transactions by sender, country, and amount range.
    public List<Transaction> filterTransactions(String sender, String country, BigDecimal min, BigDecimal max) {
        return TransactionFSS.filter(transactionHistoryService.getAllTransactions(), sender, country, min, max);
    }

    //Search transactions by receiver name.
    public List<Transaction> searchTransactionsByReceiver(String receiver) {
        return TransactionFSS.searchByReceiver(transactionHistoryService.getAllTransactions(), receiver);
    }

    //Sort transactions by amount using merge sort.
    public Transaction[] sortTransactionsByAmount(boolean descending) {
        return TransactionFSS.sortByAmount(transactionHistoryService.getAllTransactions(), descending);
    }

    //Sort transactions by risk score using merge sort.
    public Transaction[] sortTransactionsByRiskScore(boolean descending) {
        return TransactionFSS.sortByRiskScore(transactionHistoryService.getAllTransactions(), descending);
    }


    // ======================
    // Alert FSS: Filter, Search, Sort using AlertFSS
    // ======================

    //Filter alerts by sender and priority level.
    public List<Alert> filterAlerts(String sender, String priorityLevel) {
        return AlertFSS.filter(alertHistoryService.getAllAlerts(), sender, priorityLevel);
    }

    //Search alerts by reason keyword.
    public List<Alert> searchAlertsByReason(String keyword) {
        return AlertFSS.searchAlertsByReason(alertHistoryService.getAllAlerts(), keyword);
    }

    //Sort alerts by priority score using merge sort.
    public Alert[] sortAlertsByPriority(boolean descending) {
        Alert[] array = alertHistoryService.getAllAlerts().toArray(new Alert[0]);
        return AlertFSS.sortByPriority(array, descending);
    }


    // ======================
    // Rule FSS: Filter, Search, Sort using RuleFSS
    // ======================

    //Filter rules by sensitivity and optional tag.
    public List<Rule> filterRules(RuleSensitivity sensitivity, String tag) {
        return RuleFSS.filter(ruleEngine.getActiveRules(), sensitivity, tag);
    }

    //Search rules by description keyword.
    public List<Rule> searchRules(String keyword) {
        return RuleFSS.search(ruleEngine.getActiveRules(), keyword);
    }

    //Sort rules by sensitivity weight (ascending or descending).
    public Rule[] sortRulesBySensitivity(boolean descending) {
        return RuleFSS.sortBySensitivity(ruleEngine.getActiveRules(), descending);
    }

    // ======================
    // SanctionedEntity FSS: Filter, Search, Sort using SanctionedEntityFSS
    // ======================

    //Filter sanctioned entities by country.
    public List<SanctionedEntity> filterEntitiesByCountry(String country) {
        return SanctionedEntityFSS.filterByCountry(sanctionListLoader.getConsolidatedList(), country);
    }

    //Search sanctioned entities by name containing a keyword.
    public List<SanctionedEntity> searchEntitiesByName(String keyword) {
        return SanctionedEntityFSS.searchByName(sanctionListLoader.getConsolidatedList(), keyword);
    }

    //Sort sanctioned entities by name A-Z or Z-A.
    public SanctionedEntity[] sortEntitiesByName(boolean descending) {
        return SanctionedEntityFSS.sortByName(sanctionListLoader.getConsolidatedList(), descending);
    }
}

