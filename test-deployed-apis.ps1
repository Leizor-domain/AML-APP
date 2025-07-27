# Comprehensive Deployed API Test Script
# Tests all APIs on the deployed Render application

Write-Host "Testing Deployed AML APIs on Render..." -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

$baseUrl = "https://aml-admin.onrender.com"

# Test 1: Health Check
Write-Host "`n1. Testing Health Check..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "$baseUrl/admin/db-health" -Method GET -TimeoutSec 10
    Write-Host "PASS: Health check working" -ForegroundColor Green
    Write-Host "   Response: $($healthResponse | ConvertTo-Json)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Health check error" -ForegroundColor Red
    Write-Host "   Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Currency API - Latest Rates
Write-Host "`n2. Testing Currency API - Latest Rates..." -ForegroundColor Yellow
try {
    $ratesResponse = Invoke-RestMethod -Uri "$baseUrl/api/currency?base=USD" -Method GET -TimeoutSec 15
    Write-Host "PASS: Currency rates API working" -ForegroundColor Green
    Write-Host "   Base: $($ratesResponse.base)" -ForegroundColor Gray
    Write-Host "   Rates count: $($ratesResponse.rates.Count)" -ForegroundColor Gray
    if ($ratesResponse.error) {
        Write-Host "   Error: $($ratesResponse.error)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "FAIL: Currency rates API error" -ForegroundColor Red
    Write-Host "   Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Currency API - Conversion
Write-Host "`n3. Testing Currency API - Conversion..." -ForegroundColor Yellow
try {
    $convertResponse = Invoke-RestMethod -Uri "$baseUrl/api/currency/convert?from=USD&to=EUR&amount=1" -Method GET -TimeoutSec 15
    Write-Host "PASS: Currency conversion API working" -ForegroundColor Green
    Write-Host "   Converted: $($convertResponse.amount) USD = $($convertResponse.convertedAmount) EUR" -ForegroundColor Gray
    Write-Host "   Rate: $($convertResponse.rate)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Currency conversion API error" -ForegroundColor Red
    Write-Host "   Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Stock Market API
Write-Host "`n4. Testing Stock Market API..." -ForegroundColor Yellow
try {
    $stockResponse = Invoke-RestMethod -Uri "$baseUrl/api/stocks/AAPL" -Method GET -TimeoutSec 15
    Write-Host "PASS: Stock market API working" -ForegroundColor Green
    Write-Host "   Symbol: $($stockResponse.symbol)" -ForegroundColor Gray
    Write-Host "   Data points: $($stockResponse.data.Count)" -ForegroundColor Gray
    if ($stockResponse.error) {
        Write-Host "   Error: $($stockResponse.error)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "FAIL: Stock market API error" -ForegroundColor Red
    Write-Host "   Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Transaction History
Write-Host "`n5. Testing Transaction History API..." -ForegroundColor Yellow
try {
    $historyResponse = Invoke-RestMethod -Uri "$baseUrl/ingest/transactions?page=0&size=10" -Method GET -TimeoutSec 15
    Write-Host "PASS: Transaction history API working" -ForegroundColor Green
    Write-Host "   Total Elements: $($historyResponse.totalElements)" -ForegroundColor Gray
    Write-Host "   Total Pages: $($historyResponse.totalPages)" -ForegroundColor Gray
    Write-Host "   Content Count: $($historyResponse.content.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Transaction history API error" -ForegroundColor Red
    Write-Host "   Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: User Management API
Write-Host "`n6. Testing User Management API..." -ForegroundColor Yellow
try {
    $usersResponse = Invoke-RestMethod -Uri "$baseUrl/admin/users" -Method GET -TimeoutSec 15
    Write-Host "PASS: User management API working" -ForegroundColor Green
    Write-Host "   Users count: $($usersResponse.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: User management API error" -ForegroundColor Red
    Write-Host "   Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: CORS Preflight Test
Write-Host "`n7. Testing CORS Configuration..." -ForegroundColor Yellow
try {
    $headers = @{
        "Origin" = "https://aml-app.onrender.com"
        "Access-Control-Request-Method" = "GET"
        "Access-Control-Request-Headers" = "Content-Type"
    }
    $corsResponse = Invoke-WebRequest -Uri "$baseUrl/api/currency?base=USD" -Method OPTIONS -Headers $headers -TimeoutSec 10
    Write-Host "PASS: CORS preflight working" -ForegroundColor Green
    Write-Host "   Status: $($corsResponse.StatusCode)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: CORS preflight error" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=====================================" -ForegroundColor Green
Write-Host "Deployed API Test Complete!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

Write-Host "`nDiagnostic Information:" -ForegroundColor Cyan
Write-Host "- Base URL: $baseUrl" -ForegroundColor White
Write-Host "- All APIs should return 200 OK or proper error messages" -ForegroundColor White
Write-Host "- 500 errors indicate server-side issues" -ForegroundColor White
Write-Host "- Check Render logs for detailed error information" -ForegroundColor White 