package com.leizo.admin.performance;

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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionPerformanceTest {

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

    private Rule highAmountRule;
    private List<Transaction> largeDataset;

    @BeforeEach
    void setUp() {
        // Clear test data
        alertRepository.deleteAll();
        transactionRepository.deleteAll();

        // Create test rule
        highAmountRule = new Rule("High Amount Rule", RuleSensitivity.HIGH,
            (txn, amount) -> amount.compareTo(new BigDecimal("10000")) > 0,
            Set.of("amount", "high"));

        // Mock dependencies for performance
        when(riskScoringService.assessRisk(any(Transaction.class))).thenReturn(RiskScore.LOW);
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any())).thenReturn(false);
        when(ruleEngine.getActiveRules()).thenReturn(Arrays.asList(highAmountRule));

        // Generate large dataset
        largeDataset = generateLargeDataset(1000);
    }

    @Test
    void testBulkTransactionInsert_Performance() {
        // Given: Large dataset of transactions
        int datasetSize = 1000;
        List<Transaction> transactions = generateLargeDataset(datasetSize);

        // When: Bulk insert with timing
        long startTime = System.currentTimeMillis();
        
        List<Transaction> savedTransactions = transactionRepository.saveAll(transactions);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then: Verify performance metrics
        assertEquals(datasetSize, savedTransactions.size());
        assertEquals(datasetSize, transactionRepository.count());
        
        // Performance assertions (adjust thresholds based on your system)
        assertTrue(duration < 5000, "Bulk insert took too long: " + duration + "ms");
        
        // Calculate throughput
        double throughput = (double) datasetSize / (duration / 1000.0);
        System.out.println("Bulk Insert Performance:");
        System.out.println("Dataset Size: " + datasetSize);
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " transactions/second");
        
        assertTrue(throughput > 100, "Throughput too low: " + throughput + " txn/sec");
    }

    @Test
    void testConcurrentTransactionProcessing_Performance() throws InterruptedException {
        // Given: Large dataset and thread pool
        int datasetSize = 500;
        int threadCount = 4;
        List<Transaction> transactions = generateLargeDataset(datasetSize);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // When: Process transactions concurrently
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<Void>> futures = transactions.stream()
            .map(txn -> CompletableFuture.runAsync(() -> {
                transactionRepository.save(txn);
            }, executor))
            .collect(Collectors.toList());

        // Wait for all tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then: Verify concurrent processing performance
        assertEquals(datasetSize, transactionRepository.count());
        
        // Performance assertions
        assertTrue(duration < 10000, "Concurrent processing took too long: " + duration + "ms");
        
        double throughput = (double) datasetSize / (duration / 1000.0);
        System.out.println("Concurrent Processing Performance:");
        System.out.println("Dataset Size: " + datasetSize);
        System.out.println("Thread Count: " + threadCount);
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " transactions/second");
        
        assertTrue(throughput > 50, "Concurrent throughput too low: " + throughput + " txn/sec");
    }

    @Test
    void testLargeDatasetQuery_Performance() {
        // Given: Large dataset in database
        int datasetSize = 1000;
        List<Transaction> transactions = generateLargeDataset(datasetSize);
        transactionRepository.saveAll(transactions);

        // When: Query large dataset with timing
        long startTime = System.currentTimeMillis();
        
        List<Transaction> allTransactions = transactionRepository.findAll();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then: Verify query performance
        assertEquals(datasetSize, allTransactions.size());
        
        // Performance assertions
        assertTrue(duration < 2000, "Large dataset query took too long: " + duration + "ms");
        
        double throughput = (double) datasetSize / (duration / 1000.0);
        System.out.println("Large Dataset Query Performance:");
        System.out.println("Dataset Size: " + datasetSize);
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " transactions/second");
        
        assertTrue(throughput > 200, "Query throughput too low: " + throughput + " txn/sec");
    }

    @Test
    void testFilteredQuery_Performance() {
        // Given: Large dataset with mixed risk scores
        int datasetSize = 1000;
        List<Transaction> transactions = generateLargeDatasetWithMixedRisk(datasetSize);
        transactionRepository.saveAll(transactions);

        // When: Query with filters
        long startTime = System.currentTimeMillis();
        
        List<Transaction> highRiskTransactions = transactionRepository.findAll().stream()
            .filter(t -> RiskScore.HIGH.equals(t.getRiskScore()))
            .collect(Collectors.toList());
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then: Verify filtered query performance
        assertTrue(highRiskTransactions.size() > 0);
        assertTrue(duration < 3000, "Filtered query took too long: " + duration + "ms");
        
        System.out.println("Filtered Query Performance:");
        System.out.println("Total Dataset Size: " + datasetSize);
        System.out.println("Filtered Result Size: " + highRiskTransactions.size());
        System.out.println("Duration: " + duration + "ms");
    }

    @Test
    void testAlertGeneration_Performance() {
        // Given: Large dataset with high-risk transactions
        int datasetSize = 500;
        List<Transaction> transactions = generateHighRiskDataset(datasetSize);
        
        // Mock sanctions checker to return true for some transactions
        when(sanctionsChecker.isSanctionedEntity(anyString(), anyString(), any(), any()))
            .thenAnswer(invocation -> {
                String sender = invocation.getArgument(0);
                return sender.contains("Sanctioned");
            });

        // When: Process transactions and generate alerts
        long startTime = System.currentTimeMillis();
        
        for (Transaction txn : transactions) {
            transactionRepository.save(txn);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then: Verify alert generation performance
        List<Alert> alerts = alertRepository.findAll();
        assertTrue(alerts.size() > 0);
        
        assertTrue(duration < 8000, "Alert generation took too long: " + duration + "ms");
        
        double throughput = (double) datasetSize / (duration / 1000.0);
        System.out.println("Alert Generation Performance:");
        System.out.println("Dataset Size: " + datasetSize);
        System.out.println("Alerts Generated: " + alerts.size());
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " transactions/second");
        
        assertTrue(throughput > 30, "Alert generation throughput too low: " + throughput + " txn/sec");
    }

    @Test
    void testMemoryUsage_Performance() {
        // Given: Very large dataset
        int datasetSize = 5000;
        List<Transaction> transactions = generateLargeDataset(datasetSize);

        // When: Monitor memory usage during processing
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        long startTime = System.currentTimeMillis();
        
        List<Transaction> savedTransactions = transactionRepository.saveAll(transactions);
        
        long endTime = System.currentTimeMillis();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;

        // Then: Verify memory efficiency
        assertEquals(datasetSize, savedTransactions.size());
        
        // Memory usage assertions (adjust based on your system)
        long memoryPerTransaction = memoryUsed / datasetSize;
        System.out.println("Memory Usage Performance:");
        System.out.println("Dataset Size: " + datasetSize);
        System.out.println("Total Memory Used: " + (memoryUsed / 1024) + " KB");
        System.out.println("Memory Per Transaction: " + memoryPerTransaction + " bytes");
        
        assertTrue(memoryPerTransaction < 1000, "Memory usage per transaction too high: " + memoryPerTransaction + " bytes");
    }

    @Test
    void testBatchProcessing_Performance() {
        // Given: Large dataset for batch processing
        int datasetSize = 2000;
        int batchSize = 100;
        List<Transaction> transactions = generateLargeDataset(datasetSize);

        // When: Process in batches
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < transactions.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, transactions.size());
            List<Transaction> batch = transactions.subList(i, endIndex);
            transactionRepository.saveAll(batch);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then: Verify batch processing performance
        assertEquals(datasetSize, transactionRepository.count());
        
        assertTrue(duration < 10000, "Batch processing took too long: " + duration + "ms");
        
        double throughput = (double) datasetSize / (duration / 1000.0);
        System.out.println("Batch Processing Performance:");
        System.out.println("Dataset Size: " + datasetSize);
        System.out.println("Batch Size: " + batchSize);
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " transactions/second");
        
        assertTrue(throughput > 150, "Batch processing throughput too low: " + throughput + " txn/sec");
    }

    private List<Transaction> generateLargeDataset(int size) {
        return IntStream.range(0, size)
            .mapToObj(i -> {
                Transaction txn = new Transaction(
                    "Sender" + i,
                    "Receiver" + i,
                    new BigDecimal(1000 + (i % 5000)),
                    "USD",
                    "USA",
                    "2025-01-01"
                );
                txn.setRiskScore(RiskScore.LOW);
                return txn;
            })
            .collect(Collectors.toList());
    }

    private List<Transaction> generateLargeDatasetWithMixedRisk(int size) {
        return IntStream.range(0, size)
            .mapToObj(i -> {
                Transaction txn = new Transaction(
                    "Sender" + i,
                    "Receiver" + i,
                    new BigDecimal(1000 + (i % 5000)),
                    "USD",
                    i % 3 == 0 ? "Iran" : "USA",
                    "2025-01-01"
                );
                txn.setRiskScore(i % 3 == 0 ? RiskScore.HIGH : RiskScore.LOW);
                return txn;
            })
            .collect(Collectors.toList());
    }

    private List<Transaction> generateHighRiskDataset(int size) {
        return IntStream.range(0, size)
            .mapToObj(i -> {
                String sender = i % 10 == 0 ? "Sanctioned" + i : "Normal" + i;
                Transaction txn = new Transaction(
                    sender,
                    "Receiver" + i,
                    new BigDecimal(15000 + (i % 10000)),
                    "USD",
                    i % 5 == 0 ? "Iran" : "USA",
                    "2025-01-01"
                );
                txn.setRiskScore(RiskScore.HIGH);
                return txn;
            })
            .collect(Collectors.toList());
    }
} 