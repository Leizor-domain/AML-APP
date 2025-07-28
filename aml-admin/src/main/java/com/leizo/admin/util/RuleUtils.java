package com.leizo.admin.util;

import com.leizo.admin.entity.Rule;
import com.leizo.enums.RuleSensitivity;

/**
 * Utility class for rule-related operations to eliminate code duplication
 */
public class RuleUtils {
    
    /**
     * Gets the sensitivity weight for a rule
     * 
     * @param rule the rule to get sensitivity weight for
     * @return the sensitivity weight, or 0 if rule or sensitivity is null
     */
    public static int getSensitivityWeight(Rule rule) {
        if (rule == null || rule.getSensitivity() == null) {
            return 0;
        }
        return rule.getSensitivity().getWeight();
    }
    
    /**
     * Gets the priority score adjustment based on rule sensitivity
     * 
     * @param rule the rule to get priority adjustment for
     * @return the priority score adjustment
     */
    public static int getPriorityAdjustment(Rule rule) {
        if (rule == null || rule.getSensitivity() == null) {
            return 0;
        }
        
        switch(rule.getSensitivity()) {
            case HIGH: return 20;
            case MEDIUM: return 10;
            case LOW: return 5;
            default: return 0;
        }
    }
    
    /**
     * Checks if a rule is active and valid
     * 
     * @param rule the rule to check
     * @return true if the rule is active and valid, false otherwise
     */
    public static boolean isActiveRule(Rule rule) {
        return rule != null && 
               rule.getDescription() != null && 
               !rule.getDescription().trim().isEmpty();
    }
} 