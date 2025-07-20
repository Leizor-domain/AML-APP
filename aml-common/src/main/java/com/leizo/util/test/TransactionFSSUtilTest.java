package com.leizo.util.test;

import com.leizo.enums.RiskScore;
import com.leizo.model.Transaction;
import com.leizo.util.TransactionFSS;
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

        Transaction t2 = new Transaction("Alice", "Charlie", new BigDecimal("5000"), "USD", "USA", "1990-01-01");
        t2.setRiskScore(RiskScore.MEDIUM);

        Transaction t3 = new Transaction("David", "Bob", new BigDecimal("20000"), "USD", "Canada", "1990-01-01");
        t3.setRiskScore(RiskScore.HIGH);

        transactions.add(t1);
        transactions.add(t2);
        transactions.add(t3);
    }

    @Test
    void testFilter_BySenderCountryAmountRange() {
        List<Transaction> filtered = TransactionFSS.filter(transactions, "Alice", "USA",
                new BigDecimal("1000"), new BigDecimal("6000"));

        assertEquals(2, filtered.size());
    }

    @Test
    void testSearch_ByReceiver() {
        List<Transaction> result = TransactionFSS.searchByReceiver(transactions, "Bob");
        assertEquals(2, result.size());

        result = TransactionFSS.searchByReceiver(transactions, "Charlie");
        assertEquals(1, result.size());
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
    void testSortByRisk_Ascending() {
        Transaction[] sorted = TransactionFSS.sortByRiskScore(transactions, false);
        assertEquals(RiskScore.LOW, sorted[0].getRiskScore());
        assertEquals(RiskScore.MEDIUM, sorted[1].getRiskScore());
        assertEquals(RiskScore.HIGH, sorted[2].getRiskScore());
    }

    @Test
    void testSortByRisk_Descending() {
        Transaction[] sorted = TransactionFSS.sortByRiskScore(transactions, true);
        assertEquals(RiskScore.HIGH, sorted[0].getRiskScore());
        assertEquals(RiskScore.MEDIUM, sorted[1].getRiskScore());
        assertEquals(RiskScore.LOW, sorted[2].getRiskScore());
    }
}
