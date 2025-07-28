package com.leizo.service.impl;

import com.leizo.pojo.entity.Rule;
import com.leizo.pojo.entity.Transaction;
import com.leizo.service.RuleEngine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
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
