package com.leizo.service.impl;

import com.leizo.enums.RiskScore;
import com.leizo.loader.SanctionListLoader;
import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.Transaction;
import com.leizo.service.RiskScoringService;

import java.math.BigDecimal;
import java.util.*;

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

        if(rule != null && rule.getSensitivity() != null) {
            switch(rule.getSensitivity()) {
                case HIGH: score += 20; break;
                case MEDIUM: score += 10; break;
                case LOW: score += 5; break;
            }
        }
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
        return txn.getMetadata() instanceof Map && containsKeyIgnoreCase((Map<String, String>)txn.getMetadata(), "flagged");
    }

    private boolean containsKeyIgnoreCase(Map<String, String> map, String key) {
        return map.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(key));
    }

}
