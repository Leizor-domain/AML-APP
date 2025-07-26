package com.leizo.admin.controller;

import com.leizo.admin.entity.Transaction;
import com.leizo.admin.entity.Alert;
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
    private MockMultipartFile jsonFile;

    @BeforeEach
    void setUp() {
        // Create valid CSV file with new DTO structure
        String validCsvContent = "transactionId,timestamp,amount,currency,senderName,receiverName,senderAccount,receiverAccount,country,manualFlag,description\n" +
                "TXN-001,2025-07-25T14:22:30Z,1000.00,USD,John Doe,Jane Smith,ACC-001,ACC-002,US,true,Salary payment\n" +
                "TXN-002,2025-07-25T15:10:00Z,2500.50,EUR,Maria Garcia,Carlos Rodriguez,ACC-003,ACC-004,ES,false,Invoice settlement";

        validCsvFile = new MockMultipartFile(
            "file", 
            "transactions.csv", 
            "text/csv", 
            validCsvContent.getBytes()
        );

        // Create invalid CSV file with missing columns
        String invalidCsvContent = "transactionId,timestamp,amount,currency,senderName\n" +
                "TXN-001,2025-07-25T14:22:30Z,1000.00,USD,John Doe";

        invalidCsvFile = new MockMultipartFile(
            "file", 
            "invalid.csv", 
            "text/csv", 
            invalidCsvContent.getBytes()
        );

        // Create JSON file (will be rejected with "not implemented" message)
        String jsonContent = "[{\"sender\":\"John Doe\",\"receiver\":\"Jane Smith\",\"amount\":1000.00,\"currency\":\"USD\",\"country\":\"USA\"}]";
        jsonFile = new MockMultipartFile(
            "file", 
            "transactions.json", 
            "application/json", 
            jsonContent.getBytes()
        );
    }

    @Test
    void testIngestValidCsvFile() {
        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.LOW);
        when(ruleEngine.getActiveRules()).thenReturn(new ArrayList<>());
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(validCsvFile);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2, responseBody.get("processed"));
        assertEquals(2, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));
        assertEquals(0, responseBody.get("alertsGenerated"));

        // Verify transactions were processed
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(riskScoringService, times(2)).assessRisk(any(Transaction.class));
    }

    @Test
    void testIngestInvalidCsvFile() {
        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(invalidCsvFile);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue(responseBody.containsKey("errors"));
        List<String> errors = (List<String>) responseBody.get("errors");
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("CSV header is missing required columns")));
    }

    @Test
    void testIngestJsonFile() {
        // Execute - JSON ingestion is not implemented in new DTO structure
        ResponseEntity<?> response = transactionController.ingestFile(jsonFile);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue(responseBody.containsKey("errors"));
        List<String> errors = (List<String>) responseBody.get("errors");
        assertTrue(errors.stream().anyMatch(error -> error.contains("JSON ingestion not yet implemented")));
    }

    @Test
    void testIngestFileWithSanctionsMatch() {
        // Mock sanctions match
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.HIGH);
        when(ruleEngine.getActiveRules()).thenReturn(new ArrayList<>());
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        when(alertRepository.save(any(Alert.class))).thenReturn(new Alert());

        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(validCsvFile);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2, responseBody.get("alertsGenerated"));

        // Verify alerts were created
        verify(alertRepository, times(2)).save(any(Alert.class));
    }

    @Test
    void testIngestEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", 
            "empty.csv", 
            "text/csv", 
            "".getBytes()
        );

        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(emptyFile);

        // Verify
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Failed to parse file: CSV file is empty", responseBody.get("error"));
    }

    @Test
    void testIngestUnsupportedFileType() {
        MockMultipartFile unsupportedFile = new MockMultipartFile(
            "file", 
            "transactions.txt", 
            "text/plain", 
            "some content".getBytes()
        );

        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(unsupportedFile);

        // Verify
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Unsupported file type. Only CSV is allowed.", responseBody.get("error"));
    }

    @Test
    void testIngestNullFile() {
        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(null);

        // Verify
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("No file uploaded", responseBody.get("error"));
    }

    @Test
    void testCsvRowValidation() {
        // Create CSV with invalid data matching new DTO structure
        String invalidDataCsv = "transactionId,timestamp,amount,currency,senderName,receiverName,senderAccount,receiverAccount,country,manualFlag,description\n" +
                "TXN-001,2025-07-25T14:22:30Z,invalid_amount,USD,John Doe,Jane Smith,ACC-001,ACC-002,US,true,Test\n" +
                "TXN-002,2025-07-25T15:10:00Z,2500.50,INVALID_CURRENCY,Maria Garcia,Carlos Rodriguez,ACC-003,ACC-004,ES,false,Test\n" +
                "TXN-003,2025-07-25T16:00:00Z,3000.00,USD,,Jane Smith,ACC-005,ACC-006,US,true,Test\n" +
                "TXN-004,2025-07-25T17:00:00Z,4000.00,USD,John Doe,Jane Smith,ACC-007,ACC-008,US,invalid_boolean,Test\n" +
                "TXN-005,invalid_timestamp,5000.00,USD,John Doe,Jane Smith,ACC-009,ACC-010,US,true,Test";

        MockMultipartFile invalidDataFile = new MockMultipartFile(
            "file", 
            "invalid_data.csv", 
            "text/csv", 
            invalidDataCsv.getBytes()
        );

        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(invalidDataFile);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue(responseBody.containsKey("errors"));
        List<String> errors = (List<String>) responseBody.get("errors");
        
        // Check for specific validation errors in new DTO structure
        assertTrue(errors.stream().anyMatch(error -> error.contains("amount must be a valid decimal")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("currency must be 3-letter ISO 4217 code")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("senderName cannot be empty")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("manualFlag must be 'true' or 'false'")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("timestamp must be in ISO 8601 format")));
    }

    @Test
    void testBatchProcessingEfficiency() {
        // Create large CSV file with new DTO structure
        StringBuilder largeCsv = new StringBuilder();
        largeCsv.append("transactionId,timestamp,amount,currency,senderName,receiverName,senderAccount,receiverAccount,country,manualFlag,description\n");
        
        for (int i = 1; i <= 250; i++) {
            largeCsv.append(String.format("TXN-%03d,2025-07-25T14:22:30Z,%d.00,USD,Sender%d,Receiver%d,ACC-S%d,ACC-R%d,US,false,Batch test %d\n", 
                i, i * 100, i, i, i, i, i));
        }

        MockMultipartFile largeFile = new MockMultipartFile(
            "file", 
            "large_transactions.csv", 
            "text/csv", 
            largeCsv.toString().getBytes()
        );

        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.LOW);
        when(ruleEngine.getActiveRules()).thenReturn(new ArrayList<>());
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Execute
        ResponseEntity<?> response = transactionController.ingestFile(largeFile);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(250, responseBody.get("processed"));
        assertEquals(250, responseBody.get("successful"));
        assertEquals(0, responseBody.get("failed"));

        // Verify all transactions were processed
        verify(transactionRepository, times(250)).save(any(Transaction.class));
        verify(riskScoringService, times(250)).assessRisk(any(Transaction.class));
    }
} 