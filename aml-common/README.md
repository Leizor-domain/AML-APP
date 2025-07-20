# aml-common

The `aml-common` module contains core shared resources across the AML Engine system. It includes custom enums, exceptions, and utility classes used by multiple modules such as `aml-admin`, `aml-portal`, and others.

---

## Key Features

- Shared enums and error codes
- Centralized exception handling
- Common utility classes (e.g., validation, formatting)
- Modular design for reuse across other modules

---

## Getting Started

### Prerequisites

- Java 17+
- Maven

---

## Module Structure

```bash
aml-common/
├── enums/                              # System-wide role enums
│   └── UserRole.java
│
├── exception/                          # Global exception definitions
│   ├── AuthenticationException.java
│   ├── DuplicateUserException.java
│   ├── InvalidRequestException.java
│   └── ResourceNotFoundException.java
│
├── security/                           # JWT logic and helpers
│   └── JwtUtil.java
│
└── utils/                              # Common utilities
    └── ValidationUtils.java
