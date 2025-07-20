package com.leizo;
//Main class for tyesting logic before integration of springboot!!!!!!!!!!!!!!!!!!!
import com.leizo.loader.SanctionListLoader;
import com.leizo.model.*;
import com.leizo.repository.*;
import com.leizo.service.*;
import com.leizo.service.impl.*;

import java.math.BigDecimal;
import java.util.List;
public class Main {
/*
    public static void main(String[] args) {

        System.out.println(" AML Engine Started...");

        // STEP 1: Central Rule Repository
        RuleRepositoryImpl ruleRepository = new RuleRepositoryImpl();

        // STEP 2: Initialize services
        TransactionService transactionService = new TransactionServiceImpl();
        RuleEngine ruleEngine = new RuleEngineImpl(ruleRepository);
        FileImportService fileImportService = new FileImportServiceImpl();
        SanctionListLoader sanctionListLoader = new SanctionListLoader(fileImportService);
        SanctionsChecker sanctionsChecker = new SanctionsCheckerImpl(sanctionListLoader);
        AlertRepository alertRepository = new AlertRepositoryImpl();
        AlertService alertService = new AlertServiceImpl(alertRepository);
        ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();
        CaseManager caseManager = new CaseManagerImpl();
        LoggerService loggerService = new LoggerServiceImpl();
        AuthService authService = new AuthServiceImpl();
        RiskScoringService riskScoringService = new RiskScoringServiceImpl(sanctionListLoader);
        TransactionHistoryService transactionHistoryService = new TransactionHistoryServiceImpl();
        BehavioralPatternDetector behavioralPatternDetector = new BehavioralPatternDetectorImpl();
        AlertHistoryService alertHistoryService = new AlertHistoryServiceImpl();
        SanctionedEntityRepository sanctionedEntityRepository = new SanctionedEntityRepositoryImpl();

        // STEP 3: Construct AML engine with full FSS wiring
        AMLEngine amlEngine = new AMLEngine(
                transactionService,
                ruleEngine,
                sanctionsChecker,
                alertService,
                exchangeRateService,
                caseManager,
                loggerService,
                riskScoringService,
                behavioralPatternDetector,
                transactionHistoryService,
                alertHistoryService,
                ruleRepository,
                sanctionedEntityRepository
        );

        // Populate Entity Repo (from sanction list loader)
        sanctionListLoader.getConsolidatedList().forEach(sanctionedEntityRepository::saveEntity);

        // STEP 4: Simulate login
        String username = "analystUser";
        String password = "analyst123";
        User user = authService.authenticate(username, password);

        if (user == null) {
            System.out.println(" Authentication failed.");
            return;
        }
        System.out.println(" Login successful. Role: " + user.getRole());

        // STEP 5: Create a sample transaction
        Transaction txn = new Transaction(
                "senderProd",
                "receiverProd",
                new BigDecimal("25000"),
                "USD",
                "Russia",
                "01-01-1990"
        );

        // STEP 6: Ingest Transaction
        IngestionResult result = amlEngine.ingestTransaction(txn, user);

        System.out.println("\n Ingestion Status: " + result.getStatus());
        if (result.isAlertGenerated()) {
            System.out.println(" Alert Triggered: " + result.getAlertId());
        } else {
            System.out.println(" No alert raised.");
        }

        System.out.println("\n=== DB VALIDATION TEST ===");

        // Transaction FSS Demo
        System.out.println("\n=== Transaction FSS ===");
        List<Transaction> filteredTx = amlEngine.filterTransactions("senderProd", "Russia",
                new BigDecimal("1000"), new BigDecimal("50000"));
        System.out.println("Filtered Transactions: " + filteredTx.size());

        Transaction[] sortedTxByAmount = amlEngine.sortTransactionsByAmount(false);
        Transaction[] sortedTxByRisk = amlEngine.sortTransactionsByRiskScore(true);

        System.out.println("Sorted by Amount (ASC):");
        for (Transaction t : sortedTxByAmount) {
            System.out.println(t.getSender() + " | Amount: " + t.getAmount());
        }

        System.out.println("Sorted by Risk (DESC):");
        for (Transaction t : sortedTxByRisk) {
            System.out.println(t.getSender() + " | Risk: " + t.getRiskScore());
        }

        // Alerts FSS Demo
        System.out.println("\n=== Alert FSS ===");
        List<Alert> filteredAlerts = amlEngine.filterAlerts("senderProd", "High");
        System.out.println("Filtered Alerts: " + filteredAlerts.size());

        List<Alert> searchedAlerts = amlEngine.searchAlertsByReason("violation");
        System.out.println("Searched Alerts by reason 'violation': " + searchedAlerts.size());

        Alert[] sortedAlerts = amlEngine.sortAlertsByPriority(true);
        System.out.println("Sorted Alerts by Priority (DESC):");
        for (Alert a : sortedAlerts) {
            System.out.println(a.getAlertId() + " | Priority: " + a.getPriorityLevel());
        }

        // Entities FSS Demo
        System.out.println("\n=== Sanctioned Entity FSS ===");
        List<SanctionedEntity> entitiesByCountry = amlEngine.filterEntitiesByCountry("Russia");
        System.out.println("Entities from Russia: " + entitiesByCountry.size());

        List<SanctionedEntity> searchedEntities = amlEngine.searchEntitiesByName("Ali");
        System.out.println("Entities with name 'Ali': " + searchedEntities.size());

        SanctionedEntity[] sortedEntities = amlEngine.sortEntitiesByName(false);
        System.out.println("Entities Sorted by Name (ASC):");
        for (SanctionedEntity e : sortedEntities) {
            System.out.println(e);
        }

    }

 */
}
