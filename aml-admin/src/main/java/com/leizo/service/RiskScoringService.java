package com.leizo.service;

import com.leizo.enums.RiskScore;
import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.Transaction;

import java.util.Set;

public interface RiskScoringService {

    Set<String> getHighRiskCountries();

    RiskScore assessRisk(Transaction txn);

    int calculateRiskScore(Transaction txn);

    int calculateRiskScore(Transaction txn, Rule rule);
}
