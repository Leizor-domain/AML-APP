# AML Application

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18+-blue.svg)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive **Anti-Money Laundering (AML)** application built with Spring Boot backend and React frontend, designed for financial institutions to monitor transactions, detect suspicious activities, and ensure regulatory compliance.

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Node.js 18+**
- **PostgreSQL 13+**
- **Maven 3.8+**

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AML-App
   ```

2. **Backend Setup**
   ```bash
   cd aml-admin
   mvn spring-boot:run
   ```

3. **Frontend Setup**
   ```bash
   npm install
   npm run dev
   ```

4. **Database Setup**
   ```bash
   # Use the provided schema
   psql -U your_user -d your_database -f config/schema.sql
   ```

## 📁 Project Structure

```
AML App/
├── 📁 aml-admin/          # Main Spring Boot application
├── 📁 aml-common/         # Shared utilities and components
├── 📁 aml-pojo/           # Domain models and entities
├── 📁 aml-portal/         # Portal-specific application
├── 📁 src/                # React frontend application
├── 📁 docs/               # Documentation
├── 📁 scripts/            # Utility scripts
├── 📁 config/             # Configuration files
├── 📁 test-data/          # Test data files
└── 📁 public/             # Static assets
```

📖 **Detailed Structure**: See [docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)

## 🔧 Core Features

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

## 🎯 User Roles

- **Admin**: System administration and user management
- **Analyst**: Transaction analysis and alert review
- **Supervisor**: Team management and oversight
- **Viewer**: Read-only access to reports and data

## 🛠️ Development

### Backend Development
```bash
cd aml-admin
mvn clean install
mvn spring-boot:run
```

### Frontend Development
```bash
npm install
npm run dev
```

### Testing
```bash
# Backend tests
cd aml-admin
mvn test

# Frontend tests
npm test
```

## 📊 API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: See [docs/API_REFERENCE.md](docs/API_REFERENCE.md)

## 🔒 Security

- **JWT Authentication**: Secure token-based authentication
- **Role-based Access**: Granular permission control
- **Data Encryption**: Encrypted data transmission
- **Audit Logging**: Comprehensive activity tracking

## 🚀 Deployment

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

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up --build
```

## 📈 Monitoring

- **Health Checks**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Logs**: Application logs with structured logging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards
- **Backend**: Java coding conventions
- **Frontend**: ESLint and Prettier
- **Documentation**: Markdown standards
- **Testing**: Minimum 80% coverage

## 📚 Documentation

- **[Project Structure](docs/PROJECT_STRUCTURE.md)**: Detailed project organization
- **[API Reference](docs/API_REFERENCE.md)**: Complete API documentation
- **[Deployment Guide](docs/DEPLOYMENT.md)**: Production deployment instructions
- **[User Manual](docs/USER_MANUAL.md)**: End-user documentation
- **[Developer Guide](docs/DEVELOPER_GUIDE.md)**: Development setup and guidelines

## 🐛 Troubleshooting

### Common Issues
- **Database Connection**: Check PostgreSQL service and credentials
- **Port Conflicts**: Ensure ports 8080 and 3000 are available
- **Dependencies**: Run `mvn clean install` and `npm install`

### Support
- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- **Documentation**: [docs/](docs/)
- **Email**: support@your-company.com

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot Team**: For the excellent framework
- **React Team**: For the powerful frontend library
- **PostgreSQL**: For the reliable database
- **Open Source Community**: For the amazing tools and libraries

---

**AML Application** - Professional, scalable, and maintainable anti-money laundering solution.

*Built with ❤️ for financial compliance and security.*
