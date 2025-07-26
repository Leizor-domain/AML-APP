# OFAC Integration Test Script using curl
# Tests the real-time sanctions screening using U.S. Treasury OFAC SDN list

$baseUrl = "http://localhost:8080"
$apiUrl = "$baseUrl/api"

Write-Host "🔍 OFAC Integration Test Script (curl)" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# Test 1: Health Check
Write-Host "`n1. Testing OFAC Service Health..." -ForegroundColor Yellow
try {
    $healthResponse = curl -s "$apiUrl/ofac/health"
    Write-Host "✅ Health Check Response:" -ForegroundColor Green
    Write-Host $healthResponse -ForegroundColor White
} catch {
    Write-Host "❌ Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Get Statistics
Write-Host "`n2. Testing OFAC Statistics..." -ForegroundColor Yellow
try {
    $statsResponse = curl -s "$apiUrl/ofac/stats"
    Write-Host "✅ Statistics Response:" -ForegroundColor Green
    Write-Host $statsResponse -ForegroundColor White
} catch {
    Write-Host "❌ Statistics Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test Entity Screening - Neutral Name
Write-Host "`n3. Testing Entity Screening - Neutral Name (Jane Doe)..." -ForegroundColor Yellow
try {
    $checkResponse = curl -s "$apiUrl/ofac/check?name=Jane%20Doe`&country=US"
    Write-Host "✅ Entity Check Result:" -ForegroundColor Green
    Write-Host $checkResponse -ForegroundColor White
} catch {
    Write-Host "❌ Entity Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test Entity Screening - Potential OFAC Match
Write-Host "`n4. Testing Entity Screening - Potential OFAC Match (Ali Mohammed)..." -ForegroundColor Yellow
try {
    $checkResponse = curl -s "$apiUrl/ofac/check?name=Ali%20Mohammed`&country=US"
    Write-Host "✅ Entity Check Result:" -ForegroundColor Green
    Write-Host $checkResponse -ForegroundColor White
} catch {
    Write-Host "❌ Entity Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test Fuzzy Matching
Write-Host "`n5. Testing Fuzzy Matching..." -ForegroundColor Yellow
try {
    $fuzzyResponse = curl -s "$apiUrl/ofac/check-fuzzy?name=Ali%20Mohammad`&threshold=0.8"
    Write-Host "✅ Fuzzy Check Result:" -ForegroundColor Green
    Write-Host $fuzzyResponse -ForegroundColor White
} catch {
    Write-Host "❌ Fuzzy Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Search Sanctioned Entities
Write-Host "`n6. Testing Entity Search..." -ForegroundColor Yellow
try {
    $searchResponse = curl -s "$apiUrl/ofac/search?name=Ali"
    Write-Host "✅ Search Result:" -ForegroundColor Green
    Write-Host $searchResponse -ForegroundColor White
} catch {
    Write-Host "❌ Search Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Test Transaction Ingestion with OFAC Screening
Write-Host "`n7. Testing Transaction Ingestion with OFAC Screening..." -ForegroundColor Yellow
try {
    $transactionJson = '{
        "transactionId": "TXN-OFAC-TEST-001",
        "timestamp": "2025-07-25T14:22:30Z",
        "amount": 1000.00,
        "currency": "USD",
        "senderName": "Ali Mohammed",
        "receiverName": "Jane Smith",
        "senderAccount": "ACC-001",
        "receiverAccount": "ACC-002",
        "country": "US",
        "manualFlag": true,
        "description": "Test transaction with potential OFAC match"
    }'
    
    $ingestResponse = curl -s -X POST "$apiUrl/transactions/ingest" -H "Content-Type: application/json" -d $transactionJson
    Write-Host "✅ Transaction Ingestion Result:" -ForegroundColor Green
    Write-Host $ingestResponse -ForegroundColor White
} catch {
    Write-Host "❌ Transaction Ingestion Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 8: Manual Refresh
Write-Host "`n8. Testing Manual OFAC Data Refresh..." -ForegroundColor Yellow
try {
    $refreshResponse = curl -s -X POST "$apiUrl/ofac/refresh"
    Write-Host "✅ Manual Refresh Result:" -ForegroundColor Green
    Write-Host $refreshResponse -ForegroundColor White
} catch {
    Write-Host "❌ Manual Refresh Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎯 OFAC Integration Test Summary" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "✅ All tests completed. Check results above for any failures." -ForegroundColor Green
Write-Host "`n📋 Expected Results:" -ForegroundColor Yellow
Write-Host "   - Jane Doe should NOT be flagged as sanctioned" -ForegroundColor White
Write-Host "   - Ali Mohammed/Ali Mohammad should be flagged as potentially sanctioned" -ForegroundColor White
Write-Host "   - Transaction ingestion should trigger alerts for sanctioned entities" -ForegroundColor White
Write-Host "   - OFAC data should be loaded and cached successfully" -ForegroundColor White 