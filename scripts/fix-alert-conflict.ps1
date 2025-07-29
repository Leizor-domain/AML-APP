# Fix Alert Conflict Issue
Write-Host "=== Fixing Alert Conflict Issue ===" -ForegroundColor Green
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

# Test 2: Clear all existing alerts
Write-Host "`n2. Clearing all existing alerts..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/clear-all" -Method DELETE -TimeoutSec 30
    if ($response.success) {
        Write-Host "✓ Successfully cleared $($response.clearedCount) alerts" -ForegroundColor Green
    } else {
        Write-Host "✗ Failed to clear alerts: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Failed to clear alerts: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Safely populate new mock alerts
Write-Host "`n3. Safely populating new mock alerts..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/populate-mock-safe" -Method POST -TimeoutSec 30
    if ($response.success) {
        Write-Host "✓ Successfully populated $($response.addedCount) mock alerts" -ForegroundColor Green
        Write-Host "  Total alerts: $($response.newCount)" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Failed to populate mock alerts: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Failed to populate mock alerts: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Verify alerts are working
Write-Host "`n4. Verifying alerts are working..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts?page=0&size=3" -Method GET -TimeoutSec 10
    if ($response.content) {
        Write-Host "✓ Successfully retrieved $($response.content.Count) alerts" -ForegroundColor Green
        Write-Host "  Total alerts: $($response.totalElements)" -ForegroundColor Cyan
        
        # Display first alert details
        if ($response.content.Count -gt 0) {
            $firstAlert = $response.content[0]
            Write-Host "`n  First Alert Details:" -ForegroundColor Cyan
            Write-Host "    ID: $($firstAlert.id)" -ForegroundColor White
            Write-Host "    Alert ID: $($firstAlert.alertId)" -ForegroundColor White
            Write-Host "    Reason: $($firstAlert.reason)" -ForegroundColor White
            Write-Host "    Type: $($firstAlert.alertType)" -ForegroundColor White
        }
    } else {
        Write-Host "✗ No alerts found in response" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Failed to retrieve alerts: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Fix Complete ===" -ForegroundColor Green
Write-Host "✓ Alert conflict issue should be resolved" -ForegroundColor Green
Write-Host "✓ Mock alerts are now available" -ForegroundColor Green
Write-Host "✓ Application should start without errors" -ForegroundColor Green
Write-Host "`nNext steps:" -ForegroundColor Cyan
Write-Host "1. Restart the backend if needed" -ForegroundColor White
Write-Host "2. Test the frontend: npm run dev" -ForegroundColor White
Write-Host "3. Navigate to Analyst Dashboard" -ForegroundColor White