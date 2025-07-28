package com.leizo.service.impl;

import com.leizo.enums.RiskScore;
import com.leizo.loader.SanctionListLoader;
import com.leizo.pojo.entity.Rule;
import com.leizo.pojo.entity.Transaction;
import com.leizo.service.RiskScoringService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class RiskScoringServiceImpl implements RiskScoringService {

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("100000");
    private static final BigDecimal MEDIUM_AMOUNT_THRESHOLD = new BigDecimal("25000");

    private static final int HIGH_AMOUNT_SCORE = 25;
    private static final int HIGH_RISK_COUNTRY_SCORE = 25;
    private static final int FREQUENT_SENDER_SCORE = 25;
    private static final int MANUAL_FLAG_SCORE = 25;

    private final Set<String> sanctionedCountries;

    public RiskScoringServiceImpl(SanctionListLoader sanctionListLoader) {
        // Use your file service!
        this.sanctionedCountries = sanctionListLoader.getHighRiskCountries();
    }

    @Override
    public RiskScore assessRisk(Transaction txn) {
        int score = calculateRiskScore(txn);

        if(score >= 75) return RiskScore.HIGH;
        else if(score >= 40) return RiskScore.MEDIUM;
        else return RiskScore.LOW;
    }

    @Override
    public int calculateRiskScore(Transaction txn) {
        int score = 0;

        //Base criteria for scoring
        if (txn.getAmount() != null && isHighAmount(txn)) score += HIGH_AMOUNT_SCORE;
        if (isSanctionedCountry(txn)) score += HIGH_RISK_COUNTRY_SCORE;
        if (isFrequentSender(txn)) score += FREQUENT_SENDER_SCORE;
        if (hasManualFlag(txn)) score += MANUAL_FLAG_SCORE;


        return Math.min(score, 100);
    }

    @Override
    public int calculateRiskScore(Transaction txn, Rule rule) {
        int score = calculateRiskScore(txn);
        score += com.leizo.admin.util.RuleUtils.getPriorityAdjustment(rule);
        return Math.min(score, 100);
    }

    @Override
    public Set<String> getHighRiskCountries() {
        return sanctionedCountries;
    }

    private boolean isHighAmount(Transaction txn) {
        return txn.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) >= 0;
    }

    private boolean isSanctionedCountry(Transaction txn) {
        String country = txn.getCountry();
        return country != null && sanctionedCountries.contains(country.trim());
    }

    private boolean isFrequentSender(Transaction txn) {
        //placeholder: integrate with historical frequency Tracker
        return false;
    }

    private boolean hasManualFlag(Transaction txn) {
        return com.leizo.admin.util.TransactionUtils.hasManualFlag(txn);
    }

}
