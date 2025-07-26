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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leizo.admin.dto.TransactionDTO;
import com.leizo.admin.dto.TransactionCsvParser;
import com.leizo.admin.dto.TransactionMapper;

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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final int BATCH_SIZE = 100; // Process transactions in batches

    @PostMapping("/file")
    public ResponseEntity<?> ingestFile(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file uploaded"));
        }
        if (file.isEmpty() || file.getSize() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to parse file: CSV file is empty"));
        }
        String filename = file.getOriginalFilename();
        boolean isCsv = filename != null && filename.toLowerCase().endsWith(".csv");
        boolean isJson = filename != null && filename.toLowerCase().endsWith(".json");
        List<TransactionDTO> dtos = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int processed = 0, successful = 0, failed = 0, alertsGenerated = 0;
        try {
            if (isCsv) {
                dtos = TransactionCsvParser.parse(file.getInputStream(), errors);
                processed = dtos.size() + errors.size();
            } else if (isJson) {
                // TODO: Implement JSON to DTO parsing if needed
                errors.add("JSON ingestion not yet implemented for new DTO structure");
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Unsupported file type. Only CSV is allowed."));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to parse file: " + e.getMessage()));
        }
        // Map DTOs to entities
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionDTO dto : dtos) {
            try {
                transactions.add(TransactionMapper.toEntity(dto));
            } catch (Exception e) {
                errors.add("DTO mapping failed: " + e.getMessage());
            }
        }
        // Process transactions in batches for efficiency
        Map<String, Object> processingResult = processTransactionsBatch(transactions);
        successful = (Integer) processingResult.get("successful");
        failed = (Integer) processingResult.get("failed");
        alertsGenerated = (Integer) processingResult.get("alertsGenerated");
        errors.addAll((List<String>) processingResult.get("errors"));
        Map<String, Object> response = new HashMap<>();
        response.put("processed", processed);
        response.put("successful", successful);
        response.put("failed", failed);
        response.put("alertsGenerated", alertsGenerated);
        if (!errors.isEmpty()) response.put("errors", errors);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> processTransactionsBatch(List<Transaction> transactions) {
        int successful = 0, failed = 0, alertsGenerated = 0;
        List<String> errors = new ArrayList<>();
        
        // Process in batches for efficiency
        for (int i = 0; i < transactions.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, transactions.size());
            List<Transaction> batch = transactions.subList(i, endIndex);
            
            for (Transaction txn : batch) {
                try {
                    // Risk scoring
                    txn.setRiskScore(riskScoringService.assessRisk(txn));
                    
                    // Rule engine (apply all active rules)
                    for (var rule : ruleEngine.getActiveRules()) {
                        ruleEngine.applyRule(txn, rule, txn.getAmount());
                    }
                    
                    // Sanctions check with enhanced matching
                    boolean isSanctioned = sanctionsChecker.isSanctionedEntity(
                        txn.getSender(), txn.getCountry(), txn.getDob(), null
                    );
                    
                    if (isSanctioned) {
                        Alert alert = createSanctionsAlert(txn);
                        alertRepository.save(alert);
                        alertsGenerated++;
                    }
                    
                    // Save transaction
                    transactionRepository.save(txn);
                    successful++;
                    
                } catch (Exception e) {
                    failed++;
                    errors.add("Transaction " + txn.getId() + " failed: " + e.getMessage());
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successful", successful);
        result.put("failed", failed);
        result.put("alertsGenerated", alertsGenerated);
        result.put("errors", errors);
        return result;
    }

    private Alert createSanctionsAlert(Transaction txn) {
        Alert alert = new Alert();
        alert.setMatchedEntityName(txn.getSender());
        alert.setMatchedList("Sanctions");
        alert.setMatchReason("Sender matched against sanctions list");
        alert.setTransactionId(txn.getId());
        alert.setReason("Sender is sanctioned");
        alert.setTimestamp(LocalDateTime.now());
        alert.setAlertType("SANCTIONS");
        alert.setPriorityLevel("HIGH");
        return alert;
    }
} 