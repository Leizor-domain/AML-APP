# Test Mock Alert Population Script
# This script tests the mock alert generation endpoints

Write-Host "=== AML Mock Alert Population Test ===" -ForegroundColor Green
Write-Host ""

# Base URL for the API
$baseUrl = "http://localhost:8080"

# Function to make HTTP requests
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body = $null
    )
    
    try {
        $headers = @{
            "Content-Type" = "application/json"
        }
        
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $Body
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
        }
        
        return $response
    }
    catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Test 1: Get current alert count
Write-Host "1. Getting current alert count..." -ForegroundColor Yellow
$countResponse = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/alerts/count"
if ($countResponse) {
    Write-Host "   Current alert count: $($countResponse.count)" -ForegroundColor Cyan
} else {
    Write-Host "   Failed to get alert count" -ForegroundColor Red
}
Write-Host ""

# Test 2: Populate mock alerts
Write-Host "2. Populating 70 mock alerts..." -ForegroundColor Yellow
$populateResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/alerts/populate-mock"
if ($populateResponse) {
    Write-Host "   Success: $($populateResponse.message)" -ForegroundColor Green
    Write-Host "   Previous count: $($populateResponse.previousCount)" -ForegroundColor Cyan
    Write-Host "   New count: $($populateResponse.newCount)" -ForegroundColor Cyan
    Write-Host "   Added: $($populateResponse.addedCount)" -ForegroundColor Cyan
} else {
    Write-Host "   Failed to populate mock alerts" -ForegroundColor Red
}
Write-Host ""

# Test 3: Verify new count
Write-Host "3. Verifying new alert count..." -ForegroundColor Yellow
$newCountResponse = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/alerts/count"
if ($newCountResponse) {
    Write-Host "   Current alert count: $($newCountResponse.count)" -ForegroundColor Cyan
} else {
    Write-Host "   Failed to get new alert count" -ForegroundColor Red
}
Write-Host ""

# Test 4: Get all alerts (first 10)
Write-Host "4. Getting first 10 alerts..." -ForegroundColor Yellow
$alertsResponse = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/alerts"
if ($alertsResponse) {
    $first10Alerts = $alertsResponse | Select-Object -First 10
    Write-Host "   Retrieved $($first10Alerts.Count) alerts:" -ForegroundColor Cyan
    foreach ($alert in $first10Alerts) {
        Write-Host "   - ID: $($alert.id), Type: $($alert.alertType), Entity: $($alert.matchedEntityName), Priority: $($alert.priorityLevel)" -ForegroundColor White
    }
} else {
    Write-Host "   Failed to retrieve alerts" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "To clear all alerts, run: Invoke-RestMethod -Uri '$baseUrl/alerts/clear-all' -Method DELETE" -ForegroundColor Magenta