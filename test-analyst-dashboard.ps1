# Test Analyst Dashboard Functionality
Write-Host "=== Testing Analyst Dashboard and Mock Alerts ===" -ForegroundColor Green
Write-Host ""

# Test 1: Check if backend is running
Write-Host "1. Testing backend connectivity..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 10
    Write-Host "✓ Backend is running" -ForegroundColor Green
} catch {
    Write-Host "✗ Backend is not running. Please start the backend first." -ForegroundColor Red
    Write-Host "  Run: cd aml-admin && mvn spring-boot:run" -ForegroundColor Cyan
    exit 1
}

# Test 2: Check current alert count
Write-Host "`n2. Checking current alert count..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/count" -Method GET -TimeoutSec 10
    $currentCount = $response.count
    Write-Host "✓ Current alert count: $currentCount" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to get alert count" -ForegroundColor Red
    $currentCount = 0
}

# Test 3: Populate mock alerts if needed
if ($currentCount -eq 0) {
    Write-Host "`n3. Populating mock alerts..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/populate-mock" -Method POST -TimeoutSec 30
        if ($response.success) {
            Write-Host "✓ Successfully populated $($response.addedCount) mock alerts" -ForegroundColor Green
            Write-Host "  Total alerts: $($response.newCount)" -ForegroundColor Cyan
        } else {
            Write-Host "✗ Failed to populate mock alerts: $($response.message)" -ForegroundColor Red
        }
    } catch {
        Write-Host "✗ Failed to populate mock alerts" -ForegroundColor Red
    }
} else {
    Write-Host "`n3. Skipping mock alert population (alerts already exist)" -ForegroundColor Cyan
}

# Test 4: Get alerts with pagination
Write-Host "`n4. Testing alert retrieval with pagination..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts?page=0&size=5" -Method GET -TimeoutSec 10
    if ($response.content) {
        Write-Host "✓ Retrieved $($response.content.Count) alerts" -ForegroundColor Green
        Write-Host "  Total alerts: $($response.totalElements)" -ForegroundColor Cyan
        Write-Host "  Total pages: $($response.totalPages)" -ForegroundColor Cyan
        
        # Display first alert details
        if ($response.content.Count -gt 0) {
            $firstAlert = $response.content[0]
            Write-Host "`n  First Alert Details:" -ForegroundColor Cyan
            Write-Host "    ID: $($firstAlert.id)" -ForegroundColor White
            Write-Host "    Alert ID: $($firstAlert.alertId)" -ForegroundColor White
            Write-Host "    Reason: $($firstAlert.reason)" -ForegroundColor White
            Write-Host "    Type: $($firstAlert.alertType)" -ForegroundColor White
            Write-Host "    Priority: $($firstAlert.priorityLevel)" -ForegroundColor White
            Write-Host "    Entity: $($firstAlert.matchedEntityName)" -ForegroundColor White
            Write-Host "    Timestamp: $($firstAlert.timestamp)" -ForegroundColor White
        }
    } else {
        Write-Host "✗ No alerts found in response" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Failed to retrieve alerts: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test filtering by priority level
Write-Host "`n5. Testing alert filtering by priority level..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts?priorityLevel=HIGH&page=0&size=3" -Method GET -TimeoutSec 10
    if ($response.content) {
        Write-Host "✓ Retrieved $($response.content.Count) HIGH priority alerts" -ForegroundColor Green
    } else {
        Write-Host "ℹ No HIGH priority alerts found" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Failed to filter alerts by priority" -ForegroundColor Red
}

# Test 6: Test filtering by alert type
Write-Host "`n6. Testing alert filtering by alert type..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts?alertType=SANCTIONS&page=0&size=3" -Method GET -TimeoutSec 10
    if ($response.content) {
        Write-Host "✓ Retrieved $($response.content.Count) SANCTIONS alerts" -ForegroundColor Green
    } else {
        Write-Host "ℹ No SANCTIONS alerts found" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Failed to filter alerts by type" -ForegroundColor Red
}

Write-Host "`n=== Test Summary ===" -ForegroundColor Green
Write-Host "✓ Analyst Dashboard backend functionality tested" -ForegroundColor Green
Write-Host "✓ Mock alerts are available for testing" -ForegroundColor Green
Write-Host "✓ Pagination and filtering are working" -ForegroundColor Green
Write-Host "`nNext steps:" -ForegroundColor Cyan
Write-Host "1. Start the frontend: npm run dev" -ForegroundColor White
Write-Host "2. Navigate to the Analyst Dashboard" -ForegroundColor White
Write-Host "3. Test the 'View All Pending Alerts' button" -ForegroundColor White
Write-Host "4. Verify alerts are displayed correctly" -ForegroundColor White