package com.leizo.admin.controller;

import com.leizo.admin.monitoring.TransactionMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Autowired
    private TransactionMetrics transactionMetrics;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Transaction metrics
        metrics.put("transactionsProcessed", transactionMetrics.getTransactionsProcessedCount());
        metrics.put("alertsGenerated", transactionMetrics.getAlertsGeneratedCount());
        metrics.put("sanctionsMatches", transactionMetrics.getSanctionsMatchesCount());
        metrics.put("ruleMatches", transactionMetrics.getRuleMatchesCount());
        metrics.put("processingErrors", transactionMetrics.getProcessingErrorsCount());
        
        // Business metrics
        metrics.put("countryTransactionCounts", transactionMetrics.getCountryTransactionCounts());
        metrics.put("currencyTransactionCounts", transactionMetrics.getCurrencyTransactionCounts());
        metrics.put("averageTransactionAmount", transactionMetrics.getAverageTransactionAmount());
        metrics.put("highRiskTransactionPercentage", transactionMetrics.getHighRiskTransactionPercentage());
        
        // System metrics
        transactionMetrics.recordMemoryUsage();
        transactionMetrics.recordSystemHealth();
        
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Basic health check
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("uptime", System.currentTimeMillis() / 1000);
        health.put("threadCount", Thread.activeCount());
        
        // Memory health
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", totalMemory);
        memory.put("used", usedMemory);
        memory.put("free", freeMemory);
        memory.put("usagePercentage", (double) usedMemory / totalMemory * 100);
        health.put("memory", memory);
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        Map<String, Object> performance = new HashMap<>();
        
        // Record current performance metrics
        transactionMetrics.recordMemoryUsage();
        transactionMetrics.recordSystemHealth();
        
        // Performance indicators
        performance.put("activeTransactions", transactionMetrics.getActiveTransactionCount());
        performance.put("activeAlerts", transactionMetrics.getActiveAlertCount());
        performance.put("averageTransactionAmount", transactionMetrics.getAverageTransactionAmount());
        performance.put("highRiskPercentage", transactionMetrics.getHighRiskTransactionPercentage());
        
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/business")
    public ResponseEntity<Map<String, Object>> getBusinessMetrics() {
        Map<String, Object> business = new HashMap<>();
        
        // Business intelligence metrics
        business.put("countryDistribution", transactionMetrics.getCountryTransactionCounts());
        business.put("currencyDistribution", transactionMetrics.getCurrencyTransactionCounts());
        business.put("totalTransactions", transactionMetrics.getTransactionsProcessedCount());
        business.put("totalAlerts", transactionMetrics.getAlertsGeneratedCount());
        business.put("sanctionsMatches", transactionMetrics.getSanctionsMatchesCount());
        business.put("ruleMatches", transactionMetrics.getRuleMatchesCount());
        business.put("errorRate", calculateErrorRate());
        
        return ResponseEntity.ok(business);
    }

    private double calculateErrorRate() {
        double totalTransactions = transactionMetrics.getTransactionsProcessedCount();
        double totalErrors = transactionMetrics.getProcessingErrorsCount();
        
        if (totalTransactions > 0) {
            return (totalErrors / totalTransactions) * 100.0;
        }
        return 0.0;
    }
} 