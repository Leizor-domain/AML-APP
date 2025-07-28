# AML POJO Module

Core domain models and data transfer objects (DTOs) for the Anti-Money Laundering (AML) Engine system. This module defines the essential entities used for transaction processing, rule evaluation, alert management, and data persistence.

## 🚀 Overview

The `aml-pojo` module contains the foundational data models that represent the core business entities in the AML system. These Plain Old Java Objects (POJOs) are used across all modules for data transfer, rule matching, ingestion processing, and risk analysis.

## 🏗️ Architecture

### Core Components

#### **Domain Models**
- **Transaction entities** for financial transaction data
- **Alert entities** for suspicious activity notifications
- **Rule entities** for AML rule definitions
- **User entities** for system user management

#### **Enums & Constants**
- **Risk scoring** enums and thresholds
- **Rule sensitivity** levels and types
- **Ingestion status** tracking
- **System-wide** constants

#### **Data Transfer Objects**
- **Transaction DTOs** for API communication
- **Alert DTOs** for alert management
- **User DTOs** for authentication and authorization

## 📁 Module Structure

```
aml-pojo/
├── src/main/java/com/leizo/
│   ├── enums/                           # System enums
│   │   ├── IngestionStatus.java         # Transaction ingestion status
│   │   ├── RiskScore.java               # Risk scoring levels
│   │   └── RuleSensitivity.java         # Rule sensitivity levels
│   ├── model/                           # Core domain models
│   │   ├── Alert.java                   # Alert entity
│   │   ├── IngestionResult.java         # Ingestion result model
│   │   ├── Rule.java                    # Rule entity
│   │   ├── SanctionedEntity.java        # Sanctions entity
│   │   ├── Transaction.java             # Transaction entity
│   │   └── User.java                    # User entity
│   └── pojo/                            # Plain Old Java Objects
│       └── entity/                      # JPA entities
│           ├── Alert.java               # Alert JPA entity
│           ├── AuditLogEntry.java       # Audit log entity
│           ├── Rule.java                # Rule JPA entity
│           ├── SanctionedEntity.java    # Sanctions JPA entity
│           └── Transaction.java         # Transaction JPA entity
└── src/test/java/                       # Test classes
```

## 🔧 Key Features

### Transaction Management
- **Comprehensive transaction model** with all required fields
- **Metadata support** for flexible data storage
- **Validation annotations** for data integrity
- **Audit trail** capabilities

### Alert System
- **Multi-type alert support** (sanctions, high value, behavioral)
- **Priority scoring** and risk assessment
- **Status tracking** for alert lifecycle
- **Entity matching** for sanctions screening

### Rule Engine
- **Flexible rule definitions** with JSON configuration
- **Multiple rule types** (high value, sanctions, geographic)
- **Sensitivity levels** for rule prioritization
- **Rule chaining** and evaluation logic

### User Management
- **Role-based access control** with enum definitions
- **Secure user entity** with password hashing
- **Audit fields** for compliance tracking
- **Status management** for account control

## 🔌 Entity Definitions

### Transaction Entity
```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String transactionId;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, length = 3)
    private String currency;
    
    @Column(nullable = false)
    private String senderName;
    
    @Column(nullable = false)
    private String receiverName;
    
    @Column(nullable = false)
    private String senderAccount;
    
    @Column(nullable = false)
    private String receiverAccount;
    
    @Column(nullable = false)
    private String country;
    
    @Column
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "transaction_metadata")
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata;
    
    // ... getters, setters, and methods
}
```

### Alert Entity
```java
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "alert_id", nullable = false, unique = true)
    private String alertId;
    
    @Column(name = "transaction_id")
    private Integer transactionId;
    
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "alert_type", length = 50)
    private String alertType;
    
    @Column(name = "priority_level", length = 20)
    private String priorityLevel;
    
    private String matchedEntityName;
    private String matchedList;
    private String matchReason;
    
    @Column(name = "priority_score")
    private Integer priorityScore;
    
    // ... getters, setters, and methods
}
```

### Rule Entity
```java
@Entity
@Table(name = "rules")
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private RuleSensitivity sensitivity;
    
    @ElementCollection
    private List<String> tags;
    
    @Column(nullable = false)
    private String type;
    
    @Column(columnDefinition = "TEXT")
    private String condition;
    
    // ... getters, setters, and methods
}
```

## 📊 Enums & Constants

### Risk Score Enum
```java
public enum RiskScore {
    LOW(1, "Low Risk"),
    MEDIUM(2, "Medium Risk"),
    HIGH(3, "High Risk"),
    CRITICAL(4, "Critical Risk");
    
    private final int weight;
    private final String description;
    
    // ... constructor and methods
}
```

### Rule Sensitivity Enum
```java
public enum RuleSensitivity {
    LOW(1, "Low Sensitivity"),
    MEDIUM(2, "Medium Sensitivity"),
    HIGH(3, "High Sensitivity");
    
    private final int weight;
    private final String description;
    
    // ... constructor and methods
}
```

### Ingestion Status Enum
```java
public enum IngestionStatus {
    PENDING("Pending Processing"),
    PROCESSING("Currently Processing"),
    SUCCESS("Successfully Processed"),
    FAILED("Processing Failed"),
    ALERT_GENERATED("Alert Generated");
    
    private final String description;
    
    // ... constructor and methods
}
```

## 🔒 Data Validation

### Bean Validation Annotations
```java
@Entity
public class Transaction {
    @NotNull(message = "Transaction ID is required")
    @Size(min = 1, max = 100, message = "Transaction ID must be between 1 and 100 characters")
    private String transactionId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    private String currency;
    
    @NotNull(message = "Sender name is required")
    @Size(min = 1, max = 255, message = "Sender name must be between 1 and 255 characters")
    private String senderName;
    
    // ... other validation annotations
}
```

### Custom Validation
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCurrencyValidator.class)
public @interface ValidCurrency {
    String message() default "Invalid currency code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TransactionTest

# Run with coverage
mvn test jacoco:report
```

### Integration Tests
```bash
# Test entity persistence
mvn test -Dtest="*EntityTest"

# Test validation logic
mvn test -Dtest="*ValidationTest"
```

## 📊 Configuration

### JPA Configuration
```properties
# Entity scanning
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Validation
spring.jpa.properties.hibernate.validator.apply_to_ddl=false
spring.jpa.properties.hibernate.validator.autoregister_listeners=false
```

### Bean Validation
```properties
# Enable validation
spring.validation.enabled=true

# Custom validation messages
validation.messages.bundle=ValidationMessages
```

## 🔧 Development

### Adding New Entities
1. **Create entity class** with proper annotations
2. **Add validation** using Bean Validation
3. **Create repository** interface if needed
4. **Add unit tests** for entity logic
5. **Update documentation** with usage examples

### Best Practices
- **Use meaningful field names** that reflect business concepts
- **Include comprehensive validation** for data integrity
- **Add proper documentation** for complex fields
- **Follow JPA naming conventions** for database mapping
- **Use appropriate data types** for each field

## 🚀 Deployment

### Build Process
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package for deployment
mvn package
```

### Integration with Other Modules
```bash
# Install to local repository
mvn clean install

# Use in other modules
mvn dependency:resolve
```

## 🔧 Troubleshooting

### Common Issues

#### JPA Mapping Problems
```bash
# Check entity annotations
# Verify table names and column mappings
# Ensure proper relationship definitions

# Test entity persistence
mvn test -Dtest=EntityPersistenceTest
```

#### Validation Issues
```bash
# Check validation annotations
# Verify custom validators
# Test validation logic

# Run validation tests
mvn test -Dtest=ValidationTest
```

#### Data Type Problems
```bash
# Verify field types match database schema
# Check precision and scale for decimal fields
# Ensure proper enum handling
```

## 📚 API Reference

### Transaction Methods
```java
public class Transaction {
    // Get transaction ID
    public String getTransactionId();
    
    // Get transaction amount
    public BigDecimal getAmount();
    
    // Get sender information
    public String getSenderName();
    public String getSenderAccount();
    
    // Get receiver information
    public String getReceiverName();
    public String getReceiverAccount();
    
    // Get metadata
    public Map<String, String> getMetadata();
    
    // Add metadata
    public void addMetadata(String key, String value);
}
```

### Alert Methods
```java
public class Alert {
    // Get alert information
    public String getAlertId();
    public String getReason();
    public String getAlertType();
    
    // Get priority information
    public String getPriorityLevel();
    public Integer getPriorityScore();
    
    // Update priority
    public void updatePriorityLevel();
}
```

### Rule Methods
```java
public class Rule {
    // Get rule information
    public String getDescription();
    public RuleSensitivity getSensitivity();
    public String getType();
    
    // Check if rule applies
    public boolean appliesTo(Transaction transaction, BigDecimal amount);
    
    // Get rule weight
    public int getWeight();
}
```

## 📈 Performance

### Optimization Tips
- **Use appropriate indexes** for frequently queried fields
- **Optimize entity relationships** to avoid N+1 queries
- **Use lazy loading** for large collections
- **Implement caching** for frequently accessed entities

### Monitoring
- **Entity creation** and update performance
- **Validation overhead** monitoring
- **Database query** optimization
- **Memory usage** for large datasets

## 🔒 Security Considerations

### Data Protection
- **Encrypt sensitive fields** using JPA converters
- **Validate all inputs** to prevent injection attacks
- **Use secure data types** for sensitive information
- **Implement audit trails** for compliance

### Access Control
- **Use proper annotations** for field-level security
- **Implement row-level security** where appropriate
- **Validate entity ownership** before operations
- **Log security-relevant** entity changes

---

**AML POJO Module - Core domain models for transaction processing and alert management**
