package com.leizo.service;

import com.leizo.pojo.entity.Rule;
import com.leizo.pojo.entity.Transaction;
import java.math.BigDecimal;
import java.util.List;
public interface RuleEngine {
    List<Rule> getActiveRules();
    boolean applyRule(Transaction txn, Rule rule, BigDecimal normalizedAmount);

}
