package com.leizo.loader;

import com.fasterxml.jackson.databind.*;
import com.leizo.enums.RuleSensitivity;
import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Rule;
import org.springframework.stereotype.Component;

import java.util.*;
import java.math.*;
import java.io.*;
import java.util.function.BiPredicate;

/**
 * RuleLoader handles the logic of dynamically importing rule definitions
 * from a JSON file and registering them into the RuleRepository.
 *
 * This supports scalable, rule-driven AML detection logic with externalized configuration.
 */
@Component
public class RuleLoader {
    private final List<Rule> rules = new ArrayList<>();

    /**
     * Constructor for RuleLoader
     */
    public RuleLoader() {
        // Initialize with empty list, rules will be loaded when needed
    }

    /**
     * Loads a list of rule definitions from a given JSON file path.
     * Each rule must specify:
     * - description: textual summary of the rule
     * - sensitivity: LOW, MEDIUM, HIGH
     * - tags: metadata keywords
     * - type: determines internal BiPredicate condition mapping
     *
     * @param filePath path to the rules.json file
     */

    public void loadFromJson(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;
            
            // Try to load from classpath first, then from file system
            try {
                root = mapper.readTree(getClass().getClassLoader().getResourceAsStream(filePath.replace("classpath:", "")));
            } catch (Exception e) {
                root = mapper.readTree(new File(filePath));
            }
            
            for (JsonNode ruleNode : root) {
                // Extract rule properties
                String description = ruleNode.get("description").asText();
                RuleSensitivity sensitivity = RuleSensitivity.valueOf(ruleNode.get("sensitivity").asText().toUpperCase());

                // Extract tags array
                Set<String> tags = new HashSet<>();
                for (JsonNode tag : ruleNode.get("tags")) {
                    tags.add(tag.asText());
                }

                // Map rule type to a condition
                BiPredicate<Transaction, BigDecimal> condition = createConditionFromType(ruleNode.get("type").asText());

                // Add rule to repository if valid
                if (condition != null) {
                    Rule rule = new Rule(description, sensitivity, condition, tags);
                    rules.add(rule);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load rules from JSON: " + e.getMessage());
        }
    }

    /**
     * Maps a rule 'type' string to a predefined BiPredicate logic.
     * Future enhancement: move to external scripting or strategy pattern.
     *
     * @param type the rule type from JSON (e.g. "high_value")
     * @return BiPredicate evaluating the rule's matching logic
     */
    private BiPredicate<Transaction, BigDecimal> createConditionFromType(String type) {
        return switch (type.toLowerCase()) {
            case "high_value" -> (txn, amt) -> amt.compareTo(new BigDecimal("10000")) > 0;
            case "medium_risk_country" -> (txn, amt) -> {
                String country = txn.getCountry();
                return country != null && (country.equalsIgnoreCase("Turkey") || country.equalsIgnoreCase("Mexico"));
            };
            case "low_value" -> (txn, amt) -> amt.compareTo(new BigDecimal("500")) < 0;
            case "manual_flag" -> (txn, amt) -> com.leizo.admin.util.TransactionUtils.hasManualFlag(txn);
            case "high_risk_country" -> (txn, amt) -> {
                String country = txn.getCountry();
                return country != null && (
                    country.equalsIgnoreCase("Iran") || 
                    country.equalsIgnoreCase("North Korea") || 
                    country.equalsIgnoreCase("Syria") ||
                    country.equalsIgnoreCase("Russia") ||
                    country.equalsIgnoreCase("Venezuela") ||
                    country.equalsIgnoreCase("Belarus") ||
                    country.equalsIgnoreCase("Zimbabwe") ||
                    country.equalsIgnoreCase("Sudan") ||
                    country.equalsIgnoreCase("Libya") ||
                    country.equalsIgnoreCase("Somalia")
                );
            };
            case "fatf_grey_list" -> (txn, amt) -> {
                String country = txn.getCountry();
                return country != null && (
                    country.equalsIgnoreCase("Panama") || 
                    country.equalsIgnoreCase("Albania") || 
                    country.equalsIgnoreCase("Barbados") ||
                    country.equalsIgnoreCase("Cayman Islands") ||
                    country.equalsIgnoreCase("Turkey") ||
                    country.equalsIgnoreCase("Mexico")
                );
            };
            case "money_laundering_risk" -> (txn, amt) -> {
                String country = txn.getCountry();
                return country != null && (
                    country.equalsIgnoreCase("Cayman Islands") || 
                    country.equalsIgnoreCase("Bahamas") || 
                    country.equalsIgnoreCase("Bermuda") ||
                    country.equalsIgnoreCase("Cyprus") ||
                    country.equalsIgnoreCase("Liechtenstein") ||
                    country.equalsIgnoreCase("San Marino")
                );
            };
            case "always_true" -> (txn, amt) -> true;
            default -> null;
        };
    }
    
    /**
     * Get the list of loaded rules
     * @return List of loaded rules
     */
    public List<Rule> getRules() {
        return new ArrayList<>(rules);
    }
    
    /**
     * Get the number of loaded rules
     * @return Number of loaded rules
     */
    public int getRuleCount() {
        return rules.size();
    }
}

