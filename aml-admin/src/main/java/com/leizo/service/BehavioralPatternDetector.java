package com.leizo.service;

import com.leizo.admin.entity.Transaction;
import java.util.List;

public interface BehavioralPatternDetector {

    /**
     * Detects if a given transaction deviates significantly from the sender's history.
     * @param txn the new transaction to evaluate
     * @param history sender's past transactions
     * @return true if abnormal, false if consistent
     */
    boolean detectDeviations(Transaction txn, List<Transaction> history);
}
