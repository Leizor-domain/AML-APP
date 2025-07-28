package com.leizo.admin.util;

import com.leizo.admin.entity.Alert;
import com.leizo.admin.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertFSSUtilTest {

    private List<Alert> alerts;

    @BeforeEach
    void setUp() {
        Transaction txn = new Transaction("Sender1", "Receiver1", new BigDecimal("5000"), "USD", "USA", "1990-01-01");

        Alert a1 = new Alert();
        a1.setAlertId("A1");
        a1.setTransaction(txn);
        a1.setReason("Violation of threshold");
        a1.setPriorityLevel("High");
        a1.setPriorityScore(90);

        Alert a2 = new Alert();
        a2.setAlertId("A2");
        a2.setTransaction(txn);
        a2.setReason("Suspicious behavior");
        a2.setPriorityLevel("Medium");
        a2.setPriorityScore(50);

        Alert a3 = new Alert();
        a3.setAlertId("A3");
        a3.setTransaction(txn);
        a3.setReason("Routine check");
        a3.setPriorityLevel("Low");
        a3.setPriorityScore(20);

        alerts = List.of(a1, a2, a3);
    }

    @Test
    void testFilter() {
        List<Alert> filtered = AlertFSS.filter(alerts, "Sender1", "High");
        assertEquals(1, filtered.size());
        assertEquals("High", filtered.get(0).getPriorityLevel());
    }

    @Test
    void testSearchAlertsByReason() {
        List<Alert> found = AlertFSS.searchAlertsByReason(alerts, "suspicious");
        assertEquals(1, found.size());
        assertTrue(found.get(0).getReason().toLowerCase().contains("suspicious"));
    }

    @Test
    void testSortByPriorityAsc() {
        Alert[] sorted = AlertFSS.sortByPriority(alerts.toArray(new Alert[0]), false);
        assertTrue(sorted[0].getPriorityScore() <= sorted[1].getPriorityScore());
    }

    @Test
    void testSortByPriorityDesc() {
        Alert[] sorted = AlertFSS.sortByPriority(alerts.toArray(new Alert[0]), true);
        assertTrue(sorted[0].getPriorityScore() >= sorted[1].getPriorityScore());
    }
} 