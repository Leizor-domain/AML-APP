package com.leizo.service;

import com.leizo.model.Rule;
import com.leizo.model.Transaction;
import java.math.BigDecimal;
import java.util.List;
public interface RuleEngine {
    List<Rule> getActiveRules();
    boolean applyRule(Transaction txn, Rule rule, BigDecimal normalizedAmount);

}
