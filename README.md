# AML Engine Frontend

A comprehensive React frontend application for Anti-Money Laundering (AML) Engine built with Material UI, Redux, and modern React practices.

## Features

### 🔐 Authentication & Authorization
- **Secure Login/Register**: JWT-based authentication with role-based access control
- **Role-based Dashboards**: Different dashboards for Admin, Analyst, Supervisor, and Viewer roles
- **Protected Routes**: Automatic redirection based on user roles and authentication status
- **Logout Functionality**: Secure token removal and session cleanup

### 🔍 Transaction Management
- **Transaction Ingestion**: Comprehensive form for submitting new transactions
- **Real-time Processing**: Immediate feedback on transaction status and risk assessment
- **Transaction History**: Filterable and paginated transaction history with detailed views
- **Status Tracking**: Monitor transaction processing status (success, flagged, alert generated)

### 🧠 Alert Management
- **Alert Dashboard**: Comprehensive view of all system alerts with filtering capabilities
- **Risk Assessment**: Visual indicators for risk levels (LOW, MEDIUM, HIGH)
- **Alert Details**: Detailed view of individual alerts with transaction information
- **Admin Actions**: Dismiss alerts and mark as false positives with reason tracking
- **Sanction Flags**: Display of OFAC, UN, and other sanction list matches

### ⚙️ Technical Features
- **Material UI**: Professional, responsive design with consistent theming
- **Redux State Management**: Centralized state management for authentication and app data
- **Axios Integration**: Robust API communication with automatic JWT token handling
- **Responsive Design**: Mobile-friendly interface that works across all devices
- **Error Handling**: Comprehensive error handling with user-friendly messages

## Project Structure

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

## Prerequisites

- Node.js (v16 or higher)
- Yarn package manager
- Admin server running on `http://localhost:8080`
- Portal server running on `http://localhost:8081`

## Installation

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
   VITE_PORTAL_API_URL=http://localhost:8081
   ```

4. **Start the development server**
   ```bash
   yarn dev
   ```

5. **Open your browser**
   Navigate to `http://localhost:3000`

## Available Scripts

- `yarn dev` - Start development server
- `yarn build` - Build for production
- `yarn preview` - Preview production build
- `yarn lint` - Run ESLint

## API Integration

The frontend is designed to work with dual Spring Boot backends:

### Admin Server (Port 8080)
- User authentication and management
- Alert management
- System administration

### Portal Server (Port 8081)
- Transaction ingestion
- Transaction history
- Portal-specific operations

### Authentication
- `POST /users/login` - User login
- `POST /users/register` - User registration

### Transactions
- `POST /ingest` - Submit new transaction
- `GET /transactions` - Get transaction history

### Alerts
- `GET /alerts` - Get all alerts
- `GET /alerts/{id}` - Get alert details
- `PATCH /alerts/{id}/dismiss` - Dismiss alert
- `PATCH /alerts/{id}/false-positive` - Mark as false positive

## User Roles

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

## Security Features

- **JWT Token Management**: Automatic token handling with expiration checks
- **Role-based Access Control**: Route protection based on user roles
- **Secure API Communication**: All requests include authentication headers
- **Session Management**: Automatic logout on token expiration
- **Input Validation**: Client-side validation for all forms

## Customization

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
})
```

### API Configuration
Update the API base URL in the `.env` file or modify `src/services/api.js` for different backend configurations.

## Troubleshooting

### Common Issues

1. **Backend Connection Error**
   - Ensure the backend is running on the correct port
   - Check the `VITE_API_URL` in your `.env` file
   - Verify CORS configuration on the backend

2. **Authentication Issues**
   - Clear browser localStorage
   - Check JWT token format in browser dev tools
   - Verify backend authentication endpoints

3. **Build Errors**
   - Clear node_modules and reinstall: `rm -rf node_modules && yarn install`
   - Check for version conflicts in package.json

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository. 