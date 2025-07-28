# AML Engine - Anti-Money Laundering System

A comprehensive, production-ready Anti-Money Laundering (AML) Engine with Spring Boot backend, React frontend, and advanced transaction monitoring capabilities.

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Node.js 18+**
- **PostgreSQL 13+**
- **Maven 3.8+**

### Installation
```bash
# Clone the repository
git clone https://github.com/Leizor-domain/AML-APP.git
cd AML-APP

# Backend Setup
cd aml-admin
mvn clean install
mvn spring-boot:run

# Frontend Setup (in new terminal)
npm install
npm run dev
```

## 🏗️ Architecture Overview

### Backend Modules
- **`aml-admin`** - Main Spring Boot application with authentication, transaction processing, and alert management
- **`aml-common`** - Shared utilities, security components, and common entities
- **`aml-pojo`** - Core domain models and data transfer objects
- **`aml-portal`** - Portal-specific Spring Boot application

### Frontend
- **React 18** with **Material-UI** and **Redux Toolkit**
- **Role-based dashboards** (Admin, Analyst, Supervisor, Viewer)
- **Real-time transaction monitoring** and alert management

## 🔧 Core Features

### Transaction Processing
- **CSV/JSON ingestion** with validation and error handling
- **Real-time transaction evaluation** against AML rules
- **Risk scoring** with configurable thresholds
- **Sanctions screening** (OFAC SDN + Local lists)
- **Alert generation** with deduplication and cooldown

### Rule Engine
- **JSON-based rule configuration** with hot-reloading
- **Multiple rule types**: High value, sanctions, structuring, PEP, geographic risk
- **Configurable sensitivity levels** (LOW, MEDIUM, HIGH)
- **Rule chaining** and priority-based evaluation

### Alert Management
- **Comprehensive alert types**: Sanctions, high value, behavioral patterns
- **Alert lifecycle management**: Creation, review, resolution
- **Dashboard integration** with real-time updates
- **Export capabilities** for compliance reporting

### Security & Authentication
- **JWT-based authentication** with role-based access control
- **Four user roles**: Admin, Analyst, Supervisor, Viewer
- **Secure API endpoints** with proper authorization
- **Audit logging** for compliance requirements

## 📊 Dashboard Features

### Admin Dashboard
- **System overview** with key metrics
- **User management** with role assignment
- **Alert monitoring** with real-time updates
- **Transaction processing** status and statistics

### Analyst Dashboard
- **Alert review** and investigation tools
- **Transaction analysis** with detailed views
- **Risk assessment** tools and reporting
- **Case management** for ongoing investigations

### Supervisor Dashboard
- **Team performance** monitoring
- **Escalation management** for high-risk alerts
- **Compliance reporting** and analytics
- **System configuration** and rule management

## 🗄️ Database Schema

### Core Tables
- **`users`** - User accounts and role management
- **`transactions`** - Transaction data with metadata
- **`alerts`** - Generated alerts with status tracking
- **`transaction_metadata`** - Extended transaction properties

### Key Relationships
- Transactions → Alerts (one-to-many)
- Users → Transactions (many-to-many via roles)
- Alerts → Users (assigned investigators)

## 🔌 API Endpoints

### Authentication
- `POST /auth/login` - User authentication
- `POST /auth/register` - User registration (admin only)

### Transaction Management
- `POST /ingest/file` - Upload transaction files (CSV/JSON)
- `GET /ingest/transactions` - Retrieve transaction history
- `GET /ingest/transactions/{id}` - Get specific transaction

### Alert Management
- `GET /alerts` - List all alerts with filtering
- `GET /alerts/{id}` - Get specific alert details
- `PATCH /alerts/{id}/status` - Update alert status
- `POST /alerts/populate-mock` - Generate test alerts

### User Management
- `GET /users` - List all users
- `POST /users/create` - Create new user (admin only)
- `PATCH /users/{id}/status` - Enable/disable user

## 🧪 Testing

### Backend Testing
```bash
cd aml-admin
mvn test
```

### Frontend Testing
```bash
npm test
```

### Integration Testing
```bash
# Test transaction ingestion
curl -X POST -F "file=@test_transactions.csv" http://localhost:8080/ingest/file

# Test alert generation
curl -X POST http://localhost:8080/alerts/populate-mock
```

## 🚀 Deployment

### Local Development
```bash
# Backend (Port 8080)
cd aml-admin
mvn spring-boot:run

# Frontend (Port 5173)
npm run dev
```

### Production Deployment
```bash
# Build backend
cd aml-admin
mvn clean package -DskipTests

# Build frontend
npm run build

# Deploy using Docker
docker-compose up -d
```

### Environment Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aml_database
DB_USERNAME=aml_user
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# API URLs
VITE_ADMIN_API_URL=http://localhost:8080
VITE_PORTAL_API_URL=http://localhost:8081
```

## 📈 Monitoring & Analytics

### Key Metrics
- **Transaction processing rate** (transactions/minute)
- **Alert generation rate** (alerts/day)
- **False positive rate** (resolved alerts/total alerts)
- **System uptime** and performance metrics

### Logging
- **Structured logging** with correlation IDs
- **Audit trails** for compliance requirements
- **Error tracking** with detailed stack traces
- **Performance monitoring** with response times

## 🔒 Security Features

### Data Protection
- **Encrypted data transmission** (HTTPS/TLS)
- **Secure password hashing** (BCrypt)
- **Input validation** and sanitization
- **SQL injection prevention** with parameterized queries

### Access Control
- **Role-based permissions** with fine-grained access
- **Session management** with automatic timeout
- **API rate limiting** to prevent abuse
- **Audit logging** for security events

## 📚 Documentation

### Additional Resources
- [Alert Generation Guide](ALERT_GENERATION_DETAILED_GUIDE.md)
- [High-Risk Countries Documentation](HIGH_RISK_COUNTRIES_DOCUMENTATION.md)
- [Local Sanctions List Documentation](LOCAL_SANCTIONS_LIST_DOCUMENTATION.md)
- [OFAC API Integration Guide](OFAC_API_OFFLINE_ANALYSIS.md)

### API Documentation
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

## 🤝 Contributing

### Development Workflow
1. **Fork** the repository
2. **Create** a feature branch
3. **Implement** your changes
4. **Test** thoroughly
5. **Submit** a pull request

### Code Standards
- **Java**: Follow Google Java Style Guide
- **JavaScript**: Use ESLint and Prettier
- **Documentation**: Update README files for new features
- **Testing**: Maintain >80% code coverage

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Troubleshooting
- **Database connection issues**: Check PostgreSQL service and credentials
- **Authentication problems**: Verify JWT configuration and user roles
- **Alert generation**: Review rule configuration and transaction data
- **Performance issues**: Monitor database indexes and query optimization

### Contact
- **Issues**: [GitHub Issues](https://github.com/Leizor-domain/AML-APP/issues)
- **Documentation**: Check the `/docs` folder for detailed guides
- **Email**: leizordev@outlook.com

---

**Built with ❤️ for financial compliance and security**
