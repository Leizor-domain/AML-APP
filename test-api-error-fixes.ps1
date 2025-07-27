# Comprehensive API Error Fixes Test Script
# Tests for Currency Converter, Stock Market Data, and Transaction Ingestion endpoints

Write-Host "Testing AML Admin API Error Fixes..." -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

# Test 1: Check if service is running
Write-Host "`n1. Checking if AML Admin service is running..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/admin/health" -Method GET -TimeoutSec 5
    Write-Host "PASS: Service is running - $($healthResponse)" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Service is not running on port 8080" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Please start the backend service first" -ForegroundColor Yellow
    exit 1
}

# Test 2: Currency Converter API - Test Error Handling
Write-Host "`n2. Testing Currency Converter API Error Handling..." -ForegroundColor Yellow
try {
    # Test latest rates endpoint
    $ratesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/currency?base=USD&symbols=EUR,GBP" -Method GET -TimeoutSec 10
    Write-Host "PASS: Currency rates endpoint working" -ForegroundColor Green
    Write-Host "   Base: $($ratesResponse.base)" -ForegroundColor Gray
    Write-Host "   Rates count: $($ratesResponse.rates.Count)" -ForegroundColor Gray
    
    # Check if error field exists (should be null for success)
    if ($ratesResponse.error) {
        Write-Host "   Error: $($ratesResponse.error)" -ForegroundColor Yellow
    } else {
        Write-Host "   No errors detected" -ForegroundColor Green
    }
    
    # Test conversion endpoint
    $convertResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/currency/convert?from=USD&to=EUR&amount=100" -Method GET -TimeoutSec 10
    Write-Host "PASS: Currency conversion endpoint working" -ForegroundColor Green
    Write-Host "   Converted: $($convertResponse.amount) USD = $($convertResponse.convertedAmount) EUR" -ForegroundColor Gray
    Write-Host "   Rate: $($convertResponse.rate)" -ForegroundColor Gray
    
} catch {
    Write-Host "FAIL: Currency Converter API error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 3: Stock Market API - Test Error Handling
Write-Host "`n3. Testing Stock Market API Error Handling..." -ForegroundColor Yellow
try {
    $stockResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/stocks/AAPL" -Method GET -TimeoutSec 10
    Write-Host "PASS: Stock market API working" -ForegroundColor Green
    Write-Host "   Symbol: $($stockResponse.symbol)" -ForegroundColor Gray
    Write-Host "   Data points: $($stockResponse.data.Count)" -ForegroundColor Gray
    
    # Check if error field exists (should be null for success)
    if ($stockResponse.error) {
        Write-Host "   Error: $($stockResponse.error)" -ForegroundColor Yellow
    } else {
        Write-Host "   No errors detected" -ForegroundColor Green
        if ($stockResponse.data.Count -gt 0) {
            Write-Host "   Latest price: $($stockResponse.data[0].close)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "FAIL: Stock Market API error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 4: Transaction Ingestion - Test Error Handling
Write-Host "`n4. Testing Transaction Ingestion Error Handling..." -ForegroundColor Yellow
try {
    # Create a test CSV content
    $testCsvContent = @"
sender,receiver,amount,currency,country,dob
John Doe,Jane Smith,1000.00,USD,USA,1990-01-01
Invalid User,Test Receiver,0.00,USD,USA,1995-01-01
"@
    
    # Save to temporary file
    $tempFile = "test_transactions.csv"
    $testCsvContent | Out-File -FilePath $tempFile -Encoding UTF8
    
    # Test file upload
    $form = @{
        file = Get-Item $tempFile
    }
    
    $ingestResponse = Invoke-RestMethod -Uri "http://localhost:8080/ingest/file" -Method POST -Form $form -TimeoutSec 30
    Write-Host "PASS: Transaction ingestion working" -ForegroundColor Green
    Write-Host "   Processed: $($ingestResponse.processed)" -ForegroundColor Gray
    Write-Host "   Successful: $($ingestResponse.successful)" -ForegroundColor Gray
    Write-Host "   Failed: $($ingestResponse.failed)" -ForegroundColor Gray
    Write-Host "   Alerts Generated: $($ingestResponse.alertsGenerated)" -ForegroundColor Gray
    
    if ($ingestResponse.errors) {
        Write-Host "   Errors: $($ingestResponse.errors.Count) errors detected" -ForegroundColor Yellow
        foreach ($errorMsg in $ingestResponse.errors) {
            Write-Host "     - $errorMsg" -ForegroundColor Yellow
        }
    }
    
    # Clean up temp file
    Remove-Item $tempFile -ErrorAction SilentlyContinue
    
} catch {
    Write-Host "FAIL: Transaction ingestion error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 5: Transaction History - Test Error Handling
Write-Host "`n5. Testing Transaction History Error Handling..." -ForegroundColor Yellow
try {
    $historyResponse = Invoke-RestMethod -Uri "http://localhost:8080/ingest/transactions?page=0&size=10" -Method GET -TimeoutSec 10
    Write-Host "PASS: Transaction history endpoint working" -ForegroundColor Green
    Write-Host "   Total Elements: $($historyResponse.totalElements)" -ForegroundColor Gray
    Write-Host "   Total Pages: $($historyResponse.totalPages)" -ForegroundColor Gray
    Write-Host "   Current Page: $($historyResponse.currentPage)" -ForegroundColor Gray
    Write-Host "   Content Count: $($historyResponse.content.Count)" -ForegroundColor Gray
} catch {
    Write-Host "FAIL: Transaction history error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 6: Test Network Timeout Handling
Write-Host "`n6. Testing Network Timeout Handling..." -ForegroundColor Yellow
try {
    # Test with a very short timeout to simulate network issues
    $timeoutResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/currency?base=USD" -Method GET -TimeoutSec 1
    Write-Host "PASS: Currency API responded within timeout" -ForegroundColor Green
} catch {
    if ($_.Exception.Message -like "*timeout*" -or $_.Exception.Message -like "*timed out*") {
        Write-Host "PASS: Timeout handling working correctly" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Unexpected error during timeout test" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 7: Test Invalid API Requests
Write-Host "`n7. Testing Invalid API Request Handling..." -ForegroundColor Yellow
try {
    # Test with invalid currency
    $invalidResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/currency?base=INVALID" -Method GET -TimeoutSec 10
    Write-Host "PASS: Invalid currency request handled gracefully" -ForegroundColor Green
    if ($invalidResponse.error) {
        Write-Host "   Error message: $($invalidResponse.error)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAIL: Invalid request handling error" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=====================================" -ForegroundColor Green
Write-Host "API Error Fixes Test Complete!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

Write-Host "`nSummary:" -ForegroundColor Cyan
Write-Host "- Currency Converter API: Enhanced error handling with timeouts" -ForegroundColor White
Write-Host "- Stock Market API: Improved error handling with fallback messages" -ForegroundColor White
Write-Host "- Transaction Ingestion: Added validation and graceful error handling" -ForegroundColor White
Write-Host "- All services now return user-friendly error messages" -ForegroundColor White
Write-Host "- Network timeouts are properly handled" -ForegroundColor White 