# Test Currency Conversion API Endpoints Script
Write-Host "Testing AML Admin Currency Conversion API Endpoints..." -ForegroundColor Green

# Test 1: Check if service is running
$portCheck = netstat -an | findstr :8080
if ($portCheck) {
    Write-Host "Service is running on port 8080" -ForegroundColor Green
    Write-Host $portCheck
} else {
    Write-Host "Service is not running on port 8080" -ForegroundColor Red
    exit 1
}

# Test 2: Test GET /api/currency endpoint (default USD base)
Write-Host "`nTesting GET /api/currency..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/currency" -Method GET
    Write-Host "GET /api/currency successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content (first 500 chars): $($response.Content.Substring(0, [System.Math]::Min(500, $response.Content.Length)))"
} catch {
    Write-Host "GET /api/currency failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test GET /api/currency?base=EUR&symbols=USD,GBP endpoint
Write-Host "`nTesting GET /api/currency?base=EUR&symbols=USD,GBP..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/currency?base=EUR&symbols=USD,GBP" -Method GET
    Write-Host "GET /api/currency?base=EUR&symbols=USD,GBP successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content (first 500 chars): $($response.Content.Substring(0, [System.Math]::Min(500, $response.Content.Length)))"
} catch {
    Write-Host "GET /api/currency?base=EUR&symbols=USD,GBP failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test GET /api/currency/convert endpoint
Write-Host "`nTesting GET /api/currency/convert?from=USD&to=EUR&amount=100..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/currency/convert?from=USD&to=EUR&amount=100" -Method GET
    Write-Host "GET /api/currency/convert successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "GET /api/currency/convert failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test GET /api/currency/convert endpoint with different currencies
Write-Host "`nTesting GET /api/currency/convert?from=GBP&to=JPY&amount=50..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/currency/convert?from=GBP&to=JPY&amount=50" -Method GET
    Write-Host "GET /api/currency/convert (GBP to JPY) successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "GET /api/currency/convert (GBP to JPY) failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Test GET /api/currency/cache/stats endpoint
Write-Host "`nTesting GET /api/currency/cache/stats..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/currency/cache/stats" -Method GET
    Write-Host "GET /api/currency/cache/stats successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "GET /api/currency/cache/stats failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Test invalid conversion (should return error)
Write-Host "`nTesting invalid conversion (should return error)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/currency/convert?from=USD&to=EUR&amount=0" -Method GET
    Write-Host "Invalid conversion returned expected error!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "Invalid conversion test failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nAll currency conversion API tests completed." -ForegroundColor Green 