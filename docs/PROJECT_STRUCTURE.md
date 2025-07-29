# AML Application - Project Structure

## 📁 Overview

This is a comprehensive Anti-Money Laundering (AML) application built with Spring Boot backend and React frontend, organized in a modular architecture for scalability and maintainability.

## 🏗️ Architecture

```
AML App/
├── 📁 aml-admin/          # Main Spring Boot application
├── 📁 aml-common/         # Shared utilities and components
├── 📁 aml-pojo/           # Domain models and entities
├── 📁 aml-portal/         # Portal-specific application
├── 📁 src/                # React frontend application
├── 📁 public/             # Static assets
├── 📁 docs/               # Documentation
├── 📁 scripts/            # Utility scripts
└── 📁 config/             # Configuration files
```

## 🔧 Backend Modules

### aml-admin/
**Main Spring Boot Application**
- **Controllers**: REST API endpoints
- **Services**: Business logic implementation
- **Repositories**: Data access layer
- **Entities**: JPA entities
- **Configuration**: Spring configuration
- **Security**: Authentication and authorization

### aml-common/
**Shared Components**
- **Security**: JWT utilities, user management
- **Entities**: Common domain models
- **Repositories**: Shared data access
- **Enums**: Common enumerations

### aml-pojo/
**Domain Models**
- **Entities**: Core business entities
- **Enums**: Domain-specific enumerations
- **Models**: Data transfer objects

### aml-portal/
**Portal Application**
- **Controllers**: Portal-specific endpoints
- **Services**: Portal business logic
- **Configuration**: Portal configuration

## 🎨 Frontend Structure

### src/
**React Application**
```
src/
├── 📁 components/         # Reusable UI components
│   ├── 📁 Alerts/        # Alert-related components
│   ├── 📁 Auth/          # Authentication components
│   ├── 📁 Dashboard/     # Dashboard components
│   ├── 📁 Layout/        # Layout components
│   ├── 📁 Transaction/   # Transaction components
│   ├── 📁 CurrencyConverter/ # Currency conversion
│   └── 📁 StockMarket/   # Stock market components
├── 📁 pages/             # Page components
├── 📁 services/          # API service layer
├── 📁 store/             # Redux state management
├── 📁 utils/             # Utility functions
├── App.jsx              # Main application component
└── main.jsx             # Application entry point
```

## 📋 Key Features

### Backend Features
- **Transaction Monitoring**: Real-time transaction analysis
- **Alert Generation**: Automated suspicious activity detection
- **Sanctions Screening**: OFAC and local sanctions checking
- **Rule Engine**: Configurable AML rules
- **Risk Scoring**: Dynamic risk assessment
- **User Management**: Role-based access control
- **Audit Logging**: Comprehensive activity tracking

### Frontend Features
- **Responsive Dashboard**: Role-specific dashboards
- **Real-time Alerts**: Live alert monitoring
- **Transaction Ingestion**: File upload and processing
- **Currency Conversion**: Real-time exchange rates
- **Stock Market Data**: Market analysis tools
- **User Management**: Admin user controls
- **Reporting**: Comprehensive reporting tools

## 🚀 Development Workflow

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 13+
- Maven 3.8+

### Local Development
1. **Backend Setup**
   ```bash
   cd aml-admin
   mvn spring-boot:run
   ```

2. **Frontend Setup**
   ```bash
   npm install
   npm run dev
   ```

3. **Database Setup**
   ```bash
   # Use the provided schema.sql
   # Or run the setup script
   .\setup_local_database.ps1
   ```

## 📊 Testing

### Backend Testing
- **Unit Tests**: JUnit 5 with Mockito
- **Integration Tests**: Spring Boot Test
- **API Tests**: REST Assured

### Frontend Testing
- **Unit Tests**: Jest with React Testing Library
- **Component Tests**: Storybook
- **E2E Tests**: Cypress

## 🔒 Security

### Authentication
- JWT-based authentication
- Role-based access control
- Session management

### Data Protection
- Encrypted data transmission
- Secure API endpoints
- Input validation and sanitization

## 📈 Monitoring

### Application Metrics
- Health checks
- Performance monitoring
- Error tracking
- Audit logging

### Business Metrics
- Transaction volumes
- Alert statistics
- Risk scores
- Compliance reports

## 🚀 Deployment

### Production Deployment
- **Backend**: Spring Boot JAR deployment
- **Frontend**: Static asset hosting
- **Database**: PostgreSQL with connection pooling
- **Load Balancer**: Nginx configuration

### Container Deployment
- **Docker**: Multi-stage builds
- **Docker Compose**: Local development
- **Kubernetes**: Production orchestration

## 📚 Documentation

### Technical Documentation
- API documentation (OpenAPI/Swagger)
- Database schema documentation
- Component documentation
- Deployment guides

### Business Documentation
- User manuals
- Compliance documentation
- Process workflows
- Training materials

## 🤝 Contributing

### Code Standards
- **Backend**: Java coding conventions
- **Frontend**: ESLint and Prettier
- **Documentation**: Markdown standards
- **Testing**: Minimum 80% coverage

### Development Process
1. Feature branch creation
2. Code review process
3. Automated testing
4. Documentation updates
5. Deployment verification

---

**AML Application** - Professional, scalable, and maintainable anti-money laundering solution.