# Render Deployment API Fixes

## Current Issues Identified

### 1. 403 Forbidden Errors
- Health Check API (`/admin/db-health`)
- Transaction History API (`/ingest/transactions`)
- User Management API (`/admin/users`)

### 2. 500 Internal Server Errors
- Currency Converter API (`/api/currency/**`)
- Stock Market API (`/api/stocks/**`)

## Root Causes

### 1. Security Configuration Issues
The 403 errors indicate that the Spring Security configuration is blocking access to these endpoints. This suggests:
- The deployed version doesn't have our latest security configuration updates
- Environment variables for authentication are not properly set

### 2. External API Connection Issues
The 500 errors for currency and stock APIs suggest:
- External API calls are failing (network timeouts, API key issues)
- The deployed version doesn't have our error handling improvements

## Required Fixes

### 1. Environment Variables on Render
Ensure these environment variables are set in your Render dashboard:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://dpg-d201e92dbo4c73fv5nb0-a/amlengine_db
DATABASE_USER=admin
DATABASE_PASSWORD=8FLqQ4m1VELiPdvK9amQI1hxwkBpvt3A8FLqQ4m1VELiPdvK9amQI1hxwkBpvt3A

# API Keys
TWELVE_API_KEY=f758b6f9d1fa4afe9d4d18ec52f4f378

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=10000
```

### 2. Security Configuration Update
The security configuration needs to be updated to allow public access to certain endpoints:

```java
// In SecurityConfig.java
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/admin/health").permitAll()
    .requestMatchers("/api/currency/**").permitAll()
    .requestMatchers("/api/stocks/**").permitAll()
    .requestMatchers("/ingest/transactions").permitAll()
    .requestMatchers("/admin/users").authenticated()
    .anyRequest().authenticated()
)
```

### 3. CORS Configuration
Ensure CORS is properly configured for the deployed domain:

```java
// In CorsConfig.java
.allowedOrigins(
    "https://aml-app.onrender.com",
    "https://aml-admin.onrender.com",
    "http://localhost:3000",
    "http://localhost:5173"
)
```

## Deployment Steps

### 1. Update Environment Variables
1. Go to your Render dashboard
2. Navigate to your aml-admin service
3. Go to Environment tab
4. Add/update the environment variables listed above

### 2. Force Redeploy
1. In Render dashboard, go to your service
2. Click "Manual Deploy"
3. Select "Clear build cache & deploy"

### 3. Verify Deployment
After deployment, test the APIs using the test script:
```powershell
powershell -ExecutionPolicy Bypass -File test-deployed-apis.ps1
```

## Expected Results After Fixes

- **Health Check**: 200 OK with database status
- **Currency API**: 200 OK with exchange rates or graceful error message
- **Stock API**: 200 OK with stock data or graceful error message
- **Transaction History**: 200 OK with paginated results
- **User Management**: 200 OK (if authenticated) or 401 Unauthorized

## Monitoring

After deployment, monitor the Render logs for:
- Database connection errors
- External API timeout errors
- Security configuration issues
- Environment variable resolution problems

## Fallback Strategy

If external APIs continue to fail, the services should return:
```json
{
  "error": "Service temporarily unavailable. Please try again later.",
  "base": "USD",
  "rates": {}
}
```

This ensures the frontend doesn't crash and users get meaningful error messages. 