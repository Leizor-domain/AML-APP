# AML Engine - Full Stack Application

A comprehensive Anti-Money Laundering (AML) Engine application with Spring Boot backend and React frontend.

## 🏗️ Architecture

This project consists of multiple modules:

### Backend Modules
- **aml-admin**: Main Spring Boot application with authentication and admin features
- **aml-common**: Shared utilities, security, and common components
- **aml-pojo**: Plain Old Java Objects (POJOs) and data models
- **aml-portal**: Portal-specific Spring Boot application

### Frontend
- **React Application**: Modern React frontend with Material UI and Redux

---

## 🚀 Backend - AML Engine

The `aml-engine` module houses the core logic of the AML system. It evaluates transactions using rule-based checks, applies risk scoring, checks for sanctions, and generates alerts when suspicious activity is detected.

### Key Features

- Full transaction evaluation pipeline
- Rule-based AML checks
- Integrated risk scoring engine
- Sanctions list screening
- Alert generation and deduplication
- Modular, testable services using Spring Boot

### Prerequisites

- Java 17+
- Maven
- PostgreSQL (ensure the DB is running and configured)

### Module Structure

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
```

---

## 🎨 Frontend - React Application

A comprehensive React frontend application for Anti-Money Laundering (AML) Engine built with Material UI, Redux, and modern React practices.

### Features

#### 🔐 Authentication & Authorization
- **Secure Login**: JWT-based authentication with role-based access control. Only admins can create new users.
- **Role-based Dashboards**: Different dashboards for Admin, Analyst, Supervisor, and Viewer roles
- **Protected Routes**: Automatic redirection based on user roles and authentication status
- **Logout Functionality**: Secure token removal and session cleanup

#### 🔍 Transaction Management
- **Transaction Ingestion**: Comprehensive form for submitting new transactions
- **Real-time Processing**: Immediate feedback on transaction status and risk assessment
- **Transaction History**: Filterable and paginated transaction history with detailed views
- **Status Tracking**: Monitor transaction processing status (success, flagged, alert generated)

#### 🧠 Alert Management
- **Alert Dashboard**: Comprehensive view of all system alerts with filtering capabilities
- **Risk Assessment**: Visual indicators for risk levels (LOW, MEDIUM, HIGH)
- **Alert Details**: Detailed view of individual alerts with transaction information
- **Admin Actions**: Dismiss alerts and mark as false positives with reason tracking
- **Sanction Flags**: Display of OFAC, UN, and other sanction list matches

#### ⚙️ Technical Features
- **Material UI**: Professional, responsive design with consistent theming
- **Redux State Management**: Centralized state management for authentication and app data
- **Axios Integration**: Robust API communication with automatic JWT token handling
- **Responsive Design**: Mobile-friendly interface that works across all devices
- **Error Handling**: Comprehensive error handling with user-friendly messages

### Project Structure

```
src/
├── components/
│   ├── Auth/
│   │   ├── LoginForm.jsx
│   │   └── RegisterForm.jsx
│   ├── Layout/
│   │   ├── Navbar.jsx
│   │   ├── Sidebar.jsx
│   │   └── ProtectedRoute.jsx
│   ├── Dashboard/
│   │   ├── AdminDashboard.jsx
│   │   ├── AnalystDashboard.jsx
│   │   ├── SupervisorDashboard.jsx
│   │   └── ViewerDashboard.jsx
│   ├── Transaction/
│   │   ├── IngestForm.jsx
│   │   └── HistoryTable.jsx
│   └── Alerts/
│       ├── AlertsList.jsx
│       └── AlertDetails.jsx
├── pages/
│   ├── LoginPage.jsx
│   ├── RegisterPage.jsx
│   ├── IngestPage.jsx
│   ├── AlertsPage.jsx
│   └── AlertDetailsPage.jsx
├── services/
│   ├── api.js
│   ├── auth.js
│   ├── transaction.js
│   └── alerts.js
├── store/
│   ├── index.js
│   └── authSlice.js
├── App.jsx
└── main.jsx
```

### Prerequisites

- Node.js (v16 or higher)
- Yarn package manager
- Admin server running on `http://localhost:8080`
- Portal server running on `http://localhost:8082`

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd aml-frontend
   ```

2. **Install dependencies**
   ```bash
   yarn install
   ```

3. **Environment Configuration**
   Create a `.env` file in the root directory:
   ```env
   VITE_ADMIN_API_URL=http://localhost:8080
   VITE_PORTAL_API_URL=http://localhost:8082
   ```

4. **Start the development server**
   ```bash
   yarn dev
   ```

5. **Open your browser**
   Navigate to `http://localhost:3000`

### Available Scripts

- `yarn dev` - Start development server
- `yarn build` - Build for production
- `yarn preview` - Preview production build
- `yarn lint` - Run ESLint

---

## 🔌 API Integration

The frontend is designed to work with dual Spring Boot backends:

### Admin Server (Port 8080)
- User authentication and management
- Alert management
- System administration

### Portal Server (Port 8082)
- Transaction ingestion
- Transaction history
- Portal-specific operations

### Authentication
- `POST /users/login` - User login
- `POST /users/create` - Admin-only user creation (requires ROLE_ADMIN)

### Transactions
- `POST /ingest` - Submit new transaction
- `GET /transactions` - Get transaction history

### Alerts
- `GET /alerts` - Get all alerts
- `GET /alerts/{id}` - Get alert details
- `PATCH /alerts/{id}/dismiss` - Dismiss alert
- `PATCH /alerts/{id}/false-positive` - Mark as false positive

---

## 👥 User Roles

### Admin
- Full system access
- User management
- System configuration
- Report generation

### Analyst
- Transaction analysis
- Alert investigation
- Risk assessment
- Case management

### Supervisor
- Team oversight
- Alert review
- Performance monitoring
- Escalation management

### Viewer
- Read-only access
- View reports
- Monitor system status
- View statistics

---

## 🔒 Security Features

- **JWT Token Management**: Automatic token handling with expiration checks
- **Role-based Access Control**: Route protection based on user roles
- **Secure API Communication**: All requests include authentication headers
- **Session Management**: Automatic logout on token expiration
- **Input Validation**: Client-side validation for all forms

---

## 🎨 Customization

### Theming
The application uses Material UI theming. Customize colors and styles in `src/main.jsx`:

```javascript
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});
```

---

## 🚀 Getting Started

### Backend Setup
1. Ensure Java 17+ and Maven are installed
2. Configure PostgreSQL database
3. Run the Spring Boot applications:
   ```bash
   # Admin server
   cd aml-admin && mvn spring-boot:run
   
   # Portal server
   cd aml-portal && mvn spring-boot:run
   ```

### Frontend Setup
1. Install Node.js dependencies
2. Configure environment variables
3. Start the development server:
   ```bash
   cd aml-frontend && yarn dev
   ```

---

## 📝 License

This project is licensed under the MIT License.

## Troubleshooting Backend Build Errors

### 1. Run Maven Clean to Remove Stale Classes
```
cd aml-admin
mvn clean
```

### 2. Ensure Only One UserRepository Exists
- Only keep `aml-admin/src/main/java/com/leizo/admin/repository/UserRepository.java`.
- Delete any duplicate `UserRepository.java` in other packages (e.g., `com/leizo/repository`).

### 3. Check @EnableJpaRepositories
- In `AMLAdminApplication.java`, use:
  ```java
  @EnableJpaRepositories(basePackages = {"com.leizo.admin.repository"})
  ```
- Do **not** include `com.leizo.repository` or other packages.

### 4. Remove Stale Compiled Files
- Delete any `.class` files in `aml-admin/target/classes/com/leizo/repository/` if they exist.

### 5. Rebuild and Run Only the Correct Module
```
cd aml-admin
mvn clean compile
mvn spring-boot:run
```

### 6. If Bean Conflict Persists
- Add to `application.properties` (for debugging only):
  ```
  spring.main.allow-bean-definition-overriding=true
  ```

### 7. Other Common Issues
- If you see `Database may be already in use`, stop all Java processes and delete the H2 database file:
  ```powershell
  Get-Process java | Stop-Process -Force
  Remove-Item "aml-admin/data/amlengine_db.mv.db" -Force
  ```
- If you see schema validation errors, drop and recreate your dev database or update your entity/table definitions to match.

## OpenSanctions Integration (Real-Time Sanctions Screening)

### How It Works
- Every transaction is screened in real time against the OpenSanctions API.
- If a match is found, an `Alert` is created in the database with details of the match (entity name, list, reason, etc.).
- The backend logs a warning and returns alert details for frontend feedback.

### Example Test Data (Sanctioned Transaction)

Submit a transaction with a known sanctioned individual:

```json
{
  "transactionId": 1001,
  "senderName": "Viktor Yanukovych",
  "senderCountry": "Ukraine",
  "senderDob": "1950-07-09",
  "receiverName": "John Doe",
  "receiverCountry": "USA",
  "amount": 10000,
  "currency": "USD",
  "date": "2025-07-21T10:00:00Z"
}
```

### Backend Logging and Alert Details
- When a match is found, the backend logs:
  - `OpenSanctions MATCH: Entity [Viktor Yanukovych] from [Ukraine] (DOB: 1950-07-09) is sanctioned! Details: {...}`
  - `ALERT CREATED: Transaction [1001] flagged for sanctioned entity [Viktor Yanukovych]`
- The `Alert` entity will have:
  - `matchedEntityName`: Viktor Yanukovych
  - `matchedList`: OpenSanctions
  - `matchReason`: Matched by OpenSanctions real-time screening
  - `transactionId`, `reason`, `timestamp`, `alertType`, `priorityLevel`

### Frontend Real-Time Feedback
- When a transaction is flagged, the backend returns alert details.
- The frontend should display a red warning banner or modal:
  - `⚠️ Transaction flagged! Sender "Viktor Yanukovych" is on OpenSanctions list.`
  - Show details: name, country, DOB, matched list, reason, and a link to the OpenSanctions profile if available.
- If no match, show a "Clear" or "Not Sanctioned" status.

### Troubleshooting
- Ensure the backend can reach `https://api.opensanctions.org/`.
- Check backend logs for `OpenSanctions MATCH` and `ALERT CREATED` messages.
- Alerts are stored in the `alerts` table with match details for audit and review.
