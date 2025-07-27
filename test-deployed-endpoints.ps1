# Test Deployed APIs on Render
# This script tests the APIs that are currently deployed on Render

Write-Host "Testing Deployed APIs on Render..." -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

$baseUrl = "https://aml-admin.onrender.com"

# Test 1: Health Check
Write-Host "`n1. Testing Health Check..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "$baseUrl/admin/health" -Method GET -TimeoutSec 10
    Write-Host "PASS: Health check working - $($healthResponse)" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Health check failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Currency API
Write-Host "`n2. Testing Currency API..." -ForegroundColor Yellow
try {
    $currencyResponse = Invoke-RestMethod -Uri "$baseUrl/api/currency?base=USD&symbols=EUR,GBP" -Method GET -TimeoutSec 15
    Write-Host "PASS: Currency API working" -ForegroundColor Green
    Write-Host "   Base: $($currencyResponse.base)" -ForegroundColor Gray
    Write-Host "   Rates count: $($currencyResponse.rates.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Currency API failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 3: Currency Conversion
Write-Host "`n3. Testing Currency Conversion..." -ForegroundColor Yellow
try {
    $convertResponse = Invoke-RestMethod -Uri "$baseUrl/api/currency/convert?from=USD&to=EUR&amount=100" -Method GET -TimeoutSec 15
    Write-Host "PASS: Currency conversion working" -ForegroundColor Green
    Write-Host "   Converted: $($convertResponse.amount) USD = $($convertResponse.convertedAmount) EUR" -ForegroundColor Gray
    Write-Host "   Rate: $($convertResponse.rate)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Currency conversion failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 4: Stock Market API
Write-Host "`n4. Testing Stock Market API..." -ForegroundColor Yellow
try {
    $stockResponse = Invoke-RestMethod -Uri "$baseUrl/api/stocks/AAPL" -Method GET -TimeoutSec 15
    Write-Host "PASS: Stock Market API working" -ForegroundColor Green
    Write-Host "   Symbol: $($stockResponse.symbol)" -ForegroundColor Gray
    Write-Host "   Data points: $($stockResponse.data.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Stock Market API failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 5: Transaction History
Write-Host "`n5. Testing Transaction History..." -ForegroundColor Yellow
try {
    $historyResponse = Invoke-RestMethod -Uri "$baseUrl/ingest/transactions?page=0&size=10" -Method GET -TimeoutSec 15
    Write-Host "PASS: Transaction history working" -ForegroundColor Green
    Write-Host "   Total Elements: $($historyResponse.totalElements)" -ForegroundColor Gray
    Write-Host "   Content Count: $($historyResponse.content.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Transaction history failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host "`n=====================================" -ForegroundColor Green
Write-Host "Deployed API Test Complete!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

Write-Host "`nNote: If APIs are still failing, the changes may not have been deployed yet." -ForegroundColor Yellow
Write-Host "Check Render dashboard for deployment status." -ForegroundColor Yellow 