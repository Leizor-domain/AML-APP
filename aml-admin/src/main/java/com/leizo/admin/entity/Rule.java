package com.leizo.admin.entity;

import com.leizo.enums.RuleSensitivity;

import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.function.BiPredicate;

public class Rule {

    private final String description;
    private final RuleSensitivity sensitivity;
    private final BiPredicate<Transaction, BigDecimal> condition;
    private final Set<String> tags;

    public Rule(String description, RuleSensitivity sensitivity, BiPredicate<Transaction, BigDecimal> condition) {
        this.description = description;
        this.sensitivity = sensitivity;
        this.condition = condition;
        this.tags = new HashSet<>();
    }

    // Optional constructor with tags directly
    public Rule(String description, RuleSensitivity sensitivity, BiPredicate<Transaction, BigDecimal> condition, Set<String> tags) {
        this.description = description;
        this.sensitivity = sensitivity;
        this.condition = condition;
        this.tags = tags != null ? tags : new HashSet<>();
    }

    public String getDescription() {
        return description;
    }

    public RuleSensitivity getSensitivity() {
        return sensitivity;
    }

    public boolean appliesTo(Transaction txn, BigDecimal normalizedAmount) {
        return condition != null && condition.test(txn, normalizedAmount);
    }

    public Set<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void setTags(Set<String> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    @Override
    public String toString() {
        return "Rule{" +
                "description='" + description + '\'' +
                ", sensitivity=" + sensitivity +
                ", tags=" + tags +
                '}';
    }
} 