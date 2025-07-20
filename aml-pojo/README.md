# aml-pojo

The `aml-pojo` module defines the core domain models used across the AML Engine. These Plain Old Java Objects (POJOs) represent the essential entities used for data transfer, rule matching, ingestion processing, and risk analysis.

---

## Key Features

- Centralized domain model definitions
- Clear separation of model concerns (`enums`, `model`, `pojo`)
- Reusable entities across all modules (admin, engine, portal)
- Simplified design for rule evaluation and data transport

---

## Getting Started

### Prerequisites

- Java 17+
- Maven

---

## Module Structure

```bash
aml-pojo/
├── enums/
│   ├── IngestionStatus.java
│   ├── Risk.java
│   ├── RuleSensitivity.java
│   └── TransactionKey.java               # Enum constants for system logic
│
├── model/
│   ├── Alert.java
│   ├── IngestionResult.java
│   ├── Rule.java
│   ├── SanctionedEntity.java
│   ├── Transaction.java
│   └── User.java                        # Core domain objects
