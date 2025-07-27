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
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leizo.admin.dto.TransactionDTO;
import com.leizo.admin.dto.TransactionCsvParser;
import com.leizo.admin.dto.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/ingest")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
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
                    // Validate transaction data before processing
                    if (txn.getSender() == null || txn.getSender().trim().isEmpty()) {
                        throw new IllegalArgumentException("Sender name cannot be null or empty");
                    }
                    if (txn.getAmount() == null || txn.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Amount must be greater than zero");
                    }
                    
                    // Risk scoring with error handling
                    try {
                        txn.setRiskScore(riskScoringService.assessRisk(txn));
                    } catch (Exception e) {
                        logger.warn("Risk scoring failed for transaction {}: {}", txn.getId(), e.getMessage());
                        txn.setRiskScore(com.leizo.enums.RiskScore.MEDIUM); // Default fallback
                    }
                    
                    // Rule engine (apply all active rules) with error handling
                    try {
                        for (var rule : ruleEngine.getActiveRules()) {
                            ruleEngine.applyRule(txn, rule, txn.getAmount());
                        }
                    } catch (Exception e) {
                        logger.warn("Rule engine failed for transaction {}: {}", txn.getId(), e.getMessage());
                        // Continue processing even if rule engine fails
                    }
                    
                    // Sanctions check with enhanced matching and error handling
                    boolean isSanctioned = false;
                    try {
                        isSanctioned = sanctionsChecker.isSanctionedEntity(
                            txn.getSender(), txn.getCountry(), txn.getDob(), null
                        );
                    } catch (Exception e) {
                        logger.warn("Sanctions check failed for transaction {}: {}", txn.getId(), e.getMessage());
                        // Continue processing even if sanctions check fails
                    }
                    
                    if (isSanctioned) {
                        try {
                            Alert alert = createSanctionsAlert(txn);
                            alertRepository.save(alert);
                            alertsGenerated++;
                        } catch (Exception e) {
                            logger.error("Failed to create sanctions alert for transaction {}: {}", txn.getId(), e.getMessage());
                            // Continue processing even if alert creation fails
                        }
                    }
                    
                    // Save transaction with validation
                    try {
                        // Ensure required fields are set
                        if (txn.getCurrency() == null || txn.getCurrency().trim().isEmpty()) {
                            txn.setCurrency("USD"); // Default currency
                        }
                        
                        transactionRepository.save(txn);
                        successful++;
                    } catch (DataAccessException e) {
                        logger.error("Database error saving transaction {}: {}", txn.getId(), e.getMessage());
                        throw new RuntimeException("Database error: " + e.getMessage());
                    }
                    
                } catch (Exception e) {
                    failed++;
                    String errorMsg = "Transaction " + (txn.getId() != null ? txn.getId() : "unknown") + " failed: " + e.getMessage();
                    errors.add(errorMsg);
                    logger.error(errorMsg, e);
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

    // GET endpoints for transaction history
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        try {
            List<Transaction> allTransactions = transactionRepository.findAll();
            
            // Apply filters
            List<Transaction> filteredTransactions = allTransactions;
            
            if (status != null && !status.isEmpty()) {
                filteredTransactions = filteredTransactions.stream()
                    .filter(t -> status.equalsIgnoreCase(t.getRiskScore().toString()))
                    .collect(Collectors.toList());
            }
            
            if (transactionType != null && !transactionType.isEmpty()) {
                filteredTransactions = filteredTransactions.stream()
                    .filter(t -> transactionType.equalsIgnoreCase(t.getCurrency()))
                    .collect(Collectors.toList());
            }
            
            // Note: Date filtering removed as Transaction entity doesn't have timestamp field
            
            // Apply pagination
            int totalElements = filteredTransactions.size();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalElements);
            
            List<Transaction> pagedTransactions = filteredTransactions.subList(startIndex, endIndex);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", pagedTransactions);
            response.put("totalElements", totalElements);
            response.put("totalPages", totalPages);
            response.put("currentPage", page);
            response.put("size", size);
            response.put("first", page == 0);
            response.put("last", page >= totalPages - 1);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Integer id) {
        try {
            Optional<Transaction> transaction = transactionRepository.findById(id);
            if (transaction.isPresent()) {
                return ResponseEntity.ok(transaction.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transaction: " + e.getMessage()));
        }
    }
} 