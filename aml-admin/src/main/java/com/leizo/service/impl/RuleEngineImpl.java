package com.leizo.service.impl;

import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.Transaction;
import com.leizo.service.RuleEngine;

import java.math.BigDecimal;
import java.util.*;

public class RuleEngineImpl implements RuleEngine {

    private final List<Rule> rules = new ArrayList<>();

    public RuleEngineImpl() {
        // Optionally, load rules from a file or static source here
    }

    @Override
    public List<Rule> getActiveRules() {
        return rules;
    }

    @Override
    public boolean applyRule(Transaction txn, Rule rule, BigDecimal normalizedAmount) {
        return rule != null && rule.appliesTo(txn, normalizedAmount);
    }
}
