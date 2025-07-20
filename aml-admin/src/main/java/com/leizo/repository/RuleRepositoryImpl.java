package com.leizo.repository;

import com.leizo.enums.RuleSensitivity;
import com.leizo.model.Rule;
import com.leizo.model.Transaction;

import java.util.*;
import java.math.BigDecimal;
import java.util.function.BiPredicate;

/**
 * RuleRepositoryImpl is the concrete implementation of the RuleRepository interface.
 * It manages AML rule definitions in memory and provides methods to load, create,
 * and retrieve rules dynamically.
 */
public class RuleRepositoryImpl implements RuleRepository {

    // Internal in-memory list for storing Rule objects
    private final List<Rule> rules = new ArrayList<>();

    /**
     * Default constructor that automatically loads a set of pre-defined rules.
     */
    public RuleRepositoryImpl() {
        loadDefaultRules();
    }

    // ===============================
    // CORE RULE MANAGEMENT FUNCTIONS
    // ===============================

    /**
     * Saves a custom Rule object to the repository.
     *
     * @param rule Rule object to be added
     */
    @Override
    public void saveRule(Rule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }

    /**
     * Retrieves an unmodifiable list of all rules currently stored.
     *
     * @return List of Rule objects
     */
    @Override
    public List<Rule> getAllRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Clears all rules from the repository.
     */
    @Override
    public void clearRules() {
        rules.clear();
    }

    /**
     * Adds a rule with a specific logical condition and a set of tags.
     *
     * @param description Textual explanation of the rule
     * @param sensitivity Rule sensitivity (LOW, MEDIUM, HIGH)
     * @param condition   Predicate logic to evaluate transactions
     * @param tags        Set of descriptive tags (can be null)
     */
    @Override
    public void addRule(String description, RuleSensitivity sensitivity,
                        BiPredicate<Transaction, BigDecimal> condition,
                        Set<String> tags) {
        if (description != null && sensitivity != null && condition != null) {
            rules.add(new Rule(description, sensitivity, condition, tags));
        }
    }

    /**
     * Adds a rule with logic but without tags.
     *
     * @param description Rule description
     * @param sensitivity Risk level
     * @param condition   Predicate condition
     */
    @Override
    public void addRule(String description, RuleSensitivity sensitivity,
                        BiPredicate<Transaction, BigDecimal> condition) {
        addRule(description, sensitivity, condition, null);
    }

    /**
     * Adds a rule using just description, sensitivity, and tag strings.
     * Automatically resolves logic via the rule description.
     *
     * @param description Description text
     * @param sensitivity Rule sensitivity level
     * @param tags        Optional string tags
     */
    @Override
    public void addRule(String description, RuleSensitivity sensitivity, String... tags) {
        BiPredicate<Transaction, BigDecimal> resolved = createConditionForRule(description);
        Set<String> tagSet = (tags != null) ? new HashSet<>(Arrays.asList(tags)) : new HashSet<>();
        addRule(description, sensitivity, resolved, tagSet);
    }

    /**
     * Adds a basic rule using only description and sensitivity.
     *
     * @param description Rule label
     * @param sensitivity Risk severity
     */
    @Override
    public void addRule(String description, RuleSensitivity sensitivity) {
        addRule(description, sensitivity, (String[]) null);
    }

    // ===============================
    // DEFAULT RULES
    // ===============================

    /**
     * Loads three predefined example rules into the repository.
     * These include high value, medium risk region, and low value logic rules.
     */
    private void loadDefaultRules() {
        addRule("High Value Transfer Rule", RuleSensitivity.HIGH,
                (txn, amt) -> amt.compareTo(new BigDecimal("10000")) > 0,
                new HashSet<>(Arrays.asList("value", "priority", "large_txn")));

        addRule("Medium Risk Region Transfer", RuleSensitivity.MEDIUM,
                (txn, amt) -> {
                    String country = txn.getCountry();
                    return country != null &&
                            (country.equalsIgnoreCase("Turkey") ||
                                    country.equalsIgnoreCase("Mexico"));
                },
                new HashSet<>(Arrays.asList("geo", "moderate_risk", "region_specific")));

        addRule("Low Value Routine Transfer", RuleSensitivity.LOW,
                (txn, amt) -> amt.compareTo(new BigDecimal("500")) < 0,
                new HashSet<>(Arrays.asList("routine", "small_amount", "low_risk")));
    }

    // ===============================
    // LOGIC RESOLVER DISPATCH
    // ===============================

    /**
     * Dynamically resolves logic for a rule based on its description.
     * Useful when adding rules from external sources like JSON.
     *
     * @param description Rule description
     * @return Matching BiPredicate logic, or default false logic
     */
    private BiPredicate<Transaction, BigDecimal> createConditionForRule(String description) {
        if ("High Value Transfer Rule".equalsIgnoreCase(description)) {
            return (txn, amt) -> amt.compareTo(new BigDecimal("10000")) > 0;
        }
        if ("Medium Risk Region Transfer".equalsIgnoreCase(description)) {
            return (txn, amt) -> {
                String country = txn.getCountry();
                return country != null &&
                        (country.equalsIgnoreCase("Turkey") || country.equalsIgnoreCase("Mexico"));
            };
        }
        if ("Low Value Routine Transfer".equalsIgnoreCase(description)) {
            return (txn, amt) -> amt.compareTo(new BigDecimal("500")) < 0;
        }

        // Default logic: rule doesn't match any predefined condition
        return (txn, amt) -> false;
    }
}
