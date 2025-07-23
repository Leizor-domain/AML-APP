package com.leizo.service;

import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.Transaction;
import java.math.BigDecimal;
import java.util.List;
public interface RuleEngine {
    List<Rule> getActiveRules();
    boolean applyRule(Transaction txn, Rule rule, BigDecimal normalizedAmount);

}
