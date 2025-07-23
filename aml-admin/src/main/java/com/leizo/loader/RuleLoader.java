package com.leizo.loader;

import com.fasterxml.jackson.databind.*;
import com.leizo.enums.RuleSensitivity;
import com.leizo.admin.entity.Transaction;
import com.leizo.admin.entity.Rule;

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
public class RuleLoader {
    private final List<Rule> rules;

    /**
     * Constructor for injecting the RuleRepository implementation
     * that will store the loaded rules.
     *
     * @param ruleRepository the in-memory rule storage handler
     */

    public RuleLoader(List<Rule> rules) {
        this.rules = rules;
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
            JsonNode root = mapper.readTree(new File(filePath));
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
            default -> null;
        };
    }
}

