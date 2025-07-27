# Test Deployment Status
# This script checks if the deployment is working at all

Write-Host "Testing Deployment Status..." -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

$baseUrl = "https://aml-admin.onrender.com"

# Test 1: Basic connectivity
Write-Host "`n1. Testing Basic Connectivity..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl" -Method GET -TimeoutSec 10
    Write-Host "SUCCESS: Service is responding" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Cyan
    Write-Host "Response Length: $($response.Content.Length) characters" -ForegroundColor Cyan
} catch {
    Write-Host "FAIL: Service is not responding" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 2: Simple GET request to health endpoint
Write-Host "`n2. Testing Simple Health Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/admin/db-health" -Method GET -TimeoutSec 10
    Write-Host "SUCCESS: Health endpoint is responding" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Cyan
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "FAIL: Health endpoint failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 3: Test with curl-like approach
Write-Host "`n3. Testing with curl-like approach..." -ForegroundColor Yellow
try {
    $headers = @{
        "Accept" = "application/json"
        "Content-Type" = "application/json"
    }
    
    $response = Invoke-RestMethod -Uri "$baseUrl/admin/db-health" -Method GET -Headers $headers -TimeoutSec 10
    Write-Host "SUCCESS: curl-like request worked" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json)" -ForegroundColor Cyan
} catch {
    Write-Host "FAIL: curl-like request failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Response: $($_.Exception.Response.Content)" -ForegroundColor Red
    }
}

# Test 4: Check if it's a CORS issue
Write-Host "`n4. Testing CORS Preflight..." -ForegroundColor Yellow
try {
    $headers = @{
        "Origin" = "https://aml-app.onrender.com"
        "Access-Control-Request-Method" = "GET"
        "Access-Control-Request-Headers" = "Content-Type"
    }
    
    $response = Invoke-WebRequest -Uri "$baseUrl/admin/db-health" -Method OPTIONS -Headers $headers -TimeoutSec 10
    Write-Host "SUCCESS: CORS preflight worked" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Cyan
} catch {
    Write-Host "FAIL: CORS preflight failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host "`n=====================================" -ForegroundColor Green
Write-Host "Deployment Status Test Complete!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

Write-Host "`nAnalysis:" -ForegroundColor Yellow
Write-Host "- If all tests fail, the service may not be deployed or is down" -ForegroundColor White
Write-Host "- If some tests work but others fail, there may be a configuration issue" -ForegroundColor White
Write-Host "- If CORS fails, there may be a CORS configuration problem" -ForegroundColor White
Write-Host "- Check Render dashboard for deployment status and logs" -ForegroundColor White 