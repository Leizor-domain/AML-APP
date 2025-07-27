# Test Batch Ingestion Logic
# This script tests the /ingest/file endpoint with various scenarios

Write-Host "Testing Batch Ingestion Logic..." -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

$baseUrl = "https://aml-admin.onrender.com"

# Test 1: Valid CSV file ingestion
Write-Host "`n1. Testing Valid CSV File Ingestion..." -ForegroundColor Yellow
try {
    # Create a simple CSV content for testing
    $csvContent = @"
transactionId,sender,recipient,amount,currency,country,dob
TXN001,John Doe,Jane Smith,1000.00,USD,USA,1980-01-01
TXN002,Jane Smith,Bob Johnson,2500.50,EUR,Germany,1985-05-15
TXN003,Bob Johnson,Alice Brown,750.25,GBP,UK,1990-12-20
"@

    # Save to temporary file
    $csvContent | Out-File -FilePath "test_transactions.csv" -Encoding UTF8

    # Test file upload
    $response = Invoke-RestMethod -Uri "$baseUrl/ingest/file" -Method POST -Form @{
        file = Get-Item "test_transactions.csv"
    } -ContentType "multipart/form-data"

    Write-Host "SUCCESS: Valid CSV ingestion" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    # Clean up
    Remove-Item "test_transactions.csv" -ErrorAction SilentlyContinue

} catch {
    Write-Host "FAIL: Valid CSV ingestion failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 2: Empty file handling
Write-Host "`n2. Testing Empty File Handling..." -ForegroundColor Yellow
try {
    # Create empty file
    "" | Out-File -FilePath "empty_file.csv" -Encoding UTF8

    $response = Invoke-RestMethod -Uri "$baseUrl/ingest/file" -Method POST -Form @{
        file = Get-Item "empty_file.csv"
    } -ContentType "multipart/form-data"

    Write-Host "SUCCESS: Empty file handled gracefully" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    Remove-Item "empty_file.csv" -ErrorAction SilentlyContinue

} catch {
    Write-Host "FAIL: Empty file handling failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Large file handling (over 10MB limit)
Write-Host "`n3. Testing Large File Handling..." -ForegroundColor Yellow
try {
    # Create a large file (simulate by creating many rows)
    $largeContent = ""
    for ($i = 1; $i -le 100000; $i++) {
        $largeContent += "TXN$i,User$i,Recipient$i,$(Get-Random -Minimum 100 -Maximum 10000).00,USD,USA,1980-01-01`n"
    }
    $largeContent | Out-File -FilePath "large_file.csv" -Encoding UTF8

    $fileSize = (Get-Item "large_file.csv").Length
    Write-Host "File size: $($fileSize / 1MB) MB" -ForegroundColor Cyan

    $response = Invoke-RestMethod -Uri "$baseUrl/ingest/file" -Method POST -Form @{
        file = Get-Item "large_file.csv"
    } -ContentType "multipart/form-data"

    Write-Host "SUCCESS: Large file handled gracefully" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    Remove-Item "large_file.csv" -ErrorAction SilentlyContinue

} catch {
    Write-Host "FAIL: Large file handling failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Invalid file type handling
Write-Host "`n4. Testing Invalid File Type Handling..." -ForegroundColor Yellow
try {
    # Create a text file with .txt extension
    "This is not a CSV file" | Out-File -FilePath "invalid_file.txt" -Encoding UTF8

    $response = Invoke-RestMethod -Uri "$baseUrl/ingest/file" -Method POST -Form @{
        file = Get-Item "invalid_file.txt"
    } -ContentType "multipart/form-data"

    Write-Host "SUCCESS: Invalid file type handled gracefully" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    Remove-Item "invalid_file.txt" -ErrorAction SilentlyContinue

} catch {
    Write-Host "FAIL: Invalid file type handling failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Malformed CSV data
Write-Host "`n5. Testing Malformed CSV Data..." -ForegroundColor Yellow
try {
    # Create CSV with missing required fields
    $malformedContent = @"
transactionId,sender,recipient,amount,currency,country,dob
TXN001,,Jane Smith,1000.00,USD,USA,1980-01-01
TXN002,Jane Smith,,2500.50,EUR,Germany,1985-05-15
TXN003,Bob Johnson,Alice Brown,,GBP,UK,1990-12-20
"@

    $malformedContent | Out-File -FilePath "malformed.csv" -Encoding UTF8

    $response = Invoke-RestMethod -Uri "$baseUrl/ingest/file" -Method POST -Form @{
        file = Get-Item "malformed.csv"
    } -ContentType "multipart/form-data"

    Write-Host "SUCCESS: Malformed CSV handled gracefully" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    Remove-Item "malformed.csv" -ErrorAction SilentlyContinue

} catch {
    Write-Host "FAIL: Malformed CSV handling failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Transaction history retrieval
Write-Host "`n6. Testing Transaction History Retrieval..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/ingest/transactions?page=0&size=10" -Method GET

    Write-Host "SUCCESS: Transaction history retrieved" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

} catch {
    Write-Host "FAIL: Transaction history retrieval failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Batch processing with mixed valid/invalid data
Write-Host "`n7. Testing Batch Processing with Mixed Data..." -ForegroundColor Yellow
try {
    # Create CSV with mix of valid and invalid transactions
    $mixedContent = @"
transactionId,sender,recipient,amount,currency,country,dob
TXN001,John Doe,Jane Smith,1000.00,USD,USA,1980-01-01
TXN002,,Bob Johnson,2500.50,EUR,Germany,1985-05-15
TXN003,Alice Brown,Charlie Davis,750.25,GBP,UK,1990-12-20
TXN004,David Wilson,Eve Johnson,-500.00,USD,Canada,1975-08-10
TXN005,Frank Miller,Grace Lee,3000.75,EUR,France,1988-03-25
"@

    $mixedContent | Out-File -FilePath "mixed_data.csv" -Encoding UTF8

    $response = Invoke-RestMethod -Uri "$baseUrl/ingest/file" -Method POST -Form @{
        file = Get-Item "mixed_data.csv"
    } -ContentType "multipart/form-data"

    Write-Host "SUCCESS: Mixed data batch processing handled gracefully" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    Remove-Item "mixed_data.csv" -ErrorAction SilentlyContinue

} catch {
    Write-Host "FAIL: Mixed data batch processing failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=====================================" -ForegroundColor Green
Write-Host "Batch Ingestion Test Complete!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

Write-Host "`nExpected Results:" -ForegroundColor Yellow
Write-Host "- All tests should return 200 OK status" -ForegroundColor White
Write-Host "- File validation should be lenient with fallbacks" -ForegroundColor White
Write-Host "- Batch processing should continue even with invalid records" -ForegroundColor White
Write-Host "- Error messages should be descriptive and helpful" -ForegroundColor White
Write-Host "- No 400 or 500 errors should occur" -ForegroundColor White 