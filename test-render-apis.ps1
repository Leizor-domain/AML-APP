# Test Render Deployed APIs
# Tests the deployed backend services on Render

Write-Host "Testing Render Deployed APIs..." -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

# Test 1: Check if AML Admin service is running
Write-Host "`n1. Checking if AML Admin service is running..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "https://aml-admin.onrender.com/admin/health" -Method GET -TimeoutSec 10
    Write-Host "PASS: AML Admin service is running" -ForegroundColor Green
    Write-Host "   Response: $($healthResponse)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: AML Admin service is not responding" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 2: Currency Converter API
Write-Host "`n2. Testing Currency Converter API..." -ForegroundColor Yellow
try {
    $ratesResponse = Invoke-RestMethod -Uri "https://aml-admin.onrender.com/api/currency?base=USD&symbols=EUR,GBP" -Method GET -TimeoutSec 15
    Write-Host "PASS: Currency rates endpoint working" -ForegroundColor Green
    Write-Host "   Base: $($ratesResponse.base)" -ForegroundColor Gray
    Write-Host "   Rates count: $($ratesResponse.rates.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Currency Converter API error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 3: Stock Market API
Write-Host "`n3. Testing Stock Market API..." -ForegroundColor Yellow
try {
    $stockResponse = Invoke-RestMethod -Uri "https://aml-admin.onrender.com/api/stocks/AAPL" -Method GET -TimeoutSec 15
    Write-Host "PASS: Stock market API working" -ForegroundColor Green
    Write-Host "   Symbol: $($stockResponse.symbol)" -ForegroundColor Gray
    Write-Host "   Data points: $($stockResponse.data.Count)" -ForegroundColor Gray
    if ($stockResponse.data.Count -gt 0) {
        Write-Host "   Latest price: $($stockResponse.data[0].close)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAIL: Stock Market API error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 4: User Management API
Write-Host "`n4. Testing User Management API..." -ForegroundColor Yellow
try {
    $usersResponse = Invoke-RestMethod -Uri "https://aml-admin.onrender.com/admin/users" -Method GET -TimeoutSec 15
    Write-Host "PASS: User management endpoint working" -ForegroundColor Green
    Write-Host "   Users count: $($usersResponse.Count)" -ForegroundColor Gray
    if ($usersResponse.Count -gt 0) {
        Write-Host "   First user: $($usersResponse[0].username) - $($usersResponse[0].role)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAIL: User Management API error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 5: Database Health
Write-Host "`n5. Testing Database Health..." -ForegroundColor Yellow
try {
    $dbResponse = Invoke-RestMethod -Uri "https://aml-admin.onrender.com/admin/db-health" -Method GET -TimeoutSec 15
    Write-Host "PASS: Database health endpoint working" -ForegroundColor Green
    Write-Host "   Status: $($dbResponse.status)" -ForegroundColor Gray
    Write-Host "   User count: $($dbResponse.userCount)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Database health endpoint error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: CORS Headers
Write-Host "`n6. Testing CORS Configuration..." -ForegroundColor Yellow
try {
    $corsResponse = Invoke-WebRequest -Uri "https://aml-admin.onrender.com/api/currency" -Method OPTIONS -TimeoutSec 10
    Write-Host "PASS: CORS preflight request successful" -ForegroundColor Green
    Write-Host "   Access-Control-Allow-Origin: $($corsResponse.Headers['Access-Control-Allow-Origin'])" -ForegroundColor Gray
    Write-Host "   Access-Control-Allow-Methods: $($corsResponse.Headers['Access-Control-Allow-Methods'])" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: CORS configuration error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Frontend Accessibility
Write-Host "`n7. Testing Frontend Accessibility..." -ForegroundColor Yellow
try {
    $frontendResponse = Invoke-WebRequest -Uri "https://aml-app.onrender.com" -Method GET -TimeoutSec 10
    Write-Host "PASS: Frontend is accessible" -ForegroundColor Green
    Write-Host "   Status: $($frontendResponse.StatusCode)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Frontend is not accessible" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=================================" -ForegroundColor Green
Write-Host "Render API Test Complete!" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green 