package com.leizo.admin.monitoring;

import com.leizo.pojo.entity.Transaction;
import com.leizo.pojo.entity.Alert;
import com.leizo.enums.RiskScore;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.List;

@Component
public class TransactionMetrics {

    private final MeterRegistry meterRegistry;
    
    // Counters
    private final Counter transactionsProcessedCounter;
    private final Counter alertsGeneratedCounter;
    private final Counter sanctionsMatchesCounter;
    private final Counter ruleMatchesCounter;
    private final Counter processingErrorsCounter;
    
    // Timers
    private final Timer transactionProcessingTimer;
    private final Timer riskAssessmentTimer;
    private final Timer sanctionsCheckTimer;
    private final Timer alertGenerationTimer;
    
    // Custom metrics
    private final AtomicLong totalTransactionAmount = new AtomicLong(0);
    private final AtomicLong highRiskTransactionCount = new AtomicLong(0);
    private final Map<String, AtomicLong> countryTransactionCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> currencyTransactionCounts = new ConcurrentHashMap<>();

    @Autowired
    public TransactionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.transactionsProcessedCounter = Counter.builder("aml.transactions.processed")
            .description("Total number of transactions processed")
            .register(meterRegistry);
            
        this.alertsGeneratedCounter = Counter.builder("aml.alerts.generated")
            .description("Total number of alerts generated")
            .register(meterRegistry);
            
        this.sanctionsMatchesCounter = Counter.builder("aml.sanctions.matches")
            .description("Total number of sanctions matches found")
            .register(meterRegistry);
            
        this.ruleMatchesCounter = Counter.builder("aml.rule.matches")
            .description("Total number of rule matches found")
            .register(meterRegistry);
            
        this.processingErrorsCounter = Counter.builder("aml.processing.errors")
            .description("Total number of processing errors")
            .register(meterRegistry);
        
        // Initialize timers
        this.transactionProcessingTimer = Timer.builder("aml.transaction.processing.time")
            .description("Time taken to process a transaction")
            .register(meterRegistry);
            
        this.riskAssessmentTimer = Timer.builder("aml.risk.assessment.time")
            .description("Time taken for risk assessment")
            .register(meterRegistry);
            
        this.sanctionsCheckTimer = Timer.builder("aml.sanctions.check.time")
            .description("Time taken for sanctions check")
            .register(meterRegistry);
            
        this.alertGenerationTimer = Timer.builder("aml.alert.generation.time")
            .description("Time taken to generate an alert")
            .register(meterRegistry);
    }

    // Transaction processing metrics
    public void recordTransactionProcessed(Transaction transaction) {
        transactionsProcessedCounter.increment();
        
        // Update amount metrics
        if (transaction.getAmount() != null) {
            totalTransactionAmount.addAndGet(transaction.getAmount().longValue());
        }
        
        // Update risk metrics
        if (RiskScore.HIGH.equals(transaction.getRiskScore())) {
            highRiskTransactionCount.incrementAndGet();
        }
        
        // Update country metrics
        if (transaction.getCountry() != null) {
            countryTransactionCounts.computeIfAbsent(transaction.getCountry(), k -> new AtomicLong(0))
                .incrementAndGet();
        }
        
        // Update currency metrics
        if (transaction.getCurrency() != null) {
            currencyTransactionCounts.computeIfAbsent(transaction.getCurrency(), k -> new AtomicLong(0))
                .incrementAndGet();
        }
    }

    public void recordAlertGenerated(Alert alert) {
        alertsGeneratedCounter.increment();
        
        // Record specific alert types
        if ("SANCTIONS".equals(alert.getAlertType())) {
            sanctionsMatchesCounter.increment();
        } else if ("RULE_MATCH".equals(alert.getAlertType())) {
            ruleMatchesCounter.increment();
        }
    }

    public void recordProcessingError(String errorType) {
        processingErrorsCounter.increment();
        
        // Record specific error types
        Counter.builder("aml.processing.errors.by.type")
            .tag("error.type", errorType)
            .description("Processing errors by type")
            .register(meterRegistry)
            .increment();
    }

    // Timer methods
    public Timer.Sample startTransactionProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTransactionProcessingTimer(Timer.Sample sample) {
        sample.stop(transactionProcessingTimer);
    }

    public Timer.Sample startRiskAssessmentTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopRiskAssessmentTimer(Timer.Sample sample) {
        sample.stop(riskAssessmentTimer);
    }

    public Timer.Sample startSanctionsCheckTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopSanctionsCheckTimer(Timer.Sample sample) {
        sample.stop(sanctionsCheckTimer);
    }

    public Timer.Sample startAlertGenerationTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopAlertGenerationTimer(Timer.Sample sample) {
        sample.stop(alertGenerationTimer);
    }

    // Gauge methods
    public double getActiveTransactionCount() {
        // This would typically query the database for active transactions
        // For now, return a placeholder value
        return transactionsProcessedCounter.count();
    }

    public double getActiveAlertCount() {
        // This would typically query the database for active alerts
        // For now, return a placeholder value
        return alertsGeneratedCounter.count();
    }

    public double getAverageTransactionAmount() {
        long totalAmount = totalTransactionAmount.get();
        long totalTransactions = (long) transactionsProcessedCounter.count();
        
        if (totalTransactions > 0) {
            return (double) totalAmount / totalTransactions;
        }
        return 0.0;
    }

    public double getHighRiskTransactionPercentage() {
        long highRiskCount = highRiskTransactionCount.get();
        long totalTransactions = (long) transactionsProcessedCounter.count();
        
        if (totalTransactions > 0) {
            return (double) highRiskCount / totalTransactions * 100.0;
        }
        return 0.0;
    }

    // Public getter methods for monitoring
    public double getTransactionsProcessedCount() {
        return transactionsProcessedCounter.count();
    }

    public double getAlertsGeneratedCount() {
        return alertsGeneratedCounter.count();
    }

    public double getSanctionsMatchesCount() {
        return sanctionsMatchesCounter.count();
    }

    public double getRuleMatchesCount() {
        return ruleMatchesCounter.count();
    }

    public double getProcessingErrorsCount() {
        return processingErrorsCounter.count();
    }

    // Custom metrics methods
    public Map<String, Long> getCountryTransactionCounts() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        countryTransactionCounts.forEach((country, count) -> 
            result.put(country, count.get()));
        return result;
    }

    public Map<String, Long> getCurrencyTransactionCounts() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        currencyTransactionCounts.forEach((currency, count) -> 
            result.put(currency, count.get()));
        return result;
    }

    public void recordBatchProcessingMetrics(int batchSize, long processingTimeMs) {
        // Record batch processing metrics
        Timer.builder("aml.batch.processing.time")
            .tag("batch.size", String.valueOf(batchSize))
            .description("Time taken to process a batch of transactions")
            .register(meterRegistry)
            .record(processingTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void recordMemoryUsage() {
        // Memory usage metrics are recorded via simple counters
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        // Record memory usage as counters instead of gauges
        Counter.builder("aml.memory.used.bytes")
            .description("Memory used by the application")
            .register(meterRegistry)
            .increment((long) usedMemory);
    }

    public void recordDatabaseMetrics(long queryTimeMs, String queryType) {
        // Record database query performance
        Timer.builder("aml.database.query.time")
            .tag("query.type", queryType)
            .description("Time taken for database queries")
            .register(meterRegistry)
            .record(queryTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void recordApiCallMetrics(String apiEndpoint, long responseTimeMs, int statusCode) {
        // Record API call metrics
        Timer.builder("aml.api.response.time")
            .tag("endpoint", apiEndpoint)
            .tag("status.code", String.valueOf(statusCode))
            .description("API response time")
            .register(meterRegistry)
            .record(responseTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            
        // Record API call counts
        Counter.builder("aml.api.calls")
            .tag("endpoint", apiEndpoint)
            .tag("status.code", String.valueOf(statusCode))
            .description("API call counts")
            .register(meterRegistry)
            .increment();
    }

    // Business metrics
    public void recordRiskScoreDistribution(RiskScore riskScore) {
        Counter.builder("aml.risk.score.distribution")
            .tag("risk.score", riskScore.toString())
            .description("Distribution of risk scores")
            .register(meterRegistry)
            .increment();
    }

    public void recordAlertPriorityDistribution(String priorityLevel) {
        Counter.builder("aml.alert.priority.distribution")
            .tag("priority.level", priorityLevel)
            .description("Distribution of alert priority levels")
            .register(meterRegistry)
            .increment();
    }

    public void recordTransactionAmountRange(BigDecimal amount) {
        String range = getAmountRange(amount);
        Counter.builder("aml.transaction.amount.range")
            .tag("amount.range", range)
            .description("Distribution of transaction amounts by range")
            .register(meterRegistry)
            .increment();
    }

    private String getAmountRange(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("1000")) < 0) {
            return "0-1000";
        } else if (amount.compareTo(new BigDecimal("10000")) < 0) {
            return "1000-10000";
        } else if (amount.compareTo(new BigDecimal("100000")) < 0) {
            return "10000-100000";
        } else {
            return "100000+";
        }
    }

    // Performance monitoring
    public void recordSystemHealth() {
        // Record system health metrics as counters
        Counter.builder("aml.system.uptime.seconds")
            .description("System uptime in seconds")
            .register(meterRegistry)
            .increment((long) (System.currentTimeMillis() / 1000));
            
        // Record thread count
        Counter.builder("aml.system.thread.count")
            .description("Number of active threads")
            .register(meterRegistry)
            .increment(Thread.activeCount());
    }
} 