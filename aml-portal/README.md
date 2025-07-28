# AML Portal Module

Portal-specific Spring Boot application for the Anti-Money Laundering (AML) Engine system. This module provides specialized functionality for portal users, including transaction viewing, reporting, and portal-specific operations.

## 🚀 Overview

The `aml-portal` module serves as a dedicated portal application for external users and partners who need access to AML system data and reporting capabilities. It provides a secure, role-based interface for transaction monitoring and compliance reporting.

## 🏗️ Architecture

### Core Components

#### **Portal Services**
- **Transaction viewing** and reporting capabilities
- **Portal-specific** user management
- **External API** integration for partner systems
- **Reporting and analytics** for portal users

#### **Security & Access Control**
- **Portal-specific authentication** and authorization
- **Partner access** management and controls
- **API rate limiting** and security measures
- **Audit logging** for portal activities

#### **Integration Services**
- **External system** integration capabilities
- **Data export** and reporting services
- **Real-time** transaction monitoring
- **Alert notification** services

## 📁 Module Structure

```
aml-portal/
├── src/main/java/com/leizo/portal/
│   ├── AMLPortalApplication.java          # Main application entry point
│   ├── config/                           # Portal configuration
│   │   ├── CorsConfig.java              # CORS configuration for portal
│   │   └── SecurityConfig.java          # Portal security setup
│   ├── controller/                       # Portal REST controllers
│   │   ├── PortalStatusController.java   # Portal status endpoints
│   │   └── TransactionController.java    # Portal transaction endpoints
│   ├── exception/                        # Portal exception handling
│   │   └── GlobalExceptionHandler.java   # Global exception handler
│   ├── service/                          # Portal business services
│   │   ├── PortalTransactionService.java # Portal transaction logic
│   │   └── PortalReportingService.java   # Portal reporting logic
│   └── util/                             # Portal utilities
│       └── PortalUtils.java              # Portal helper functions
├── src/main/resources/
│   ├── application.properties            # Portal configuration
│   └── data/                             # Portal static data
└── src/test/java/                        # Portal test classes
```

## 🔧 Key Features

### Portal Access Management
- **Partner authentication** with secure credentials
- **Role-based access** for different portal user types
- **Session management** with configurable timeouts
- **Access logging** for compliance requirements

### Transaction Monitoring
- **Real-time transaction** viewing capabilities
- **Filtered access** based on partner permissions
- **Transaction history** with search and filtering
- **Export capabilities** for reporting needs

### Reporting & Analytics
- **Custom reports** for portal users
- **Data analytics** and visualization
- **Scheduled reporting** capabilities
- **Compliance reporting** templates

### Integration Capabilities
- **REST API** for external system integration
- **Webhook support** for real-time notifications
- **Data export** in multiple formats (CSV, JSON, XML)
- **Partner-specific** customization options

## 🔌 API Endpoints

### Portal Status
- `GET /portal/status` - Portal health and status information
- `GET /portal/version` - Portal version and build information
- `GET /portal/config` - Portal configuration details

### Portal Transactions
- `GET /portal/transactions` - View transactions (filtered by partner access)
- `GET /portal/transactions/{id}` - Get specific transaction details
- `GET /portal/transactions/export` - Export transaction data

### Portal Reporting
- `GET /portal/reports` - Available reports for portal users
- `POST /portal/reports/generate` - Generate custom reports
- `GET /portal/reports/{id}` - Get report details and data
- `GET /portal/reports/{id}/download` - Download report files

### Portal Alerts
- `GET /portal/alerts` - View alerts relevant to portal users
- `GET /portal/alerts/{id}` - Get specific alert details
- `POST /portal/alerts/{id}/acknowledge` - Acknowledge alert receipt

## 🗄️ Configuration

### Application Properties
```properties
# Portal Configuration
portal.name=AML Portal
portal.version=1.0.0
portal.description=Anti-Money Laundering Portal

# Server Configuration
server.port=8081
server.servlet.context-path=/portal

# Security Configuration
portal.security.enabled=true
portal.security.jwt.secret=${JWT_SECRET}
portal.security.jwt.expiration=86400000

# Partner Configuration
portal.partners.enabled=true
portal.partners.rate-limit=1000
portal.partners.session-timeout=3600

# Integration Configuration
portal.integration.webhooks.enabled=true
portal.integration.api.rate-limit=500
portal.integration.export.max-size=10000
```

### Environment Variables
```bash
# Portal Configuration
PORTAL_NAME=AML Portal
PORTAL_VERSION=1.0.0
PORTAL_DESCRIPTION=Anti-Money Laundering Portal

# Security Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aml_database
DB_USERNAME=aml_user
DB_PASSWORD=your_password

# Integration Configuration
WEBHOOK_URL=https://partner.example.com/webhook
API_RATE_LIMIT=500
EXPORT_MAX_SIZE=10000
```

## 🔒 Security Features

### Portal Authentication
- **Partner-specific** JWT tokens
- **Role-based access** control for portal features
- **Session management** with automatic timeout
- **Secure credential** storage and validation

### Access Control
- **Partner isolation** to prevent data leakage
- **API rate limiting** to prevent abuse
- **IP whitelisting** for enhanced security
- **Audit logging** for all portal activities

### Data Protection
- **Encrypted data transmission** (HTTPS/TLS)
- **Data masking** for sensitive information
- **Access logging** for compliance requirements
- **Secure file export** with encryption

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PortalTransactionServiceTest

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
# Test portal status
curl -X GET http://localhost:8081/portal/status

# Test transaction access
curl -X GET -H "Authorization: Bearer <token>" \
  http://localhost:8081/portal/transactions

# Test report generation
curl -X POST -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"reportType":"transaction_summary","dateRange":"last_30_days"}' \
  http://localhost:8081/portal/reports/generate
```

## 🚀 Deployment

### Local Development
```bash
# Navigate to module
cd aml-portal

# Install dependencies
mvn clean install

# Run application
mvn spring-boot:run
```

The portal will start on `http://localhost:8081`

### Production Deployment
```bash
# Build JAR file
mvn clean package -DskipTests

# Run with production profile
java -jar target/aml-portal-1.0.0.jar --spring.profiles.active=prod
```

### Docker Deployment
```dockerfile
# Use provided Dockerfile.portal
docker build -f Dockerfile.portal -t aml-portal .
docker run -p 8081:8081 aml-portal
```

## 🔧 Development

### Adding New Portal Features
1. **Create controller** for new portal endpoints
2. **Implement service** logic for business operations
3. **Add security** controls and access restrictions
4. **Create unit tests** for new functionality
5. **Update documentation** with usage examples

### Best Practices
- **Follow REST conventions** for API design
- **Implement proper error handling** with meaningful messages
- **Add comprehensive logging** for portal activities
- **Use appropriate security** measures for all endpoints
- **Include rate limiting** to prevent abuse

## 📊 Monitoring & Analytics

### Portal Metrics
- **Portal access** statistics and usage patterns
- **API performance** metrics and response times
- **Partner activity** monitoring and reporting
- **Error rates** and system health indicators

### Logging
- **Structured logging** with correlation IDs
- **Portal activity** audit trails
- **Security event** logging and monitoring
- **Performance monitoring** with response times

## 🔧 Troubleshooting

### Common Issues

#### Portal Access Problems
```bash
# Check portal service status
curl -X GET http://localhost:8081/portal/status

# Verify partner credentials
# Check JWT token validity
# Review access permissions
```

#### Integration Issues
```bash
# Test webhook connectivity
curl -X POST -H "Content-Type: application/json" \
  -d '{"test":"data"}' \
  https://partner.example.com/webhook

# Check API rate limits
# Verify export file sizes
# Review integration logs
```

#### Performance Issues
```bash
# Monitor portal response times
# Check database query performance
# Review memory usage patterns
# Analyze API usage statistics
```

## 📚 API Reference

### Portal Status API
```http
GET /portal/status
Response: {
  "status": "UP",
  "version": "1.0.0",
  "timestamp": "2024-01-01T00:00:00Z",
  "uptime": "24h 30m 15s"
}
```

### Portal Transactions API
```http
GET /portal/transactions?page=0&size=10&status=completed
Response: {
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0
}
```

### Portal Reports API
```http
POST /portal/reports/generate
Request: {
  "reportType": "transaction_summary",
  "dateRange": "last_30_days",
  "format": "csv"
}
Response: {
  "reportId": "report_123",
  "status": "generating",
  "estimatedCompletion": "2024-01-01T01:00:00Z"
}
```

## 📈 Performance Optimization

### Optimization Tips
- **Implement caching** for frequently accessed data
- **Use pagination** for large datasets
- **Optimize database queries** for portal access patterns
- **Implement connection pooling** for external integrations

### Monitoring
- **Portal response times** and performance metrics
- **API usage patterns** and rate limiting effectiveness
- **Database query** optimization for portal operations
- **Memory usage** and resource consumption

## 🔒 Security Best Practices

### Portal Security
- **Use strong authentication** for all portal access
- **Implement proper authorization** for data access
- **Monitor portal activity** for suspicious behavior
- **Regular security audits** and penetration testing

### Data Protection
- **Encrypt sensitive data** in transit and at rest
- **Implement data masking** for sensitive information
- **Use secure communication** protocols (HTTPS/TLS)
- **Regular security updates** and patch management

### Access Control
- **Implement least privilege** access principles
- **Regular access reviews** and permission audits
- **Monitor and log** all portal activities
- **Implement session management** with proper timeouts

---

**AML Portal Module - Secure portal access for external partners and reporting**
