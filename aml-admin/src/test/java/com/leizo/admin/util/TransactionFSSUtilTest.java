package com.leizo.admin.util;

import com.leizo.enums.RiskScore;
import com.leizo.pojo.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFSSUtilTest {

    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        transactions = new ArrayList<>();
        
        Transaction t1 = new Transaction("Alice", "Bob", new BigDecimal("1000"), "USD", "USA", "1990-01-01");
        t1.setRiskScore(RiskScore.LOW);
        t1.setId(1);

        Transaction t2 = new Transaction("Alice", "Charlie", new BigDecimal("5000"), "USD", "USA", "1990-01-01");
        t2.setRiskScore(RiskScore.MEDIUM);
        t2.setId(2);

        Transaction t3 = new Transaction("David", "Bob", new BigDecimal("20000"), "USD", "Canada", "1990-01-01");
        t3.setRiskScore(RiskScore.HIGH);
        t3.setId(3);

        transactions.add(t1);
        transactions.add(t2);
        transactions.add(t3);
    }

    @Test
    void testFilter_BySenderCountryAmountRange() {
        List<Transaction> filtered = TransactionFSS.filter(transactions, "Alice", "USA",
                new BigDecimal("1000"), new BigDecimal("6000"));

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(t -> "Alice".equals(t.getSender())));
        assertTrue(filtered.stream().allMatch(t -> "USA".equals(t.getCountry())));
    }

    @Test
    void testFilter_BySenderOnly() {
        List<Transaction> filtered = TransactionFSS.filter(transactions, "Alice", null,
                null, null);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(t -> "Alice".equals(t.getSender())));
    }

    @Test
    void testFilter_ByCountryOnly() {
        List<Transaction> filtered = TransactionFSS.filter(transactions, null, "USA",
                null, null);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(t -> "USA".equals(t.getCountry())));
    }

    @Test
    void testFilter_ByAmountRangeOnly() {
        List<Transaction> filtered = TransactionFSS.filter(transactions, null, null,
                new BigDecimal("1000"), new BigDecimal("6000"));

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(t -> 
            t.getAmount().compareTo(new BigDecimal("1000")) >= 0 && 
            t.getAmount().compareTo(new BigDecimal("6000")) <= 0));
    }

    @Test
    void testSearchByReceiver() {
        List<Transaction> result = TransactionFSS.searchByReceiver(transactions, "Bob");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> "Bob".equals(t.getReceiver())));

        result = TransactionFSS.searchByReceiver(transactions, "Charlie");
        assertEquals(1, result.size());
        assertEquals("Charlie", result.get(0).getReceiver());
    }

    @Test
    void testSearchByReceiver_NoMatch() {
        List<Transaction> result = TransactionFSS.searchByReceiver(transactions, "Nonexistent");
        assertEquals(0, result.size());
    }

    @Test
    void testSortByAmount_Ascending() {
        Transaction[] sorted = TransactionFSS.sortByAmount(transactions, false);
        assertTrue(sorted[0].getAmount().compareTo(sorted[1].getAmount()) <= 0);
        assertTrue(sorted[1].getAmount().compareTo(sorted[2].getAmount()) <= 0);
    }

    @Test
    void testSortByAmount_Descending() {
        Transaction[] sorted = TransactionFSS.sortByAmount(transactions, true);
        assertTrue(sorted[0].getAmount().compareTo(sorted[1].getAmount()) >= 0);
        assertTrue(sorted[1].getAmount().compareTo(sorted[2].getAmount()) >= 0);
    }

    @Test
    void testSortByRiskScore_Ascending() {
        Transaction[] sorted = TransactionFSS.sortByRiskScore(transactions, false);
        assertEquals(RiskScore.LOW, sorted[0].getRiskScore());
        assertEquals(RiskScore.MEDIUM, sorted[1].getRiskScore());
        assertEquals(RiskScore.HIGH, sorted[2].getRiskScore());
    }

    @Test
    void testSortByRiskScore_Descending() {
        Transaction[] sorted = TransactionFSS.sortByRiskScore(transactions, true);
        assertEquals(RiskScore.HIGH, sorted[0].getRiskScore());
        assertEquals(RiskScore.MEDIUM, sorted[1].getRiskScore());
        assertEquals(RiskScore.LOW, sorted[2].getRiskScore());
    }

    @Test
    void testSortByRiskScore_WithNullRiskScores() {
        // Add transaction with null risk score
        Transaction t4 = new Transaction("Eve", "Frank", new BigDecimal("3000"), "EUR", "UK", "1990-01-01");
        t4.setRiskScore(null);
        t4.setId(4);
        transactions.add(t4);

        Transaction[] sorted = TransactionFSS.sortByRiskScore(transactions, false);
        // Null risk scores should be handled gracefully
        assertNotNull(sorted);
        assertEquals(4, sorted.length);
    }

    @Test
    void testFilter_EmptyList() {
        List<Transaction> emptyList = new ArrayList<>();
        List<Transaction> result = TransactionFSS.filter(emptyList, "Alice", "USA", 
                new BigDecimal("1000"), new BigDecimal("6000"));
        assertEquals(0, result.size());
    }

    @Test
    void testSearchByReceiver_EmptyList() {
        List<Transaction> emptyList = new ArrayList<>();
        List<Transaction> result = TransactionFSS.searchByReceiver(emptyList, "Bob");
        assertEquals(0, result.size());
    }

    @Test
    void testSortByAmount_EmptyList() {
        List<Transaction> emptyList = new ArrayList<>();
        Transaction[] result = TransactionFSS.sortByAmount(emptyList, false);
        assertEquals(0, result.length);
    }
} 