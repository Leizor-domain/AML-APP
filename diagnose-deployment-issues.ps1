# Diagnostic Script for Render Deployment Issues
# This script helps identify what's causing the 500 errors

Write-Host "Diagnosing Render Deployment Issues..." -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green

$baseUrl = "https://aml-admin.onrender.com"

# Test 1: Check if service is responding at all
Write-Host "`n1. Testing Basic Connectivity..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl" -Method GET -TimeoutSec 10
    Write-Host "PASS: Service is responding (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Service is not responding" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Check if it's a database connection issue
Write-Host "`n2. Testing Database Health..." -ForegroundColor Yellow
try {
    $dbResponse = Invoke-RestMethod -Uri "$baseUrl/admin/db-health" -Method GET -TimeoutSec 15
    Write-Host "PASS: Database health check working" -ForegroundColor Green
    Write-Host "   Response: $($dbResponse | ConvertTo-Json)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Database health check failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 3: Check if it's a specific API issue
Write-Host "`n3. Testing Simple Endpoint..." -ForegroundColor Yellow
try {
    $simpleResponse = Invoke-RestMethod -Uri "$baseUrl/admin/health" -Method GET -TimeoutSec 15
    Write-Host "PASS: Simple health endpoint working" -ForegroundColor Green
    Write-Host "   Response: $($simpleResponse | ConvertTo-Json)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Simple health endpoint failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 4: Check if it's an external API dependency issue
Write-Host "`n4. Testing External API Dependencies..." -ForegroundColor Yellow
try {
    $currencyResponse = Invoke-RestMethod -Uri "$baseUrl/api/currency?base=USD" -Method GET -TimeoutSec 20
    Write-Host "PASS: Currency API working" -ForegroundColor Green
    Write-Host "   Base: $($currencyResponse.base)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Currency API failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Green
Write-Host "Diagnosis Complete!" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green

Write-Host "`nPossible Issues:" -ForegroundColor Yellow
Write-Host "1. Deployment not completed yet - Check Render dashboard" -ForegroundColor White
Write-Host "2. Database connection issues - Check environment variables" -ForegroundColor White
Write-Host "3. Missing dependencies - Check if all services are deployed" -ForegroundColor White
Write-Host "4. Build failures - Check Render build logs" -ForegroundColor White
Write-Host "5. Environment variable mismatch - Check render.yaml configuration" -ForegroundColor White

Write-Host "`nNext Steps:" -ForegroundColor Cyan
Write-Host "1. Check Render dashboard for deployment status" -ForegroundColor White
Write-Host "2. Check build logs for any errors" -ForegroundColor White
Write-Host "3. Verify environment variables are set correctly" -ForegroundColor White
Write-Host "4. Check if all services (aml-admin, aml-portal, aml-engine) are running" -ForegroundColor White 