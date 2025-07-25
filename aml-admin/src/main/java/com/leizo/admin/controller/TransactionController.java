package com.leizo.admin.controller;

import com.leizo.admin.entity.Transaction;
import com.leizo.admin.repository.TransactionRepository;
import com.leizo.service.RuleEngine;
import com.leizo.service.RiskScoringService;
import com.leizo.service.SanctionsChecker;
import com.leizo.admin.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/ingest")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private RuleEngine ruleEngine;
    @Autowired
    private RiskScoringService riskScoringService;
    @Autowired
    private SanctionsChecker sanctionsChecker;
    @Autowired
    private AlertRepository alertRepository;

    @PostMapping("/file")
    public ResponseEntity<?> ingestFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file uploaded"));
        }
        String filename = file.getOriginalFilename();
        boolean isCsv = filename != null && filename.toLowerCase().endsWith(".csv");
        boolean isJson = filename != null && filename.toLowerCase().endsWith(".json");
        List<Transaction> transactions = new ArrayList<>();
        int processed = 0, successful = 0, failed = 0, alertsGenerated = 0;
        List<String> errors = new ArrayList<>();
        try {
            if (isCsv) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                    String header = reader.readLine();
                    if (header == null) throw new IOException("CSV file is empty");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        processed++;
                        try {
                            String[] parts = line.split(",");
                            if (parts.length < 6) throw new IllegalArgumentException("Invalid CSV row");
                            Transaction txn = new Transaction();
                            txn.setSender(parts[0].trim());
                            txn.setReceiver(parts[1].trim());
                            txn.setAmount(new BigDecimal(parts[2].trim()));
                            txn.setCurrency(parts[3].trim());
                            txn.setCountry(parts[4].trim());
                            txn.setDob(parts[5].trim());
                            transactions.add(txn);
                        } catch (Exception e) {
                            failed++;
                            errors.add("Row " + processed + ": " + e.getMessage());
                        }
                    }
                }
            } else if (isJson) {
                ObjectMapper mapper = new ObjectMapper();
                Transaction[] txns = mapper.readValue(file.getInputStream(), Transaction[].class);
                transactions.addAll(Arrays.asList(txns));
                processed = transactions.size();
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Unsupported file type. Only CSV and JSON are allowed."));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to parse file: " + e.getMessage()));
        }
        // Evaluate and save transactions
        for (Transaction txn : transactions) {
            try {
                // Risk scoring
                txn.setRiskScore(riskScoringService.assessRisk(txn));
                // Rule engine (apply all active rules)
                for (var rule : ruleEngine.getActiveRules()) {
                    ruleEngine.applyRule(txn, rule, txn.getAmount());
                }
                // Sanctions check
                if (sanctionsChecker.isSanctionedEntity(txn.getSender(), txn.getCountry(), txn.getDob(), null)) {
                    Alert alert = new Alert();
                    alert.setMatchedEntityName(txn.getSender());
                    alert.setMatchedList("Sanctions");
                    alert.setMatchReason("Sender is sanctioned");
                    alert.setTransactionId(txn.getId());
                    alert.setReason("Sender is sanctioned");
                    alert.setTimestamp(java.time.LocalDateTime.now());
                    alert.setAlertType("SANCTIONS");
                    alert.setPriorityLevel("HIGH");
                    alertRepository.save(alert);
                    alertsGenerated++;
                }
                transactionRepository.save(txn);
                successful++;
            } catch (RuntimeException e) {
                failed++;
                errors.add("Transaction failed: " + e.getMessage());
            }
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("processed", processed);
        resp.put("successful", successful);
        resp.put("failed", failed);
        resp.put("alertsGenerated", alertsGenerated);
        if (!errors.isEmpty()) resp.put("errors", errors);
        return ResponseEntity.ok(resp);
    }
} 