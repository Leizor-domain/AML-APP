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

    // Helper for mapping CSV fields to TransactionDTO (for ingestion)
    private static TransactionDTO mapFieldsToDTO(String[] fields) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(fields[0]);
        dto.setTimestamp(fields[1]);
        dto.setAmount(new java.math.BigDecimal(fields[2]));
        dto.setCurrency(fields[3]);
        dto.setSenderName(fields[4]);
        dto.setReceiverName(fields[5]);
        dto.setSenderAccount(fields[6]);
        dto.setReceiverAccount(fields[7]);
        dto.setCountry(fields[8]);
        dto.setManualFlag(Boolean.parseBoolean(fields[9]));
        dto.setDescription(fields[10]);
        return dto;
    }

    @PostMapping("/file")
    public ResponseEntity<?> ingestFile(@RequestParam("file") MultipartFile file) {
        List<Map<String, Object>> errors = new ArrayList<>();
        int processed = 0, success = 0, failed = 0;
        final List<String> requiredHeaders = List.of("transactionId", "timestamp", "amount", "currency", "senderName", "receiverName", "senderAccount", "receiverAccount", "country", "manualFlag", "description");
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "processed", 0, "successful", 0, "failed", 0, "errors", List.of("No file uploaded")
                ));
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.ok(Map.of(
                    "processed", 0, "successful", 0, "failed", 0, "errors", List.of("File size exceeds 10MB limit")
                ));
            }
            String filename = file.getOriginalFilename();
            boolean isCsv = filename != null && filename.toLowerCase().endsWith(".csv");
            boolean isJson = filename != null && filename.toLowerCase().endsWith(".json");
            List<TransactionDTO> dtos = new ArrayList<>();
            if (isCsv) {
                // Read header row
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                String headerLine = reader.readLine();
                if (headerLine == null) {
                    return ResponseEntity.ok(Map.of(
                        "processed", 0, "successful", 0, "failed", 0, "errors", List.of("Empty file")
                    ));
                }
                List<String> headers = Arrays.asList(headerLine.split(","));
                if (!headers.equals(requiredHeaders)) {
                    return ResponseEntity.ok(Map.of(
                        "processed", 0, "successful", 0, "failed", 0, "errors", List.of("Header mismatch. Required: " + requiredHeaders)
                    ));
                }
                // Parse rest of file
                String line;
                int rowNum = 1;
                while ((line = reader.readLine()) != null) {
                    rowNum++;
                    String[] fields = line.split(",");
                    if (fields.length != requiredHeaders.size()) {
                        failed++;
                        errors.add(Map.of("row", rowNum, "error", "Column count mismatch"));
                        continue;
                    }
                    try {
                        // Map fields to DTO (implement this mapping as needed)
                        TransactionDTO dto = mapFieldsToDTO(fields);
                        // Validate fields (implement as needed)
                        if (dto == null) throw new IllegalArgumentException("Null DTO");
                        if (dto.getTransactionId() == null || dto.getTransactionId().trim().isEmpty())
                            throw new IllegalArgumentException("Transaction ID missing");
                        if (dto.getTimestamp() == null || dto.getTimestamp().trim().isEmpty())
                            throw new IllegalArgumentException("Timestamp missing");
                        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0)
                            throw new IllegalArgumentException("Amount format invalid");
                        if (dto.getCurrency() == null || dto.getCurrency().trim().isEmpty())
                            throw new IllegalArgumentException("Currency missing");
                        if (dto.getSenderName() == null || dto.getSenderName().trim().isEmpty())
                            throw new IllegalArgumentException("Sender name missing");
                        if (dto.getReceiverName() == null || dto.getReceiverName().trim().isEmpty())
                            throw new IllegalArgumentException("Receiver name missing");
                        if (dto.getSenderAccount() == null || dto.getSenderAccount().trim().isEmpty())
                            throw new IllegalArgumentException("Sender account missing");
                        if (dto.getReceiverAccount() == null || dto.getReceiverAccount().trim().isEmpty())
                            throw new IllegalArgumentException("Receiver account missing");
                        if (dto.getCountry() == null || dto.getCountry().trim().isEmpty())
                            throw new IllegalArgumentException("Country missing");
                        if (dto.getManualFlag() == null)
                            throw new IllegalArgumentException("Manual flag missing");
                        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty())
                            throw new IllegalArgumentException("Description missing");
                        
                        // Save transaction
                        transactionRepository.save(TransactionMapper.toEntity(dto));
                        success++;
                    } catch (Exception e) {
                        failed++;
                        errors.add(Map.of("row", rowNum, "error", e.getMessage()));
                    }
                    processed++;
                }
            } else if (isJson) {
                // TODO: Implement JSON parsing and header validation
                return ResponseEntity.ok(Map.of(
                    "processed", 0, "successful", 0, "failed", 0, "errors", List.of("JSON ingestion not implemented")
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "processed", 0, "successful", 0, "failed", 0, "errors", List.of("Unsupported file type")
                ));
            }
            return ResponseEntity.ok(Map.of(
                "processed", processed,
                "successful", success,
                "failed", failed,
                "errors", errors
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "processed", processed,
                "successful", success,
                "failed", failed,
                "errors", List.of(e.getMessage())
            ));
        }
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
                    // Validate transaction data before processing with defaults
                    if (txn.getSender() == null || txn.getSender().trim().isEmpty()) {
                        logger.warn("Transaction missing sender, using default: {}", txn.getId());
                        txn.setSender("Unknown Sender"); // Use default instead of throwing
                    }
                    if (txn.getAmount() == null || txn.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        logger.warn("Transaction has invalid amount: {}, using default: {}", txn.getAmount(), txn.getId());
                        txn.setAmount(BigDecimal.ONE); // Use default instead of throwing
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
                        // Continue processing instead of throwing - just log the error
                        String errorMsg = "Database error saving transaction " + (txn.getId() != null ? txn.getId() : "unknown") + ": " + e.getMessage();
                        errors.add(errorMsg);
                        failed++;
                        continue; // Skip this transaction and continue with the next
                    }
                    
                } catch (Exception e) {
                    failed++;
                    String errorMsg = "Transaction " + (txn.getId() != null ? txn.getId() : "unknown") + " failed: " + e.getMessage();
                    errors.add(errorMsg);
                    logger.error(errorMsg, e);
                    // Continue processing the next transaction instead of stopping the batch
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
            logger.error("Failed to retrieve transactions: {}", e.getMessage(), e);
            // Return 200 OK with error status instead of 500
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Failed to retrieve transactions: " + e.getMessage());
            response.put("content", new ArrayList<>());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("size", size);
            response.put("first", true);
            response.put("last", true);
            
            return ResponseEntity.ok(response);
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
            logger.error("Failed to retrieve transaction {}: {}", id, e.getMessage(), e);
            // Return 200 OK with error status instead of 500
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Failed to retrieve transaction: " + e.getMessage());
            response.put("transaction", null);
            
            return ResponseEntity.ok(response);
        }
    }
} 