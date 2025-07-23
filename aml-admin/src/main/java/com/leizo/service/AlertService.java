package com.leizo.service;

import com.leizo.admin.entity.Alert;
import com.leizo.admin.entity.Transaction;
import com.leizo.admin.entity.Rule;

public interface AlertService {

    void registerCooldown(String sender, String ruleId);

    Alert generateAlert(com.leizo.admin.entity.Transaction txn, com.leizo.admin.entity.Rule rule, String reason);
    boolean isDuplicateAlert(Transaction txn, String reason);
    String generateFingerPrint(Transaction txn, String reason);
    boolean isInCooldown(String sender, String ruleId);

}
