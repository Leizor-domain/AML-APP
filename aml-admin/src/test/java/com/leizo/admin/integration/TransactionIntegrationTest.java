package com.leizo.admin.integration;

import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.TransactionRepository;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.service.RuleEngine;
import com.leizo.service.RiskScoringService;
import com.leizo.service.SanctionsChecker;
import com.leizo.service.TransactionEvaluatorService;
import com.leizo.enums.RiskScore;
import com.leizo.enums.RuleSensitivity;
import com.leizo.pojo.entity.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AlertRepository alertRepository;

    @MockBean
    private RiskScoringService riskScoringService;

    @MockBean
    private SanctionsChecker sanctionsChecker;

    @MockBean
    private RuleEngine ruleEngine;

    @Autowired
    private TransactionEvaluatorService transactionEvaluatorService;

    private Transaction highRiskTransaction;
    private Transaction normalTransaction;
    private Rule highAmountRule;

    @BeforeEach
    void setUp() {
        // Clear test data
        alertRepository.deleteAll();
        transactionRepository.deleteAll();

        // Create test transactions
        highRiskTransaction = new Transaction("Ali Mohammed", "Unknown", new BigDecimal("50000"), "USD", "Iran", "2025-01-01");
        normalTransaction = new Transaction("John Doe", "Jane Smith", new BigDecimal("1000"), "USD", "USA", "2025-01-01");

        // Create test rule
        highAmountRule = new Rule("High Amount Rule", RuleSensitivity.HIGH,
            (txn, amount) -> amount.compareTo(new BigDecimal("10000")) > 0,
            Set.of("amount", "high"));

        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class)))
            .thenAnswer(invocation -> {
                Transaction txn = invocation.getArgument(0);
                if (txn.getCountry().equals("Iran")) {
                    return RiskScore.HIGH;
                }
                return RiskScore.LOW;
            });

        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any()))
            .thenAnswer(invocation -> {
                String sender = invocation.getArgument(0);
                return sender.contains("Ali Mohammed");
            });

        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(highAmountRule));
    }

    @Test
    void testEndToEndTransactionProcessing_HighRiskTransaction() {
        // Given: High-risk transaction from sanctioned country
        highRiskTransaction.setId(null); // Let JPA assign ID

        // When: Process the transaction
        transactionRepository.save(highRiskTransaction);
        
        // Verify transaction was saved
        assertNotNull(highRiskTransaction.getId());
        assertTrue(transactionRepository.findById(highRiskTransaction.getId()).isPresent());

        // Verify risk scoring was called
        verify(riskScoringService, times(1)).assessRisk(highRiskTransaction);

        // Verify sanctions check was called
        verify(sanctionsChecker, times(1)).isSanctionedEntity(
            eq("Ali Mohammed"), anyString(), any(), any());

        // Verify rule evaluation was called
        verify(ruleEngine, times(1)).getActiveRules();
    }

    @Test
    void testEndToEndTransactionProcessing_NormalTransaction() {
        // Given: Normal transaction from low-risk country
        normalTransaction.setId(null);

        // When: Process the transaction
        transactionRepository.save(normalTransaction);

        // Verify transaction was saved
        assertNotNull(normalTransaction.getId());
        assertTrue(transactionRepository.findById(normalTransaction.getId()).isPresent());

        // Verify risk scoring was called
        verify(riskScoringService, times(1)).assessRisk(normalTransaction);

        // Verify sanctions check was called
        verify(sanctionsChecker, times(1)).isSanctionedEntity(
            eq("John Doe"), anyString(), any(), any());

        // Verify no alerts were generated for normal transaction
        List<Alert> alerts = alertRepository.findAll();
        assertTrue(alerts.isEmpty());
    }

    @Test
    void testEndToEndAlertGeneration_SanctionsMatch() {
        // Given: Transaction with sanctioned sender
        Transaction sanctionedTransaction = new Transaction("Ali Mohammed", "Unknown", new BigDecimal("1000"), "USD", "USA", "2025-01-01");
        sanctionedTransaction.setId(null);

        // When: Process the transaction
        transactionRepository.save(sanctionedTransaction);

        // Then: Verify sanctions alert was generated
        List<Alert> alerts = alertRepository.findAll();
        assertFalse(alerts.isEmpty());
        
        Alert sanctionsAlert = alerts.stream()
            .filter(alert -> "SANCTIONS".equals(alert.getAlertType()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(sanctionsAlert);
        assertEquals("Ali Mohammed", sanctionsAlert.getMatchedEntityName());
        assertEquals("HIGH", sanctionsAlert.getPriorityLevel());
        assertEquals("Sender is sanctioned", sanctionsAlert.getReason());
    }

    @Test
    void testEndToEndAlertGeneration_HighAmountRule() {
        // Given: Transaction exceeding high amount threshold
        Transaction highAmountTransaction = new Transaction("John Doe", "Jane Smith", new BigDecimal("15000"), "USD", "USA", "2025-01-01");
        highAmountTransaction.setId(null);

        // When: Process the transaction
        transactionRepository.save(highAmountTransaction);

        // Then: Verify high amount alert was generated
        List<Alert> alerts = alertRepository.findAll();
        assertFalse(alerts.isEmpty());
        
        Alert highAmountAlert = alerts.stream()
            .filter(alert -> "RULE_MATCH".equals(alert.getAlertType()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(highAmountAlert);
        assertTrue(highAmountAlert.getReason().contains("High Amount Rule"));
    }

    @Test
    void testEndToEndBatchProcessing() {
        // Given: Multiple transactions
        List<Transaction> transactions = Arrays.asList(
            new Transaction("Alice", "Bob", new BigDecimal("5000"), "USD", "USA", "2025-01-01"),
            new Transaction("Charlie", "David", new BigDecimal("20000"), "EUR", "UK", "2025-01-01"),
            new Transaction("Eve", "Frank", new BigDecimal("8000"), "USD", "Canada", "2025-01-01")
        );

        // When: Process all transactions
        for (Transaction txn : transactions) {
            txn.setId(null);
            transactionRepository.save(txn);
        }

        // Then: Verify all transactions were processed
        assertEquals(3, transactionRepository.count());
        
        // Verify risk scoring was called for each transaction
        verify(riskScoringService, times(3)).assessRisk(any(Transaction.class));
        
        // Verify sanctions check was called for each transaction
        verify(sanctionsChecker, times(3)).isSanctionedEntity(anyString(), anyString(), any(), any());
    }

    @Test
    void testEndToEndDataPersistence() {
        // Given: Transaction with all fields populated
        Transaction completeTransaction = new Transaction("Sender Name", "Receiver Name", new BigDecimal("5000"), "USD", "USA", "2025-01-01");
        completeTransaction.setId(null);
        completeTransaction.setRiskScore(RiskScore.MEDIUM);

        // When: Save and retrieve the transaction
        Transaction savedTransaction = transactionRepository.save(completeTransaction);
        Optional<Transaction> retrievedTransaction = transactionRepository.findById(savedTransaction.getId());

        // Then: Verify all data was persisted correctly
        assertTrue(retrievedTransaction.isPresent());
        Transaction txn = retrievedTransaction.get();
        assertEquals("Sender Name", txn.getSender());
        assertEquals("Receiver Name", txn.getReceiver());
        assertEquals(new BigDecimal("5000"), txn.getAmount());
        assertEquals("USD", txn.getCurrency());
        assertEquals("USA", txn.getCountry());
        assertEquals(RiskScore.MEDIUM, txn.getRiskScore());
    }

    @Test
    void testEndToEndAlertPersistence() {
        // Given: Alert with all fields populated
        Alert alert = new Alert();
        alert.setAlertId("ALERT-001");
        alert.setTransactionId(1);
        alert.setReason("Test alert");
        alert.setAlertType("RULE_MATCH");
        alert.setPriorityLevel("HIGH");
        alert.setMatchedEntityName("Test Entity");
        alert.setMatchedList("Test List");
        alert.setMatchReason("Test match reason");

        // When: Save and retrieve the alert
        Alert savedAlert = alertRepository.save(alert);
        Optional<Alert> retrievedAlert = alertRepository.findById(savedAlert.getId());

        // Then: Verify all data was persisted correctly
        assertTrue(retrievedAlert.isPresent());
        Alert retrieved = retrievedAlert.get();
        assertEquals("ALERT-001", retrieved.getAlertId());
        assertEquals(1, retrieved.getTransactionId());
        assertEquals("Test alert", retrieved.getReason());
        assertEquals("RULE_MATCH", retrieved.getAlertType());
        assertEquals("HIGH", retrieved.getPriorityLevel());
        assertEquals("Test Entity", retrieved.getMatchedEntityName());
        assertEquals("Test List", retrieved.getMatchedList());
        assertEquals("Test match reason", retrieved.getMatchReason());
    }

    @Test
    void testEndToEndTransactionRetrieval() {
        // Given: Multiple transactions in database
        List<Transaction> transactions = Arrays.asList(
            new Transaction("Alice", "Bob", new BigDecimal("1000"), "USD", "USA", "2025-01-01"),
            new Transaction("Charlie", "David", new BigDecimal("2000"), "EUR", "UK", "2025-01-01"),
            new Transaction("Eve", "Frank", new BigDecimal("3000"), "CAD", "Canada", "2025-01-01")
        );

        for (Transaction txn : transactions) {
            txn.setId(null);
            transactionRepository.save(txn);
        }

        // When: Retrieve all transactions
        List<Transaction> allTransactions = transactionRepository.findAll();

        // Then: Verify all transactions were retrieved
        assertEquals(3, allTransactions.size());
        assertTrue(allTransactions.stream().anyMatch(t -> "Alice".equals(t.getSender())));
        assertTrue(allTransactions.stream().anyMatch(t -> "Charlie".equals(t.getSender())));
        assertTrue(allTransactions.stream().anyMatch(t -> "Eve".equals(t.getSender())));
    }

    @Test
    void testEndToEndAlertRetrieval() {
        // Given: Multiple alerts in database
        List<Alert> alerts = Arrays.asList(
            createTestAlert("ALERT-001", "RULE_MATCH", "HIGH"),
            createTestAlert("ALERT-002", "SANCTIONS", "CRITICAL"),
            createTestAlert("ALERT-003", "RULE_MATCH", "MEDIUM")
        );

        for (Alert alert : alerts) {
            alertRepository.save(alert);
        }

        // When: Retrieve all alerts
        List<Alert> allAlerts = alertRepository.findAll();

        // Then: Verify all alerts were retrieved
        assertEquals(3, allAlerts.size());
        assertTrue(allAlerts.stream().anyMatch(a -> "ALERT-001".equals(a.getAlertId())));
        assertTrue(allAlerts.stream().anyMatch(a -> "ALERT-002".equals(a.getAlertId())));
        assertTrue(allAlerts.stream().anyMatch(a -> "ALERT-003".equals(a.getAlertId())));
    }

    private Alert createTestAlert(String alertId, String alertType, String priorityLevel) {
        Alert alert = new Alert();
        alert.setAlertId(alertId);
        alert.setTransactionId(1);
        alert.setReason("Test alert " + alertId);
        alert.setAlertType(alertType);
        alert.setPriorityLevel(priorityLevel);
        alert.setMatchedEntityName("Test Entity " + alertId);
        alert.setMatchedList("Test List");
        alert.setMatchReason("Test match reason");
        return alert;
    }
} 