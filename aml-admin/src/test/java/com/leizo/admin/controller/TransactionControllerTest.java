package com.leizo.admin.controller;

import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.TransactionRepository;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.service.RuleEngine;
import com.leizo.service.RiskScoringService;
import com.leizo.service.SanctionsChecker;
import com.leizo.enums.RiskScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private RiskScoringService riskScoringService;

    @Mock
    private SanctionsChecker sanctionsChecker;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private TransactionController transactionController;

    private MockMultipartFile validCsvFile;
    private MockMultipartFile invalidCsvFile;
    private MockMultipartFile emptyFile;
    private MockMultipartFile largeFile;
    private MockMultipartFile unsupportedFile;

    @BeforeEach
    void setUp() {
        // Create valid CSV file with exact controller expected structure
        String validCsvContent = "transactionId,timestamp,amount,currency,senderName,receiverName,senderAccount,receiverAccount,country,manualFlag,description\n" +
                "TXN-001,2025-07-25T14:22:30Z,1000.00,USD,John Doe,Jane Smith,ACC-001,ACC-002,US,true,Salary payment\n" +
                "TXN-002,2025-07-25T15:10:00Z,2500.50,EUR,Maria Garcia,Carlos Rodriguez,ACC-003,ACC-004,ES,false,Invoice settlement";

        validCsvFile = new MockMultipartFile(
            "file",
            "transactions.csv",
            "text/csv",
            validCsvContent.getBytes()
        );

        // Create invalid CSV file with missing columns (exactly as controller expects)
        String invalidCsvContent = "transactionId,timestamp,amount,currency,senderName\n" +
                "TXN-001,2025-07-25T14:22:30Z,1000.00,USD,John Doe";

        invalidCsvFile = new MockMultipartFile(
            "file",
            "invalid.csv",
            "text/csv",
            invalidCsvContent.getBytes()
        );

        // Create empty file
        emptyFile = new MockMultipartFile(
            "file",
            "empty.csv",
            "text/csv",
            new byte[0]
        );

        // Create large file (over 10MB)
        largeFile = new MockMultipartFile(
            "file",
            "large.csv",
            "text/csv",
            new byte[11 * 1024 * 1024] // 11MB
        );

        // Create unsupported file type
        unsupportedFile = new MockMultipartFile(
            "file",
            "transactions.txt",
            "text/plain",
            "some content".getBytes()
        );
    }

    @Test
    void testIngestValidCsvFile() {
        // Mock dependencies for successful processing
        Transaction savedTransaction1 = new Transaction("John Doe", "Jane Smith", new BigDecimal("1000"), "USD", "US", "2025-01-01");
        savedTransaction1.setId(1);
        Transaction savedTransaction2 = new Transaction("Maria Garcia", "Carlos Rodriguez", new BigDecimal("2500.50"), "EUR", "ES", "2025-01-01");
        savedTransaction2.setId(2);
        
        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(savedTransaction1)
            .thenReturn(savedTransaction2);
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.LOW);
        when(ruleEngine.getActiveRules()).thenReturn(new ArrayList<>());
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);

        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(validCsvFile);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.get("processed"));
        assertEquals(2, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));

        // Verify repository was called exactly twice
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void testIngestInvalidCsvFile() {
        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(invalidCsvFile);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.get("processed")); // Controller returns 0 for invalid headers
        assertEquals(0, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));

        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) responseBody.get("errors");
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("Invalid CSV format")));
    }

    @Test
    void testIngestEmptyFile() {
        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(emptyFile);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.get("processed"));
        assertEquals(0, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));

        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) responseBody.get("errors");
        assertNotNull(errors);
        assertTrue(errors.contains("Empty file"));
    }

    @Test
    void testIngestNullFile() {
        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(null);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.get("processed"));
        assertEquals(0, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));

        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) responseBody.get("errors");
        assertNotNull(errors);
        assertTrue(errors.contains("No file uploaded"));
    }

    @Test
    void testIngestLargeFile() {
        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(largeFile);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.get("processed"));
        assertEquals(0, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));

        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) responseBody.get("errors");
        assertNotNull(errors);
        assertTrue(errors.contains("File size exceeds 10MB limit"));
    }

    @Test
    void testIngestUnsupportedFileType() {
        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(unsupportedFile);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.get("processed"));
        assertEquals(0, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));

        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) responseBody.get("errors");
        assertNotNull(errors);
        assertTrue(errors.stream().anyMatch(error -> error.contains("Unsupported file type")));
    }

    @Test
    void testGetTransactions() {
        // Mock repository with exact controller expected data
        List<Transaction> transactions = Arrays.asList(
            new Transaction("John", "Jane", new BigDecimal("1000"), "USD", "US", "2025-01-01"),
            new Transaction("Bob", "Alice", new BigDecimal("2000"), "EUR", "UK", "2025-01-02")
        );
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Execute
        ResponseEntity<?> response = transactionController.getTransactions(0, 10, null, null, null, null);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.get("totalElements"));
        assertEquals(1, responseBody.get("totalPages"));
        assertEquals(0, responseBody.get("currentPage"));
        assertEquals(10, responseBody.get("size"));
        assertTrue((Boolean) responseBody.get("first"));
        assertTrue((Boolean) responseBody.get("last"));

        @SuppressWarnings("unchecked")
        List<Transaction> content = (List<Transaction>) responseBody.get("content");
        assertNotNull(content);
        assertEquals(2, content.size());
    }

    @Test
    void testGetTransactionsWithFilters() {
        // Mock repository
        List<Transaction> transactions = Arrays.asList(
            new Transaction("John", "Jane", new BigDecimal("1000"), "USD", "US", "2025-01-01"),
            new Transaction("Bob", "Alice", new BigDecimal("2000"), "EUR", "UK", "2025-01-02")
        );
        transactions.get(0).setRiskScore(RiskScore.LOW);
        transactions.get(1).setRiskScore(RiskScore.HIGH);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Execute with status filter
        ResponseEntity<?> response = transactionController.getTransactions(0, 10, "LOW", null, null, null);

        // Verify filtered response
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.get("totalElements")); // Only LOW risk transactions
    }

    @Test
    void testGetTransactionById() {
        // Mock repository
        Transaction transaction = new Transaction("John", "Jane", new BigDecimal("1000"), "USD", "US", "2025-01-01");
        transaction.setId(1);
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        // Execute
        ResponseEntity<?> response = transactionController.getTransactionById(1);

        // Verify exact controller response format
        assertEquals(200, response.getStatusCodeValue());
        Transaction responseTransaction = (Transaction) response.getBody();
        assertNotNull(responseTransaction);
        assertEquals("John", responseTransaction.getSender());
        assertEquals("Jane", responseTransaction.getReceiver());
    }

    @Test
    void testGetTransactionByIdNotFound() {
        // Mock repository
        when(transactionRepository.findById(999)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<?> response = transactionController.getTransactionById(999);

        // Verify exact controller response format (404 Not Found)
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetTransactionByIdWithException() {
        // Mock repository to throw exception
        when(transactionRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // Execute
        ResponseEntity<?> response = transactionController.getTransactionById(1);

        // Verify exact controller response format (200 OK with error status)
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("ERROR", responseBody.get("status"));
        assertNotNull(responseBody.get("message"));
        assertNull(responseBody.get("transaction"));
    }

    @Test
    void testGetTransactionsWithException() {
        // Mock repository to throw exception
        when(transactionRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Execute
        ResponseEntity<?> response = transactionController.getTransactions(0, 10, null, null, null, null);

        // Verify exact controller response format (200 OK with error status)
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("ERROR", responseBody.get("status"));
        assertNotNull(responseBody.get("message"));
        assertEquals(0, responseBody.get("totalElements"));
        assertEquals(0, responseBody.get("totalPages"));
    }
} 