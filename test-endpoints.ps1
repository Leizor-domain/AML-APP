# Test AML Admin Endpoints Script
Write-Host "Testing AML Admin Endpoints..." -ForegroundColor Green

# Test 1: Check if service is running
$portCheck = netstat -an | findstr :8080
if ($portCheck) {
    Write-Host "Service is running on port 8080" -ForegroundColor Green
    Write-Host $portCheck
} else {
    Write-Host "Service is not running on port 8080" -ForegroundColor Red
    exit 1
}

# Test 2: Test GET /ingest/transactions endpoint
Write-Host "`nTesting GET /ingest/transactions..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/ingest/transactions" -Method GET
    Write-Host "GET /ingest/transactions - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content (first 500 chars): $($response.Content.Substring(0, [System.Math]::Min(500, $response.Content.Length)))"
} catch {
    Write-Host "GET /ingest/transactions failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test GET /ingest/transactions with filters
Write-Host "`nTesting GET /ingest/transactions with filters..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/ingest/transactions?page=0&size=5&status=HIGH&transactionType=USD" -Method GET
    Write-Host "GET /ingest/transactions with filters - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content (first 500 chars): $($response.Content.Substring(0, [System.Math]::Min(500, $response.Content.Length)))"
} catch {
    Write-Host "GET /ingest/transactions with filters failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test GET /ingest/transactions/{id} endpoint
Write-Host "`nTesting GET /ingest/transactions/1..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/ingest/transactions/1" -Method GET
    Write-Host "GET /ingest/transactions/1 - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "GET /ingest/transactions/1 failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test health check endpoint
Write-Host "`nTesting health check..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/admin/db-health" -Method GET
    Write-Host "Health check - Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "Health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nAll endpoint tests completed." -ForegroundColor Green 