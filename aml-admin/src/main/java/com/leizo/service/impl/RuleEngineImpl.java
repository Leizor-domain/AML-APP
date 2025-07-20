package com.leizo.service.impl;

import com.leizo.model.Rule;
import com.leizo.model.Transaction;
import com.leizo.repository.RuleRepositoryImpl;
import com.leizo.service.RuleEngine;

import java.math.BigDecimal;
import java.util.*;

public class RuleEngineImpl implements RuleEngine {

    private final RuleRepositoryImpl ruleRepository;

    public RuleEngineImpl(RuleRepositoryImpl ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public List<Rule> getActiveRules() {
        return ruleRepository.getAllRules();
    }

    @Override
    public boolean applyRule(Transaction txn, Rule rule, BigDecimal normalizedAmount) {
        return rule != null && rule.appliesTo(txn, normalizedAmount);
    }
}
