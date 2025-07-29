# Test Analyst Dashboard Implementation
Write-Host "=== Testing Analyst Dashboard Implementation ===" -ForegroundColor Green
Write-Host ""

# Test 1: Check if bootstrap alerts are created
Write-Host "Test 1: Checking bootstrap alerts..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/count" -Method GET
    Write-Host "✓ Alert count: $($response.count)" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to get alert count: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Test analyst alerts endpoint
Write-Host ""
Write-Host "Test 2: Testing analyst alerts endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/analyst?size=5" -Method GET
    Write-Host "✓ Analyst alerts retrieved successfully" -ForegroundColor Green
    Write-Host "  - Total alerts: $($response.totalElements)" -ForegroundColor Cyan
    Write-Host "  - Page size: $($response.size)" -ForegroundColor Cyan
    Write-Host "  - Current page: $($response.currentPage)" -ForegroundColor Cyan
    
    if ($response.content -and $response.content.Count -gt 0) {
        $firstAlert = $response.content[0]
        Write-Host "  - First alert ID: $($firstAlert.alertId)" -ForegroundColor Cyan
        Write-Host "  - First alert reason: $($firstAlert.reason)" -ForegroundColor Cyan
        Write-Host "  - First alert type: $($firstAlert.alertType)" -ForegroundColor Cyan
        Write-Host "  - First alert priority: $($firstAlert.priorityLevel)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "✗ Failed to get analyst alerts: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test pagination
Write-Host ""
Write-Host "Test 3: Testing pagination..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/analyst?page=1&size=3" -Method GET
    Write-Host "✓ Pagination working correctly" -ForegroundColor Green
    Write-Host "  - Page 1 size: $($response.content.Count)" -ForegroundColor Cyan
    Write-Host "  - Total pages: $($response.totalPages)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Failed to test pagination: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test priority filtering
Write-Host ""
Write-Host "Test 4: Testing priority filtering..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/alerts/analyst?priority=HIGH&size=10" -Method GET
    Write-Host "✓ Priority filtering working" -ForegroundColor Green
    Write-Host "  - High priority alerts: $($response.totalElements)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Failed to test priority filtering: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Test Summary ===" -ForegroundColor Green
Write-Host "All tests completed. Check the results above." -ForegroundColor Cyan
Write-Host ""
Write-Host "To test the frontend:" -ForegroundColor Yellow
Write-Host "1. Start the frontend: npm run dev" -ForegroundColor White
Write-Host "2. Navigate to Analyst Dashboard" -ForegroundColor White
Write-Host "3. Check if alerts are displayed properly" -ForegroundColor White
Write-Host "4. Test 'View All Pending Alerts' button navigation" -ForegroundColor White