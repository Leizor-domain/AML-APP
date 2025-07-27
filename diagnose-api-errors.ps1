# Diagnostic Script for API Internal Server Errors
Write-Host "Diagnosing API Internal Server Errors..." -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

# Check if service is running
Write-Host "`n1. Checking if AML Admin service is running..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/admin/health" -Method GET -TimeoutSec 5
    Write-Host "PASS: Service is running - $($healthResponse)" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Service is not running on port 8080" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "`nSOLUTION: Start the backend service with:" -ForegroundColor Yellow
    Write-Host "cd aml-admin" -ForegroundColor Cyan
    Write-Host "mvn spring-boot:run" -ForegroundColor Cyan
    exit 1
}

# Test basic endpoints
Write-Host "`n2. Testing basic endpoints..." -ForegroundColor Yellow

# Test health endpoint
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/admin/health" -Method GET -TimeoutSec 5
    Write-Host "PASS: Health endpoint working" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Health endpoint error - $($_.Exception.Message)" -ForegroundColor Red
}

# Test database health
try {
    $dbHealth = Invoke-RestMethod -Uri "http://localhost:8080/admin/db-health" -Method GET -TimeoutSec 10
    Write-Host "PASS: Database health working - Status: $($dbHealth.status)" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Database health error - $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test currency API with detailed error
Write-Host "`n3. Testing Currency API with detailed error handling..." -ForegroundColor Yellow
try {
    $currencyResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/currency?base=USD&symbols=EUR" -Method GET -TimeoutSec 10
    Write-Host "PASS: Currency API working - Status: $($currencyResponse.StatusCode)" -ForegroundColor Green
    $currencyData = $currencyResponse.Content | ConvertFrom-Json
    Write-Host "   Base: $($currencyData.base), Rates: $($currencyData.rates.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Currency API error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Response Body: $($_.Exception.Response.Content)" -ForegroundColor Red
    }
}

# Test stock API with detailed error
Write-Host "`n4. Testing Stock API with detailed error handling..." -ForegroundColor Yellow
try {
    $stockResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/stocks/AAPL" -Method GET -TimeoutSec 10
    Write-Host "PASS: Stock API working - Status: $($stockResponse.StatusCode)" -ForegroundColor Green
    $stockData = $stockResponse.Content | ConvertFrom-Json
    Write-Host "   Symbol: $($stockData.symbol), Data points: $($stockData.data.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Stock API error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Response Body: $($_.Exception.Response.Content)" -ForegroundColor Red
    }
}

# Test transaction ingestion
Write-Host "`n5. Testing Transaction Ingestion..." -ForegroundColor Yellow
$testTransaction = @{
    sender = "John Doe"
    receiver = "Jane Smith"
    amount = 1000.00
    currency = "USD"
    country = "US"
    transactionType = "TRANSFER"
    dob = "1990-01-01"
}

try {
    $ingestResponse = Invoke-WebRequest -Uri "http://localhost:8080/ingest" -Method POST -Body ($testTransaction | ConvertTo-Json) -ContentType "application/json" -TimeoutSec 10
    Write-Host "PASS: Transaction ingestion working - Status: $($ingestResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Transaction ingestion error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Response Body: $($_.Exception.Response.Content)" -ForegroundColor Red
    }
}

Write-Host "`n=========================================" -ForegroundColor Green
Write-Host "Diagnostic Complete!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green 