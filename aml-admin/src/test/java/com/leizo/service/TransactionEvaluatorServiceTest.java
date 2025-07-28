package com.leizo.service;

import com.leizo.pojo.entity.Alert;
import com.leizo.pojo.entity.Rule;
import com.leizo.pojo.entity.Transaction;
import com.leizo.enums.IngestionStatus;
import com.leizo.enums.RuleSensitivity;
import com.leizo.enums.RiskScore;
import com.leizo.model.IngestionResult;
import com.leizo.service.impl.TransactionEvaluatorServiceImpl;
import com.leizo.admin.repository.AlertRepository;
import com.leizo.admin.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionEvaluatorServiceTest {

    @Mock
    private AlertDecisionEngine alertDecisionEngine;

    @Mock
    private RiskScoringService riskScoringService;

    @Mock
    private SanctionsChecker sanctionsChecker;

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionEvaluatorServiceImpl transactionEvaluatorService;

    private Transaction validTransaction;
    private Transaction highRiskTransaction;
    private Rule testRule;

    @BeforeEach
    void setUp() {
        // Create a valid transaction
        validTransaction = new Transaction("John Doe", "Jane Smith", new BigDecimal("1000"), "USD", "USA", "2025-01-01");
        validTransaction.setId(1);

        // Create a high-risk transaction
        highRiskTransaction = new Transaction("Ali Mohammed", "Unknown", new BigDecimal("50000"), "USD", "Iran", "2025-01-01");
        highRiskTransaction.setId(2);

        // Create a test rule with proper constructor
        testRule = new Rule("High Amount Rule", RuleSensitivity.HIGH, 
            (txn, amount) -> amount.compareTo(new BigDecimal("10000")) > 0, 
            Set.of("amount", "high"));
    }

    @Test
    void testEvaluateTransaction_ValidTransaction_ReturnsSuccess() {
        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.LOW);
        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(testRule));
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);
        when(alertDecisionEngine.shouldSuppressAlert(any(Transaction.class), anyString())).thenReturn(false);

        // Execute
        IngestionResult result = transactionEvaluatorService.evaluateTransaction(validTransaction);

        // Verify
        assertNotNull(result);
        assertEquals(IngestionStatus.SUCCESS, result.getStatus());
        assertFalse(result.isAlertGenerated());
    }

    @Test
    void testEvaluateTransaction_HighRiskTransaction_GeneratesAlert() {
        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.HIGH);
        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(testRule));
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);
        when(alertDecisionEngine.shouldSuppressAlert(any(Transaction.class), anyString())).thenReturn(false);
        
        Alert mockAlert = new Alert();
        mockAlert.setAlertId("ALERT-001");
        mockAlert.setReason("High amount transaction detected");
        
        when(alertDecisionEngine.createAlert(any(Transaction.class), any(Rule.class), anyString(), anyInt(), isNull()))
            .thenReturn(mockAlert);

        // Execute
        IngestionResult result = transactionEvaluatorService.evaluateTransaction(highRiskTransaction);

        // Verify
        assertNotNull(result);
        assertEquals(IngestionStatus.SUCCESS, result.getStatus());
        assertTrue(result.isAlertGenerated());
        assertEquals("ALERT-001", result.getAlertId());
    }

    @Test
    void testEvaluateTransaction_SanctionsMatch_GeneratesSanctionsAlert() {
        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.HIGH);
        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(testRule));
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(true);
        when(alertDecisionEngine.shouldSuppressAlert(any(Transaction.class), anyString())).thenReturn(false);
        
        Alert sanctionsAlert = new Alert();
        sanctionsAlert.setAlertId("SANCTIONS-001");
        sanctionsAlert.setReason("Sender matched OFAC entity: Ali Mohammed");
        
        when(alertDecisionEngine.createAlert(any(Transaction.class), isNull(), anyString(), anyInt(), any()))
            .thenReturn(sanctionsAlert);

        // Execute
        IngestionResult result = transactionEvaluatorService.evaluateTransaction(highRiskTransaction);

        // Verify
        assertNotNull(result);
        assertEquals(IngestionStatus.SUCCESS, result.getStatus());
        assertTrue(result.isAlertGenerated());
        assertEquals("SANCTIONS-001", result.getAlertId());
    }

    @Test
    void testEvaluateTransaction_InvalidTransaction_ReturnsFailure() {
        // Create invalid transaction (null sender)
        Transaction invalidTransaction = new Transaction(null, "Jane Smith", new BigDecimal("1000"), "USD", "USA", "2025-01-01");

        // Execute
        IngestionResult result = transactionEvaluatorService.evaluateTransaction(invalidTransaction);

        // Verify
        assertNotNull(result);
        assertEquals(IngestionStatus.EVALUATION_FAILED, result.getStatus());
        assertFalse(result.isAlertGenerated());
    }

    @Test
    void testEvaluateTransaction_ExceptionHandling_ReturnsFailure() {
        // Mock dependencies to throw exception
        when(riskScoringService.assessRisk(any(Transaction.class))).thenThrow(new RuntimeException("Database error"));

        // Execute
        IngestionResult result = transactionEvaluatorService.evaluateTransaction(validTransaction);

        // Verify
        assertNotNull(result);
        assertEquals(IngestionStatus.EVALUATION_FAILED, result.getStatus());
        assertFalse(result.isAlertGenerated());
    }

    @Test
    void testGetActiveRules() {
        // Mock rule engine
        List<Rule> rules = Arrays.asList(testRule);
        when(ruleEngine.getActiveRules()).thenReturn(rules);

        // Execute
        List<Rule> result = transactionEvaluatorService.getActiveRules();

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("High Amount Rule", result.get(0).getDescription());
    }

    @Test
    void testGetEvaluationStats() {
        // Mock dependencies
        when(riskScoringService.getHighRiskCountries()).thenReturn(Set.of("Iran", "North Korea", "Syria"));
        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(testRule));

        // Execute
        Map<String, Object> stats = transactionEvaluatorService.getEvaluationStats();

        // Verify
        assertNotNull(stats);
        assertEquals(3, stats.get("highRiskCountryCount"));
        assertEquals(1, stats.get("activeRuleCount"));
        assertNotNull(stats.get("lastUpdated"));
    }

    @Test
    void testEvaluateTransaction_WithMultipleRules() {
        // Create multiple rules
        Rule rule1 = new Rule("Amount Rule", RuleSensitivity.HIGH, 
            (txn, amount) -> amount.compareTo(new BigDecimal("10000")) > 0, Set.of("amount"));
        Rule rule2 = new Rule("Country Rule", RuleSensitivity.MEDIUM, 
            (txn, amount) -> "Iran".equals(txn.getCountry()), Set.of("country"));
        
        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.MEDIUM);
        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(rule1, rule2));
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);
        when(alertDecisionEngine.shouldSuppressAlert(any(Transaction.class), anyString())).thenReturn(false);

        // Execute
        IngestionResult result = transactionEvaluatorService.evaluateTransaction(validTransaction);

        // Verify
        assertNotNull(result);
        assertEquals(IngestionStatus.SUCCESS, result.getStatus());
    }

    @Test
    void testEvaluateTransaction_WithManualFlag() {
        // Create transaction with manual flag in metadata
        Transaction manualTransaction = new Transaction("John Doe", "Jane Smith", new BigDecimal("1000"), "USD", "USA", "2025-01-01");
        manualTransaction.getMetadata().put("manualFlag", "true");

        // Mock dependencies
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.HIGH);
        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(testRule));
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);
        when(alertDecisionEngine.shouldSuppressAlert(any(Transaction.class), anyString())).thenReturn(false);
        
        Alert manualAlert = new Alert();
        manualAlert.setAlertId("MANUAL-001");
        manualAlert.setReason("Manual flag detected");
        
        when(alertDecisionEngine.createAlert(any(Transaction.class), any(Rule.class), anyString(), anyInt(), isNull()))
            .thenReturn(manualAlert);

        // Execute
        IngestionResult result = transactionEvaluatorService.evaluateTransaction(manualTransaction);

        // Verify
        assertNotNull(result);
        assertEquals(IngestionStatus.SUCCESS, result.getStatus());
        assertTrue(result.isAlertGenerated());
        assertEquals("MANUAL-001", result.getAlertId());
    }
} 