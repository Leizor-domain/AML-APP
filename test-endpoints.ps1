# Test Transaction Endpoints Script
Write-Host "Testing AML Admin Transaction Endpoints..." -ForegroundColor Green

# Test 1: Check if service is running
Write-Host "`n1. Checking if service is running on port 8080..." -ForegroundColor Yellow
$portCheck = netstat -an | findstr :8080
if ($portCheck) {
    Write-Host "✅ Service is running on port 8080" -ForegroundColor Green
    Write-Host $portCheck
} else {
    Write-Host "❌ Service is NOT running on port 8080" -ForegroundColor Red
    Write-Host "Please start the service first with: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# Test 2: Test GET /ingest/transactions endpoint
Write-Host "`n2. Testing GET /ingest/transactions endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/ingest/transactions?page=0&size=10" -Method GET -TimeoutSec 10
    Write-Host "✅ GET /ingest/transactions - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ GET /ingest/transactions failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test GET /ingest/transactions with filters
Write-Host "`n3. Testing GET /ingest/transactions with filters..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/ingest/transactions?page=0&size=5&status=HIGH&transactionType=USD" -Method GET -TimeoutSec 10
    Write-Host "✅ GET /ingest/transactions with filters - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ GET /ingest/transactions with filters failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test GET /ingest/transactions/{id} endpoint
Write-Host "`n4. Testing GET /ingest/transactions/{id} endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/ingest/transactions/1" -Method GET -TimeoutSec 10
    Write-Host "✅ GET /ingest/transactions/1 - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ GET /ingest/transactions/1 failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test health check endpoint
Write-Host "`n5. Testing health check endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 10
    Write-Host "✅ Health check - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nEndpoint testing completed!" -ForegroundColor Green 