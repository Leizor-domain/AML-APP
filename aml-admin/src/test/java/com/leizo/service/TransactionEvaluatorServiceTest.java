package com.leizo.service;

import com.leizo.admin.entity.Alert;
import com.leizo.admin.entity.Rule;
import com.leizo.admin.entity.Transaction;
import com.leizo.enums.IngestionStatus;
import com.leizo.enums.RuleSensitivity;
import com.leizo.loader.RuleLoader;
import com.leizo.loader.SanctionListLoader;
import com.leizo.model.IngestionResult;
import com.leizo.service.impl.TransactionEvaluatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionEvaluatorServiceTest {

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private SanctionsChecker sanctionsChecker;

    @Mock
    private RiskScoringService riskScoringService;

    @Mock
    private AlertService alertService;

    @Mock
    private LoggerService loggerService;

    @Mock
    private SanctionListLoader sanctionListLoader;

    @Mock
    private RuleLoader ruleLoader;

    private TransactionEvaluatorServiceImpl transactionEvaluator;

    @BeforeEach
    void setUp() {
        transactionEvaluator = new TransactionEvaluatorServiceImpl(
            ruleEngine, sanctionsChecker, riskScoringService, 
            alertService, loggerService, sanctionListLoader, ruleLoader
        );
    }
    
    @Test
    void testEvaluateTransaction_ValidTransaction_ReturnsSuccess() {
        // Arrange
        Transaction transaction = createValidTransaction();
        when(riskScoringService.calculateRiskScore(any(Transaction.class))).thenReturn(25);
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(false);
        when(alertService.isDuplicateAlert(any(Transaction.class), anyString())).thenReturn(false);
        when(alertService.isInCooldown(anyString(), anyString())).thenReturn(false);
        
        // Act
        IngestionResult result = transactionEvaluator.evaluateTransaction(transaction);
        
        // Assert
        assertNotNull(result);
        assertEquals(IngestionStatus.SUCCESS, result.getStatus());
        assertFalse(result.isAlertGenerated());
        assertEquals(25, result.getRiskScore());
    }
    
    @Test
    void testEvaluateTransaction_SanctionsMatch_TriggersAlert() {
        // Arrange
        Transaction transaction = createValidTransaction();
        when(riskScoringService.calculateRiskScore(any(Transaction.class))).thenReturn(100);
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(true);
        when(alertService.isDuplicateAlert(any(Transaction.class), anyString())).thenReturn(false);
        
        // Act
        IngestionResult result = transactionEvaluator.evaluateTransaction(transaction);
        
        // Assert
        assertNotNull(result);
        assertEquals(IngestionStatus.SUCCESS, result.getStatus());
        assertTrue(result.isAlertGenerated());
        assertEquals(100, result.getRiskScore());
    }
    
    @Test
    void testEvaluateTransaction_HighValueRule_TriggersAlert() {
        // Arrange
        Transaction transaction = createHighValueTransaction();
        Rule highValueRule = createHighValueRule();
        
        when(riskScoringService.calculateRiskScore(any(Transaction.class))).thenReturn(50);
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(false);
        when(alertService.isDuplicateAlert(any(Transaction.class), anyString())).thenReturn(false);
        when(alertService.isInCooldown(anyString(), anyString())).thenReturn(false);
        
        // Act
        AlertDecisionResult alertResult = transactionEvaluator.evaluateForAlert(transaction);
        
        // Assert
        assertNotNull(alertResult);
        assertTrue(alertResult.shouldTriggerAlert());
        assertEquals("Rule matched: High Value Transfer Rule", alertResult.getReason());
        assertEquals(50, alertResult.getRiskScore());
        assertTrue(alertResult.getPriorityScore() > 50); // Should include rule weight
    }
    
    @Test
    void testEvaluateTransaction_DuplicateAlert_SuppressesAlert() {
        // Arrange
        Transaction transaction = createValidTransaction();
        when(riskScoringService.calculateRiskScore(any(Transaction.class))).thenReturn(25);
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(false);
        when(alertService.isDuplicateAlert(any(Transaction.class), anyString())).thenReturn(true);
        
        // Act
        AlertDecisionResult alertResult = transactionEvaluator.evaluateForAlert(transaction);
        
        // Assert
        assertNotNull(alertResult);
        assertFalse(alertResult.shouldTriggerAlert());
        assertTrue(alertResult.isDuplicate());
    }
    
    @Test
    void testEvaluateTransaction_CooldownActive_SuppressesAlert() {
        // Arrange
        Transaction transaction = createValidTransaction();
        when(riskScoringService.calculateRiskScore(any(Transaction.class))).thenReturn(25);
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(false);
        when(alertService.isDuplicateAlert(any(Transaction.class), anyString())).thenReturn(false);
        when(alertService.isInCooldown(anyString(), anyString())).thenReturn(true);
        
        // Act
        AlertDecisionResult alertResult = transactionEvaluator.evaluateForAlert(transaction);
        
        // Assert
        assertNotNull(alertResult);
        assertFalse(alertResult.shouldTriggerAlert());
        assertTrue(alertResult.isInCooldown());
    }

    @Test
    void testCheckSanctions_OfacMatch_ReturnsMatchResult() {
        // Arrange
        Transaction transaction = createValidTransaction();
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(true);
        
        // Act
        SanctionsMatchResult result = transactionEvaluator.checkSanctions(transaction);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isSanctioned());
        assertEquals("OFAC_SDN", result.getMatchedList());
        assertEquals("OFAC", result.getSanctioningBody());
        assertEquals(1.0, result.getConfidenceScore());
    }
    
    @Test
    void testCheckSanctions_CountryMatch_ReturnsMatchResult() {
        // Arrange
        Transaction transaction = createValidTransaction();
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(false);
        when(sanctionsChecker.checkCountry(anyString())).thenReturn(true);
        
        // Act
        SanctionsMatchResult result = transactionEvaluator.checkSanctions(transaction);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isSanctioned());
        assertEquals("COUNTRY_SANCTIONS", result.getMatchedList());
        assertEquals("UN", result.getSanctioningBody());
    }
    
    @Test
    void testCheckSanctions_NoMatch_ReturnsNoMatch() {
        // Arrange
        Transaction transaction = createValidTransaction();
        when(sanctionsChecker.isSanctionedEntity(eq("John Doe"), eq("USA"), isNull(), eq("Any"))).thenReturn(false);
        when(sanctionsChecker.checkCountry(anyString())).thenReturn(false);
        when(sanctionsChecker.checkName(anyString())).thenReturn(false);
        when(sanctionsChecker.checkPartialName(anyString())).thenReturn(false);
        
        // Act
        SanctionsMatchResult result = transactionEvaluator.checkSanctions(transaction);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSanctioned());
        assertEquals(0.0, result.getConfidenceScore());
    }
    
    @Test
    void testCalculateRiskScore_HighRiskCountry_AddsRiskPoints() {
        // Arrange
        Transaction transaction = createHighRiskCountryTransaction();
        when(riskScoringService.calculateRiskScore(any(Transaction.class))).thenReturn(35);
        
        // Act
        int riskScore = transactionEvaluator.calculateRiskScore(transaction);
        
        // Assert
        assertEquals(35, riskScore); // Should delegate to riskScoringService
    }
    
    @Test
    void testCalculateRiskScore_HighValue_AddsRiskPoints() {
        // Arrange
        Transaction transaction = createHighValueTransaction();
        when(riskScoringService.calculateRiskScore(any(Transaction.class))).thenReturn(45);
        
        // Act
        int riskScore = transactionEvaluator.calculateRiskScore(transaction);
        
        // Assert
        assertEquals(45, riskScore); // Should delegate to riskScoringService
    }
    
    @Test
    void testGetActiveRules_ReturnsCombinedRules() {
        // Act
        List<Rule> rules = transactionEvaluator.getActiveRules();
        
        // Assert
        assertNotNull(rules);
        assertTrue(rules.size() >= 6); // Should include hardcoded rules
    }
    
    @Test
    void testGetEvaluationStats_ReturnsComprehensiveStats() {
        // Act
        Map<String, Object> stats = transactionEvaluator.getEvaluationStats();
        
        // Assert
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalTransactionsEvaluated"));
        assertTrue(stats.containsKey("totalAlertsGenerated"));
        assertTrue(stats.containsKey("totalSanctionsMatches"));
        assertTrue(stats.containsKey("activeRulesCount"));
        assertTrue(stats.containsKey("highRiskCountriesCount"));
    }
    
    @Test
    void testAddAndRemoveRule_ManagesRulesCorrectly() {
        // Arrange
        Rule testRule = new Rule("Test Rule", RuleSensitivity.MEDIUM, null, Set.of("test"));
        
        // Act & Assert
        transactionEvaluator.addRule(testRule);
        List<Rule> rulesAfterAdd = transactionEvaluator.getActiveRules();
        assertTrue(rulesAfterAdd.stream().anyMatch(r -> r.getDescription().equals("Test Rule")));
        
        transactionEvaluator.removeRule("Test Rule");
        List<Rule> rulesAfterRemove = transactionEvaluator.getActiveRules();
        assertFalse(rulesAfterRemove.stream().anyMatch(r -> r.getDescription().equals("Test Rule")));
    }

    private Transaction createValidTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setSender("John Doe");
        transaction.setReceiver("Jane Smith");
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setCurrency("USD");
        transaction.setCountry("USA");
        return transaction;
    }

    private Transaction createHighValueTransaction() {
        Transaction transaction = createValidTransaction();
        transaction.setAmount(new BigDecimal("15000.00"));
        return transaction;
    }

    private Transaction createHighRiskCountryTransaction() {
        Transaction transaction = createValidTransaction();
        transaction.setCountry("Afghanistan");
        return transaction;
    }

    private Rule createHighValueRule() {
        return new Rule("High Value Transfer Rule", RuleSensitivity.HIGH, null, Set.of("value", "priority"));
    }
} 