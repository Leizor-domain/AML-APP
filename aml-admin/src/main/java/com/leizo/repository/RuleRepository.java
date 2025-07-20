package com.leizo.repository;

import com.leizo.enums.RuleSensitivity;
import com.leizo.model.Rule;
import com.leizo.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * RuleRepository defines the contract for storing and managing AML (Anti-Money Laundering) rules.
 * Each rule contains metadata and a condition that is used to evaluate transactions.
 *
 * This repository supports adding, retrieving, and clearing rule definitions.
 */
public interface RuleRepository {

    // === Core CRUD ===
    /**
     * Persists a fully constructed Rule object.
     *
     * @param rule the Rule instance to be saved
     */
    void saveRule(Rule rule);

    /**
     * Retrieves a list of all currently registered rules.
     *
     * @return List of Rule objects
     */
    List<Rule> getAllRules();


    /**
     * Clears all existing rules from the repository.
     * Useful for reinitialization or refreshing rule sets.
     */
    void clearRules();

    // === Rule Builders ===
    /**
     * Adds a rule with a logical condition and a set of tags for categorization.
     *
     * @param description description of the rule's purpose
     * @param sensitivity rule's impact level (LOW, MEDIUM, HIGH)
     * @param condition   logic to evaluate a Transaction against
     * @param tags        labels used for categorization or filtering
     */
    void addRule(String description, RuleSensitivity sensitivity, BiPredicate<Transaction, BigDecimal> condition, Set<String> tags);

    /**
     * Adds a rule with a logical condition, without specific tags.
     *
     * @param description description of the rule
     * @param sensitivity rule sensitivity level
     * @param condition   condition to evaluate
     */
    void addRule(String description, RuleSensitivity sensitivity, BiPredicate<Transaction, BigDecimal> condition);

    /**
     * Adds a rule using only metadata and tag labels (no custom logic).
     * Can be used for tagging rules that might be linked to predefined types.
     *
     * @param description textual description
     * @param sensitivity sensitivity level
     * @param tags        optional tags as varargs
     */
    void addRule(String description, RuleSensitivity sensitivity, String... tags);

    /**
     * Adds a rule with just a description and sensitivity.
     * Acts as a placeholder or for pre-defined logic dispatch.
     *
     * @param description description of the rule
     * @param sensitivity risk sensitivity level
     */
    void addRule(String description, RuleSensitivity sensitivity);

}