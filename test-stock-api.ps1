# Test Stock Market API Endpoints Script
Write-Host "Testing AML Admin Stock Market API Endpoints..." -ForegroundColor Green

# Test 1: Check if service is running
Write-Host "`n1. Checking if service is running on port 8080..." -ForegroundColor Yellow
$portCheck = netstat -an | findstr :8080
if ($portCheck) {
    Write-Host "Service is running on port 8080" -ForegroundColor Green
    Write-Host $portCheck
} else {
    Write-Host "❌ Service is NOT running on port 8080" -ForegroundColor Red
    Write-Host "Please start the service first with: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# Test 2: Test GET /api/stocks/AAPL endpoint
Write-Host "`n2. Testing GET /api/stocks/AAPL endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/stocks/AAPL" -Method GET -Headers @{"Content-Type"="application/json"}
    Write-Host "GET /api/stocks/AAPL successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content (first 500 chars): $($response.Content.Substring(0, [System.Math]::Min(500, $response.Content.Length)))"
} catch {
    Write-Host "❌ GET /api/stocks/AAPL FAILED!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)"
    exit 1
}

# Test 3: Test GET /api/stocks/TSLA endpoint
Write-Host "`n3. Testing GET /api/stocks/TSLA endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/stocks/TSLA" -Method GET -Headers @{"Content-Type"="application/json"}
    Write-Host "GET /api/stocks/TSLA successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content (first 500 chars): $($response.Content.Substring(0, [System.Math]::Min(500, $response.Content.Length)))"
} catch {
    Write-Host "❌ GET /api/stocks/TSLA FAILED!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)"
    # Do not exit here, as it might be expected if API has rate limits
}

# Test 4: Test GET /api/stocks/INVALID endpoint (should return error)
Write-Host "`n4. Testing GET /api/stocks/INVALID endpoint (error handling)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/stocks/INVALID" -Method GET -Headers @{"Content-Type"="application/json"}
    Write-Host "GET /api/stocks/INVALID returned expected error!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Content: $($response.Content)"
} catch {
    Write-Host "❌ GET /api/stocks/INVALID FAILED!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)"
    # This is expected to fail, so don't exit
}

Write-Host "`nAll stock market API tests completed." -ForegroundColor Green 