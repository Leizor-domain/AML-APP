package com.leizo.service;

import com.leizo.pojo.entity.Alert;
import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Rule;

public interface AlertService {

    void registerCooldown(String sender, String ruleId);

    Alert generateAlert(com.leizo.pojo.entity.Transaction txn, com.leizo.pojo.entity.Rule rule, String reason);
    boolean isDuplicateAlert(Transaction txn, String reason);
    String generateFingerPrint(Transaction txn, String reason);
    boolean isInCooldown(String sender, String ruleId);

}
