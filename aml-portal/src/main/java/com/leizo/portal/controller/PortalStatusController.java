package com.leizo.portal.controller;

import com.leizo.enums.RiskScore;
import com.leizo.admin.entity.Transaction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PortalStatusController {

    //Check portal status on dedicated port
    @GetMapping("/portal/status")
    public String portalStatus() {
        return " Portal Module is working!";
    }

    //Check a transaction sample on the dedicated port (to determine if the server responds as required)
    @GetMapping("/transaction/sample")
    //Create a mock transaction ny calling the transaction class and entering mock data
    public Transaction sampleTransaction() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("source", "portal-test");
        metadata.put("flag", "manual");

        //Transaction data
        Transaction txn = new Transaction();
        txn.setSender("John Doe");
        txn.setReceiver("Jane Smith");
        txn.setAmount(new BigDecimal("2500.00"));
        txn.setCurrency("USD");
        txn.setCountry("USA");
        txn.setDob("1990-04-12");
        txn.setMetadata(metadata);
        txn.setRiskScore(RiskScore.MEDIUM);

        return txn;
    }

    @GetMapping("/public/db-health")
    public Map<String, Object> dbHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Portal database health check (no DB logic implemented)");
        return response;
    }
} 