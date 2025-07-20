package com.leizo.service;

import com.leizo.model.Alert;
import com.leizo.model.Transaction;
import com.leizo.model.Rule;

public interface AlertService {

    void registerCooldown(String sender, String ruleId);

    Alert generateAlert(Transaction txn, Rule rule, String reason);
    boolean isDuplicateAlert(Transaction txn, String reason);
    String generateFingerPrint(Transaction txn, String reason);
    boolean isInCooldown(String sender, String ruleId);

}
