# Test AML Application Script
Write-Host "Testing AML Application..." -ForegroundColor Green

# Test 1: Check if application is running
$portCheck = netstat -an | findstr :8080
if ($portCheck) {
    Write-Host "Application is running on port 8080" -ForegroundColor Green
    Write-Host $portCheck
} else {
    Write-Host "Application is not running on port 8080" -ForegroundColor Red
    exit 1
}

# Test 2: Test database health endpoint
Write-Host "`nTesting database health..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/admin/db-health" -Method GET
    Write-Host "Database Health: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "Database health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test tables info endpoint
Write-Host "`nTesting tables info..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/admin/tables" -Method GET
    Write-Host "Tables Info: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "Tables info check failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test user registration endpoint
Write-Host "`nTesting user registration..." -ForegroundColor Yellow
try {
    $userData = @{
        username = "testuser"
        email = "test@example.com"
        password = "testpassword123"
        role = "ANALYST"
    }
    
    $jsonData = $userData | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -Body $jsonData -ContentType "application/json"
    Write-Host "User Registration: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "User registration failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test user login endpoint
Write-Host "`nTesting user login..." -ForegroundColor Yellow
try {
    $loginData = @{
        username = "testuser"
        password = "testpassword123"
    }
    
    $jsonData = $loginData | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -Body $jsonData -ContentType "application/json"
    Write-Host "User Login: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "User login failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Test admin health endpoint
Write-Host "`nTesting admin health..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/admin/health" -Method GET
    Write-Host "Admin Health: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "Admin health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nAll application tests completed." -ForegroundColor Green 