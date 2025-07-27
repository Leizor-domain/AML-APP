# Comprehensive Deployment Check Script
# This script performs detailed diagnostics to identify deployment issues

Write-Host "Comprehensive Deployment Check" -ForegroundColor Green
Write-Host "=============================" -ForegroundColor Green

$baseUrl = "https://aml-admin.onrender.com"

# Test 1: Check if the service is accessible at all
Write-Host "`n1. Basic Service Accessibility..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl" -Method GET -TimeoutSec 15
    Write-Host "PASS: Service is accessible (Status: $($response.StatusCode))" -ForegroundColor Green
    Write-Host "   Content Length: $($response.Content.Length) bytes" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Service is not accessible" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "   Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 2: Check if it's a Spring Boot application
Write-Host "`n2. Spring Boot Application Check..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/actuator/health" -Method GET -TimeoutSec 10
    Write-Host "PASS: Spring Boot Actuator health endpoint accessible" -ForegroundColor Green
    Write-Host "   Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "INFO: Spring Boot Actuator not available (this is normal)" -ForegroundColor Yellow
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# Test 3: Check our custom health endpoint
Write-Host "`n3. Custom Health Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/admin/health" -Method GET -TimeoutSec 15
    Write-Host "PASS: Custom health endpoint working" -ForegroundColor Green
    Write-Host "   Response: $response" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Custom health endpoint failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "   Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 4: Check database health specifically
Write-Host "`n4. Database Health Check..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/admin/db-health" -Method GET -TimeoutSec 20
    Write-Host "PASS: Database health check working" -ForegroundColor Green
    Write-Host "   Status: $($response.status)" -ForegroundColor Gray
    Write-Host "   Message: $($response.message)" -ForegroundColor Gray
    Write-Host "   User Count: $($response.userCount)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Database health check failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "   Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "   This suggests a database connection or application startup issue" -ForegroundColor Yellow
    }
}

# Test 5: Check if external API services are working
Write-Host "`n5. External API Services..." -ForegroundColor Yellow

# Test Currency API
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/currency?base=USD" -Method GET -TimeoutSec 20
    Write-Host "PASS: Currency API working" -ForegroundColor Green
    Write-Host "   Base: $($response.base)" -ForegroundColor Gray
    Write-Host "   Rates Count: $($response.rates.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Currency API failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "   Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test Stock Market API
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/stocks/AAPL" -Method GET -TimeoutSec 20
    Write-Host "PASS: Stock Market API working" -ForegroundColor Green
    Write-Host "   Symbol: $($response.symbol)" -ForegroundColor Gray
    Write-Host "   Data Points: $($response.data.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Stock Market API failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "   Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 6: Check if it's a CORS issue
Write-Host "`n6. CORS Configuration Check..." -ForegroundColor Yellow
try {
    $headers = @{
        "Origin" = "https://aml-app.onrender.com"
        "Access-Control-Request-Method" = "GET"
        "Access-Control-Request-Headers" = "Content-Type"
    }
    $response = Invoke-WebRequest -Uri "$baseUrl/admin/health" -Method OPTIONS -Headers $headers -TimeoutSec 10
    Write-Host "PASS: CORS preflight request successful" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Gray
} catch {
    Write-Host "INFO: CORS preflight failed (may be normal)" -ForegroundColor Yellow
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
}

Write-Host "`n=======================================" -ForegroundColor Green
Write-Host "Comprehensive Check Complete!" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green

Write-Host "`nAnalysis:" -ForegroundColor Cyan
Write-Host "If all endpoints return 500 errors, the issue is likely:" -ForegroundColor White
Write-Host "1. Application startup failure (missing dependencies, configuration)" -ForegroundColor White
Write-Host "2. Database connection issues (environment variables)" -ForegroundColor White
Write-Host "3. Build/deployment failure (check Render logs)" -ForegroundColor White
Write-Host "4. Missing service beans or autowiring issues" -ForegroundColor White

Write-Host "`nRecommended Actions:" -ForegroundColor Cyan
Write-Host "1. Check Render dashboard for deployment status" -ForegroundColor White
Write-Host "2. Review build logs for compilation errors" -ForegroundColor White
Write-Host "3. Verify environment variables are set correctly" -ForegroundColor White
Write-Host "4. Check if all required services are running" -ForegroundColor White
Write-Host "5. Consider restarting the service if deployment is stuck" -ForegroundColor White

Write-Host "`nExpected Timeline:" -ForegroundColor Cyan
Write-Host "- Deployment should complete within 5-10 minutes" -ForegroundColor White
Write-Host "- If still failing after 15 minutes, there's likely a configuration issue" -ForegroundColor White 