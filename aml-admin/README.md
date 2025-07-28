# AML Admin Module

The core Spring Boot application for the Anti-Money Laundering (AML) Engine system. This module handles authentication, transaction processing, alert management, and provides the main REST API endpoints.

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 13+**

### Installation & Running
```bash
# Navigate to module
cd aml-admin

# Install dependencies
mvn clean install

# Run application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🏗️ Architecture

### Core Components

#### **Authentication & Security**
- **JWT-based authentication** with role-based access control
- **Spring Security** configuration with stateless sessions
- **BCrypt password hashing** for secure user management
- **CORS configuration** for frontend integration

#### **Transaction Processing**
- **CSV/JSON file ingestion** with validation and error handling
- **Real-time transaction evaluation** against AML rules
- **Risk scoring engine** with configurable algorithms
- **Sanctions screening** (OFAC SDN + Local lists)

#### **Alert Management**
- **Comprehensive alert generation** with deduplication
- **Alert lifecycle management** (creation, review, resolution)
- **Mock alert data service** for testing and development
- **Alert statistics** and reporting capabilities

## 📁 Module Structure

```
aml-admin/
├── src/main/java/com/leizo/admin/
│   ├── AMLAdminApplication.java          # Main application entry point
│   ├── config/                           # Configuration classes
│   │   ├── CorsConfig.java              # CORS configuration
│   │   └── SecurityConfig.java          # Spring Security setup
│   ├── controller/                       # REST API controllers
│   │   ├── AlertController.java         # Alert management endpoints
│   │   ├── AMLAdminController.java      # Admin-specific endpoints
│   │   ├── TransactionController.java   # Transaction processing
│   │   └── UserController.java          # User management
│   ├── dto/                             # Data Transfer Objects
│   │   ├── TransactionCsvParser.java    # CSV parsing utilities
│   │   ├── TransactionDTO.java          # Transaction data transfer
│   │   └── TransactionMapper.java       # Entity mapping utilities
│   ├── entity/                          # JPA entities
│   │   ├── Alert.java                   # Alert entity
│   │   ├── Rule.java                    # Rule entity
│   │   ├── SanctionedEntity.java        # Sanctions entity
│   │   └── Transaction.java             # Transaction entity
│   ├── repository/                      # Data access layer
│   │   ├── AlertRepository.java         # Alert data operations
│   │   ├── AuditLogRepository.java      # Audit logging
│   │   └── TransactionRepository.java   # Transaction data operations
│   ├── service/                         # Business logic services
│   │   ├── AuditLogService.java         # Audit logging service
│   │   ├── MockAlertDataService.java    # Mock data generation
│   │   └── impl/                        # Service implementations
│   └── util/                            # Utility classes
│       ├── AlertUtils.java              # Alert processing utilities
│       ├── RuleUtils.java               # Rule evaluation utilities
│       └── TransactionUtils.java        # Transaction processing utilities
├── src/main/resources/
│   ├── application.properties           # Main configuration
│   ├── application-prod.properties      # Production settings
│   ├── application-test.properties      # Test environment
│   └── data/                            # Static data files
└── src/test/java/                       # Test classes
```

## 🔌 API Endpoints

### Authentication
- `POST /auth/login` - User authentication with JWT token
- `POST /auth/register` - User registration (admin only)

### Transaction Management
- `POST /ingest/file` - Upload and process transaction files (CSV/JSON)
- `GET /ingest/transactions` - Retrieve transaction history with pagination
- `GET /ingest/transactions/{id}` - Get specific transaction details

### Alert Management
- `GET /alerts` - List all alerts with filtering and pagination
- `GET /alerts/{id}` - Get specific alert details
- `PATCH /alerts/{id}/status` - Update alert status
- `PATCH /alerts/{id}/dismiss` - Dismiss alert with reason
- `PATCH /alerts/{id}/false-positive` - Mark alert as false positive
- `POST /alerts/populate-mock` - Generate test alert data
- `DELETE /alerts/clear-all` - Clear all alerts from database
- `GET /alerts/count` - Get current alert count

### User Management
- `GET /users` - List all users with pagination
- `POST /users/create` - Create new user (admin only)
- `PATCH /users/{id}/status` - Enable/disable user account
- `GET /users/role-distribution` - Get user role statistics

### System Health
- `GET /actuator/health` - Application health check
- `GET /admin/db-health` - Database health and statistics

## 🗄️ Database Configuration

### PostgreSQL Setup
```sql
-- Create database
CREATE DATABASE aml_database;

-- Create user
CREATE USER aml_user WITH PASSWORD 'your_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE aml_database TO aml_user;
```

### Environment Variables
```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aml_database
DB_USERNAME=aml_user
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Server Configuration
SERVER_PORT=8080
```

## 🔧 Configuration

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# Logging
logging.level.com.leizo=INFO
logging.level.org.springframework.security=DEBUG
```

### Profiles
- **default** - Development environment
- **test** - Testing with H2 in-memory database
- **prod** - Production environment with PostgreSQL

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TransactionControllerTest

# Run with coverage
mvn test jacoco:report
```

### Integration Tests
```bash
# Run integration tests
mvn test -Dtest="*IntegrationTest"

# Test with specific profile
mvn test -Dspring.profiles.active=test
```

### API Testing
```bash
# Test transaction ingestion
curl -X POST -F "file=@test_transactions.csv" http://localhost:8080/ingest/file

# Test alert generation
curl -X POST http://localhost:8080/alerts/populate-mock

# Test authentication
curl -X POST -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' \
  http://localhost:8080/auth/login
```

## 🔒 Security Features

### Authentication
- **JWT tokens** with configurable expiration
- **BCrypt password hashing** with salt rounds
- **Role-based access control** (ADMIN, ANALYST, SUPERVISOR, VIEWER)
- **Session management** with automatic token refresh

### Authorization
- **Endpoint-level security** with @PreAuthorize annotations
- **Method-level security** for service layer protection
- **Resource-based access control** for data protection

### Data Protection
- **Input validation** and sanitization
- **SQL injection prevention** with parameterized queries
- **XSS protection** with proper content encoding
- **CSRF protection** for state-changing operations

## 📊 Monitoring & Logging

### Health Checks
- **Application health** via Spring Boot Actuator
- **Database connectivity** monitoring
- **External service** health checks (OFAC API)

### Logging
- **Structured logging** with correlation IDs
- **Audit trails** for compliance requirements
- **Performance monitoring** with response times
- **Error tracking** with detailed stack traces

### Metrics
- **Transaction processing** rate and success metrics
- **Alert generation** statistics
- **User activity** and authentication metrics
- **System performance** indicators

## 🚀 Deployment

### Local Development
```bash
# Run with development profile
mvn spring-boot:run -Dspring.profiles.active=default

# Run with test profile (H2 database)
mvn spring-boot:run -Dspring.profiles.active=test
```

### Production Deployment
```bash
# Build JAR file
mvn clean package -DskipTests

# Run with production profile
java -jar target/aml-admin-1.0.0.jar --spring.profiles.active=prod
```

### Docker Deployment
```dockerfile
# Use provided Dockerfile.admin
docker build -f Dockerfile.admin -t aml-admin .
docker run -p 8080:8080 aml-admin
```

## 🔧 Troubleshooting

### Common Issues

#### Database Connection
```bash
# Check PostgreSQL service
sudo systemctl status postgresql

# Test connection
psql -h localhost -U aml_user -d aml_database
```

#### JWT Issues
```bash
# Verify JWT secret is set
echo $JWT_SECRET

# Check token expiration
curl -H "Authorization: Bearer <token>" http://localhost:8080/alerts
```

#### Memory Issues
```bash
# Increase heap size
java -Xmx2g -jar target/aml-admin-1.0.0.jar
```

### Log Analysis
```bash
# View application logs
tail -f logs/application.log

# Search for errors
grep "ERROR" logs/application.log

# Monitor performance
grep "response time" logs/application.log
```

## 📚 Additional Resources

- [Alert Generation Guide](../ALERT_GENERATION_DETAILED_GUIDE.md)
- [High-Risk Countries Documentation](../HIGH_RISK_COUNTRIES_DOCUMENTATION.md)
- [Local Sanctions List Documentation](../LOCAL_SANCTIONS_LIST_DOCUMENTATION.md)
- [OFAC API Integration Guide](../OFAC_API_OFFLINE_ANALYSIS.md)

---

**AML Admin Module - Core backend for transaction processing and alert management**
