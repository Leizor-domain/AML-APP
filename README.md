# aml-engine

The `aml-engine` module houses the core logic of the AML system. It evaluates transactions using rule-based checks, applies risk scoring, checks for sanctions, and generates alerts when suspicious activity is detected.

---

## Key Features

- Full transaction evaluation pipeline
- Rule-based AML checks
- Integrated risk scoring engine
- Sanctions list screening
- Alert generation and deduplication
- Modular, testable services using Spring Boot

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL (ensure the DB is running and configured)

---

## Module Structure

```bash
aml-engine/
├── config/                             # Configuration components
│   └── EngineConfig.java
│
├── controller/                         # REST APIs for rule evaluation, alerts
│   └── TransactionController.java
│
├── enums/
│   ├── IngestionStatus.java
│   ├── RiskScore.java
│   └── RuleSensitivity.java
│
├── model/                              # Core domain models
│   ├── Alert.java
│   ├── IngestionResult.java
│   ├── Rule.java
│   ├── SanctionedEntity.java
│   ├── Transaction.java
│   └── User.java
│
├── repository/                         # Repositories for DB persistence
│   └── AlertRepository.java
│
├── service/                            # Core business services
│   ├── AlertService.java
│   ├── AlertServiceImpl.java
│   ├── RuleEngineService.java
│   ├── RuleEngineServiceImpl.java
│   ├── RiskScoringService.java
│   ├── RiskScoringServiceImpl.java
│   ├── SanctionCheckService.java
│   ├── SanctionCheckServiceImpl.java
│   └── TransactionService.java
│   └── TransactionServiceImpl.java
│
├── AMLApp.java                         # Main Spring Boot entry point
├── application.properties              # Environment config
