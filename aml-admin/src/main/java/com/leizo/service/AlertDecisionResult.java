package com.leizo.service;

import com.leizo.admin.entity.Alert;
import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.Transaction;

import java.util.List;

/**
 * Result of alert decision evaluation
 * Contains all information about whether an alert should be triggered
 * and the reasoning behind the decision
 */
public class AlertDecisionResult {
    
    private final boolean shouldTriggerAlert;
    private final Alert alert;
    private final Rule matchedRule;
    private final Transaction transaction;
    private final String reason;
    private final int riskScore;
    private final int priorityScore;
    private final List<String> matchedRuleDescriptions;
    private final boolean isDuplicate;
    private final boolean isInCooldown;
    private final SanctionsMatchResult sanctionsResult;
    
    public AlertDecisionResult(boolean shouldTriggerAlert, Alert alert, Rule matchedRule, 
                             Transaction transaction, String reason, int riskScore, 
                             int priorityScore, List<String> matchedRuleDescriptions,
                             boolean isDuplicate, boolean isInCooldown, 
                             SanctionsMatchResult sanctionsResult) {
        this.shouldTriggerAlert = shouldTriggerAlert;
        this.alert = alert;
        this.matchedRule = matchedRule;
        this.transaction = transaction;
        this.reason = reason;
        this.riskScore = riskScore;
        this.priorityScore = priorityScore;
        this.matchedRuleDescriptions = matchedRuleDescriptions;
        this.isDuplicate = isDuplicate;
        this.isInCooldown = isInCooldown;
        this.sanctionsResult = sanctionsResult;
    }
    
    // Static factory methods for common scenarios
    public static AlertDecisionResult noAlert(Transaction transaction, int riskScore) {
        return new AlertDecisionResult(false, null, null, transaction, 
                "No rules matched", riskScore, riskScore, List.of(), false, false, null);
    }
    
    public static AlertDecisionResult duplicateAlert(Transaction transaction, String reason) {
        return new AlertDecisionResult(false, null, null, transaction, reason, 0, 0, 
                List.of(), true, false, null);
    }
    
    public static AlertDecisionResult cooldownAlert(Transaction transaction, String reason) {
        return new AlertDecisionResult(false, null, null, transaction, reason, 0, 0, 
                List.of(), false, true, null);
    }
    
    public static AlertDecisionResult sanctionsAlert(Transaction transaction, Alert alert, 
                                                   SanctionsMatchResult sanctionsResult) {
        return new AlertDecisionResult(true, alert, null, transaction, 
                "Sanctions violation detected", 100, 100, List.of("SANCTIONS"), 
                false, false, sanctionsResult);
    }
    
    public static AlertDecisionResult ruleAlert(Transaction transaction, Alert alert, 
                                              Rule matchedRule, String reason, 
                                              int riskScore, int priorityScore, 
                                              List<String> matchedRuleDescriptions) {
        return new AlertDecisionResult(true, alert, matchedRule, transaction, reason, 
                riskScore, priorityScore, matchedRuleDescriptions, false, false, null);
    }
    
    // Getters
    public boolean shouldTriggerAlert() { return shouldTriggerAlert; }
    public Alert getAlert() { return alert; }
    public Rule getMatchedRule() { return matchedRule; }
    public Transaction getTransaction() { return transaction; }
    public String getReason() { return reason; }
    public int getRiskScore() { return riskScore; }
    public int getPriorityScore() { return priorityScore; }
    public List<String> getMatchedRuleDescriptions() { return matchedRuleDescriptions; }
    public boolean isDuplicate() { return isDuplicate; }
    public boolean isInCooldown() { return isInCooldown; }
    public SanctionsMatchResult getSanctionsResult() { return sanctionsResult; }
    
    @Override
    public String toString() {
        return "AlertDecisionResult{" +
                "shouldTriggerAlert=" + shouldTriggerAlert +
                ", reason='" + reason + '\'' +
                ", riskScore=" + riskScore +
                ", priorityScore=" + priorityScore +
                ", isDuplicate=" + isDuplicate +
                ", isInCooldown=" + isInCooldown +
                '}';
    }
} 