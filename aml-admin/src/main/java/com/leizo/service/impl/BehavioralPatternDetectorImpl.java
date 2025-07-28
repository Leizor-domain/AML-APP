package com.leizo.service.impl;

import com.leizo.pojo.entity.Transaction;
import com.leizo.service.BehavioralPatternDetector;
import org.springframework.stereotype.Service;

import java.math.*;
import java.util.*;

@Service
public class BehavioralPatternDetectorImpl implements BehavioralPatternDetector {

    private static final BigDecimal AMOUNT_DEVIATION_THRESHOLD = new BigDecimal("2.0"); // e.g. 2x avg
    private static final int FREQUENCY_THRESHOLD = 5; // e.g. more than 5 txns in short period triggers

    @Override
    public boolean detectDeviations(Transaction txn, List<Transaction> history) {
        if (history == null || history.isEmpty()) {
            return false;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : history) {
            if (t.getAmount() != null) {
                total = total.add(t.getAmount());
            }
        }
        BigDecimal avg = total.divide(BigDecimal.valueOf(history.size()), BigDecimal
                .ROUND_HALF_UP);

        boolean amountOutlier = txn.getAmount() != null &&
                txn.getAmount().compareTo(avg.multiply(AMOUNT_DEVIATION_THRESHOLD)) > 0;

        boolean highFrequency = history.size() > FREQUENCY_THRESHOLD;

        return amountOutlier || highFrequency;
    }
}
