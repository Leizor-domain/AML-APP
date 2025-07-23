# Comprehensive Application Test Script
Write-Host "=== AML Application Test Suite ===" -ForegroundColor Cyan
Write-Host ""

# Check if application is running
Write-Host "1. Checking if application is running on port 8080..." -ForegroundColor Yellow
$listening = netstat -ano | findstr "LISTENING" | findstr ":8080"
if ($listening) {
    Write-Host "✅ Application is running on port 8080" -ForegroundColor Green
} else {
    Write-Host "❌ Application is not running on port 8080" -ForegroundColor Red
    Write-Host "Please start the application first: cd aml-admin; mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Test Database Health
Write-Host "2. Testing Database Health Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/public/db/health" -Method GET -UseBasicParsing
    Write-Host "✅ Database Health: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Database Health Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test Tables Endpoint
Write-Host "3. Testing Tables Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/public/db/tables" -Method GET -UseBasicParsing
    Write-Host "✅ Tables Info: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Tables Info Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test User Registration
Write-Host "4. Testing User Registration..." -ForegroundColor Yellow
$testUser = @{
    username = "testuser_$(Get-Date -Format 'HHmmss')"
    password = "password123"
    role = "USER"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/users/register" -Method POST -Body $testUser -ContentType "application/json" -UseBasicParsing
    Write-Host "✅ User Registration: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ User Registration Failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test User Login
Write-Host "5. Testing User Login..." -ForegroundColor Yellow
$loginData = @{
    username = "testuser_$(Get-Date -Format 'HHmmss')"
    password = "password123"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/users/login" -Method POST -Body $loginData -ContentType "application/json" -UseBasicParsing
    Write-Host "✅ User Login: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ User Login Failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test Admin Health
Write-Host "6. Testing Admin Health Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/admin/health" -Method GET -UseBasicParsing
    Write-Host "✅ Admin Health: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Admin Health Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan 