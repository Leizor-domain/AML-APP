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
import com.leizo.common.entity.Users;
import com.leizo.model.IngestionResult;
import com.leizo.loader.SanctionListLoader;
import com.leizo.service.*;
import com.leizo.admin.util.AlertFSS;
import com.leizo.admin.util.RuleFSS;
import com.leizo.admin.util.SanctionedEntityFSS;
import com.leizo.admin.util.TransactionFSS;

/**
 * AMLEngine - Refactored to use unified services
 * 
 * This engine now delegates to specialized services:
 * - TransactionEvaluatorService: Handles all transaction evaluation logic
 * - AlertDecisionEngine: Manages alert lifecycle and suppression
 * - Maintains backward compatibility with existing FSS methods
 */
public class AMLEngine {

    // Core unified services
    private final TransactionEvaluatorService transactionEvaluator;
    private final AlertDecisionEngine alertDecisionEngine;
    
    // Legacy services for backward compatibility
    private final TransactionService transactionService;
    private final RuleEngine ruleEngine;
    private final SanctionsChecker sanctionsChecker;
    private final AlertService alertService;
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
                     CaseManager caseManager,
                     LoggerService loggerService,
                     RiskScoringService riskScoringService,
                     BehavioralPatternDetector behavioralPatternDetector,
                     TransactionHistoryService transactionHistoryService,
                     AlertHistoryService alertHistoryService,
                     SanctionListLoader sanctionListLoader,
                     TransactionEvaluatorService transactionEvaluator,
                     AlertDecisionEngine alertDecisionEngine) {

        this.transactionService = Objects.requireNonNull(transactionService, "Transaction Service is required");
        this.ruleEngine = ruleEngine;
        this.sanctionsChecker = sanctionsChecker;
        this.alertService = alertService;
        this.caseManager = caseManager;
        this.loggerService = loggerService;
        this.riskScoringService = Objects.requireNonNull(riskScoringService);
        this.behavioralPatternDetector = behavioralPatternDetector;
        this.transactionHistoryService = transactionHistoryService;
        this.alertHistoryService = alertHistoryService;
        this.sanctionListLoader = sanctionListLoader;
        
        // New unified services
        this.transactionEvaluator = Objects.requireNonNull(transactionEvaluator, "Transaction Evaluator Service is required");
        this.alertDecisionEngine = Objects.requireNonNull(alertDecisionEngine, "Alert Decision Engine is required");
    }

    /**
     * Main transaction ingestion method - now uses unified evaluator
     */
    public IngestionResult ingestTransaction(Transaction txn, Users user) {
        if (user == null || (!"analyst".equalsIgnoreCase(user.getRole()) && !"admin".equalsIgnoreCase(user.getRole()))) {
            return logAndThrowUnauthorized(user);
        }
        
        if (!com.leizo.admin.util.TransactionUtils.isValidTransaction(txn)) {
            return logAndReturn(IngestionStatus.INVALID_INPUT, false, "SYSTEM", "INVALID_INPUT", "Rejected or malformed transaction", 0);
        }
        
        try {
            // Save transaction
            transactionService.saveTransaction(txn);
            transactionHistoryService.saveTransaction(txn);

            // Get sender history for behavioral analysis
            List<Transaction> history = transactionHistoryService.getTransactionHistory(txn.getSender());
            boolean abnormal = behavioralPatternDetector.detectDeviations(txn, history);

            if (abnormal) {
                logEvent("BEHAVIORAL_ALERT", txn.getSender(), "Deviation detected from historical pattern!");
            }

            // Use unified transaction evaluator
            IngestionResult result = transactionEvaluator.evaluateTransaction(txn);
            
            // Process alert if generated
            if (result.isAlertGenerated() && result.getAlertId() != null) {
                // Alert processing is handled by AlertDecisionEngine within TransactionEvaluatorService
                logEvent("ALERT_PROCESSED", txn.getSender(), "Alert processed: " + result.getAlertId());
            }

            logEvent("TRANSACTION_SUCCESS", txn.getSender(), "Transaction evaluated successfully");
            return result;
            
        } catch (Exception e) {
            return logAndReturn(IngestionStatus.EVALUATION_FAILED, false, txn.getSender(), "Evaluation_Error", "Transaction evaluation failed: " + e.getMessage(), 0);
        }
    }

    /**
     * Transaction evaluation - now delegated to unified service
     */
    public Alert evaluateTransaction(Transaction txn) {
        AlertDecisionResult alertResult = transactionEvaluator.evaluateForAlert(txn);
        
        if (alertResult.shouldTriggerAlert()) {
            Alert alert = alertResult.getAlert();
            
            // Process the alert through the decision engine
            if (alertDecisionEngine.processAlert(alert)) {
                logEvent("ALERT_TRIGGERED", txn.getSender(), 
                        String.format("Alert %s created with priority %d", 
                                    alert.getAlertId(), alert.getPriorityScore()));
                return alert;
            } else {
                logEvent("ALERT_PROCESSING_FAILED", txn.getSender(), 
                        "Failed to process alert: " + alert.getAlertId());
                return null;
            }
        }
        
        return null;
    }

    /**
     * Get evaluation statistics from unified service
     */
    public Map<String, Object> getEvaluationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.putAll(transactionEvaluator.getEvaluationStats());
        stats.putAll(alertDecisionEngine.getAlertStats());
        return stats;
    }

    // Legacy helper methods for backward compatibility
    
    private IngestionResult logAndReturn(IngestionStatus status, boolean alertTriggered, String actor, String eventType, String details, int riskScore) {
        logEvent(eventType, actor, details);
        return new IngestionResult(status, alertTriggered, null, riskScore);
    }

    private IngestionResult logAndThrowUnauthorized(Users user) {
        String name = (user != null) ? user.getUsername() : "Unknown";
        logEvent("ACCESS_DENIED", "SYSTEM", "Unauthorized user: " + name);
        throw new UnauthorizedAccessException("Access Denied: Only analysts and admins can ingest Transactions");
    }



    // Central logging utility
    private void logEvent(String eventType, String actor, String details) {
        loggerService.logEvent(eventType, actor, details);
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
        return RuleFSS.filter(transactionEvaluator.getActiveRules(), sensitivity, tag);
    }

    //Search rules by description keyword.
    public List<Rule> searchRules(String keyword) {
        return RuleFSS.search(transactionEvaluator.getActiveRules(), keyword);
    }

    //Sort rules by sensitivity weight (ascending or descending).
    public Rule[] sortRulesBySensitivity(boolean descending) {
        return RuleFSS.sortBySensitivity(transactionEvaluator.getActiveRules(), descending);
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

