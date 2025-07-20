# aml-portal

The `aml-portal` module serves as the entry point for external user interaction with the AML Engine. It exposes core endpoints (e.g., transaction ingestion) and acts as the integration layer with the rule engine, ingestion logic, and response services.

---

## Key Features

- Public-facing REST API for ingesting transactions
- Delegates transaction data into the AML Engine pipeline
- Lightweight, decoupled architecture for input channels
- Modular separation from internal admin or engine logic

---

## Getting Started

### Prerequisites

- Java 17+
- Maven

---

## Module Structure

```bash
aml-portal/
├── controller/
│   └── PortalIngestionController.java     # REST controller for external ingestion
│
├── AMLPortalApplication.java              # Spring Boot main class
├── application.properties
