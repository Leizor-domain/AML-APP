package com.leizo.portal.controller;

import com.leizo.enums.IngestionStatus;
import com.leizo.model.IngestionResult;
import com.leizo.pojo.entity.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController {

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestTransaction(@RequestBody Map<String, Object> transactionData) {
        try {
            // Create a mock ingestion result for now
            // In a real implementation, this would call the AML engine
            IngestionResult result = new IngestionResult(
                IngestionStatus.SUCCESS,
                false, // No alert triggered
                null,  // No alert ID
                25     // Mock risk score
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Transaction ingested successfully");
            response.put("riskScore", 25);
            response.put("alertTriggered", false);
            response.put("transactionId", "TXN-" + System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Transaction ingestion failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        try {
            // Mock transaction data
            Map<String, Object> mockTransaction = new HashMap<>();
            mockTransaction.put("id", 1);
            mockTransaction.put("sender", "John Doe");
            mockTransaction.put("receiver", "Jane Smith");
            mockTransaction.put("amount", 2500.00);
            mockTransaction.put("currency", "USD");
            mockTransaction.put("country", "USA");
            mockTransaction.put("status", "COMPLETED");
            mockTransaction.put("riskScore", 25);
            mockTransaction.put("createdAt", "2024-01-15T10:30:00Z");

            Map<String, Object> response = new HashMap<>();
            response.put("content", new Object[]{mockTransaction});
            response.put("totalElements", 1);
            response.put("totalPages", 1);
            response.put("currentPage", page);
            response.put("size", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to retrieve transactions: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Integer id) {
        try {
            // Mock transaction data
            Map<String, Object> mockTransaction = new HashMap<>();
            mockTransaction.put("id", id);
            mockTransaction.put("sender", "John Doe");
            mockTransaction.put("receiver", "Jane Smith");
            mockTransaction.put("amount", 2500.00);
            mockTransaction.put("currency", "USD");
            mockTransaction.put("country", "USA");
            mockTransaction.put("status", "COMPLETED");
            mockTransaction.put("riskScore", 25);
            mockTransaction.put("createdAt", "2024-01-15T10:30:00Z");

            return ResponseEntity.ok(mockTransaction);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to retrieve transaction: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 