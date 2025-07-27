# OFAC Integration Test Script (curl)
# Tests the real-time sanctions screening using U.S. Treasury OFAC SDN list

$baseUrl = "http://localhost:8080"
$apiUrl = "$baseUrl/api"

Write-Host "OFAC Integration Test Script (curl)" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

# Test 1: Health Check
Write-Host "`n1. Testing OFAC Service Health..." -ForegroundColor Yellow
try {
    $healthResponse = curl -s "$apiUrl/ofac/health" | ConvertFrom-Json
    Write-Host "Health Check Response:" -ForegroundColor Green
    Write-Host "   Status: $($healthResponse.status)" -ForegroundColor Green
    Write-Host "   Entity Count: $($healthResponse.entityCount)" -ForegroundColor Green
    Write-Host "   Last Refresh: $($healthResponse.lastRefresh)" -ForegroundColor Green
} catch {
    Write-Host "Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Get Statistics
Write-Host "`n2. Testing OFAC Statistics..." -ForegroundColor Yellow
try {
    $statsResponse = curl -s "$apiUrl/ofac/stats" | ConvertFrom-Json
    Write-Host "Statistics Response:" -ForegroundColor Green
    Write-Host "   Total Entities: $($statsResponse.totalEntities)" -ForegroundColor Green
    Write-Host "   Last Refresh Date: $($statsResponse.lastRefreshDate)" -ForegroundColor Green
} catch {
    Write-Host "Statistics Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test Entity Screening - Neutral Name
Write-Host "`n3. Testing Entity Screening - Neutral Name (Jane Doe)..." -ForegroundColor Yellow
try {
    $name = [System.Web.HttpUtility]::UrlEncode("Jane Doe")
    $country = [System.Web.HttpUtility]::UrlEncode("US")
    $checkResponse = curl -s "$apiUrl/ofac/check?name=$name&country=$country" | ConvertFrom-Json
    Write-Host "Entity Check Result:" -ForegroundColor Green
    Write-Host "   Entity: $($checkResponse.entity)" -ForegroundColor Green
    Write-Host "   Country: $($checkResponse.country)" -ForegroundColor Green
    Write-Host "   Is Sanctioned: $($checkResponse.isSanctioned)" -ForegroundColor Green
} catch {
    Write-Host "Entity Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test Entity Screening - Potential OFAC Match
Write-Host "`n4. Testing Entity Screening - Potential OFAC Match (Ali Mohammed)..." -ForegroundColor Yellow
try {
    $name = [System.Web.HttpUtility]::UrlEncode("Ali Mohammed")
    $country = [System.Web.HttpUtility]::UrlEncode("US")
    $checkResponse = curl -s "$apiUrl/ofac/check?name=$name&country=$country" | ConvertFrom-Json
    Write-Host "Entity Check Result:" -ForegroundColor Green
    Write-Host "   Entity: $($checkResponse.entity)" -ForegroundColor Green
    Write-Host "   Country: $($checkResponse.country)" -ForegroundColor Green
    Write-Host "   Is Sanctioned: $($checkResponse.isSanctioned)" -ForegroundColor Green
} catch {
    Write-Host "Entity Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test Fuzzy Matching
Write-Host "`n5. Testing Fuzzy Matching..." -ForegroundColor Yellow
try {
    $name = [System.Web.HttpUtility]::UrlEncode("Ali Mohammad")
    $threshold = "0.8"
    $fuzzyResponse = curl -s "$apiUrl/ofac/check-fuzzy?name=$name&threshold=$threshold" | ConvertFrom-Json
    Write-Host "Fuzzy Check Result:" -ForegroundColor Green
    Write-Host "   Entity: $($fuzzyResponse.entity)" -ForegroundColor Green
    Write-Host "   Threshold: $($fuzzyResponse.threshold)" -ForegroundColor Green
    Write-Host "   Is Sanctioned: $($fuzzyResponse.isSanctioned)" -ForegroundColor Green
} catch {
    Write-Host "Fuzzy Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Search Sanctioned Entities
Write-Host "`n6. Testing Entity Search..." -ForegroundColor Yellow
try {
    $searchName = [System.Web.HttpUtility]::UrlEncode("Ali")
    $searchResponse = curl -s "$apiUrl/ofac/search?name=$searchName" | ConvertFrom-Json
    Write-Host "Search Result:" -ForegroundColor Green
    Write-Host "   Found $($searchResponse.Count) entities with 'Ali'" -ForegroundColor Green
    if ($searchResponse.Count -gt 0) {
        foreach ($entity in $searchResponse) {
            Write-Host "   - $($entity.name) from $($entity.country)" -ForegroundColor Green
        }
    }
} catch {
    Write-Host "Search Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Test Transaction Ingestion with OFAC Screening
Write-Host "`n7. Testing Transaction Ingestion with OFAC Screening..." -ForegroundColor Yellow
try {
    # Test transaction with potential OFAC match
    $transactionData = @{
        transactionId = "TXN-OFAC-TEST-001"
        timestamp = "2025-07-25T14:22:30Z"
        amount = 1000.00
        currency = "USD"
        senderName = "Ali Mohammed"
        receiverName = "Jane Smith"
        senderAccount = "ACC-001"
        receiverAccount = "ACC-002"
        country = "US"
        manualFlag = $true
        description = "Test transaction with potential OFAC match"
    }
    
    $jsonData = $transactionData | ConvertTo-Json
    $ingestResponse = curl -s -X POST -H "Content-Type: application/json" -d $jsonData "$apiUrl/transactions/ingest" | ConvertFrom-Json
    Write-Host "Transaction Ingestion Result:" -ForegroundColor Green
    Write-Host "   Status: $($ingestResponse.status)" -ForegroundColor Green
    Write-Host "   Alert Triggered: $($ingestResponse.alertTriggered)" -ForegroundColor Green
    Write-Host "   Risk Score: $($ingestResponse.riskScore)" -ForegroundColor Green
} catch {
    Write-Host "Transaction Ingestion Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 8: Manual Refresh (if needed)
Write-Host "`n8. Testing Manual OFAC Data Refresh..." -ForegroundColor Yellow
try {
    $refreshResponse = curl -s -X POST "$apiUrl/ofac/refresh" | ConvertFrom-Json
    Write-Host "Manual Refresh Result:" -ForegroundColor Green
    Write-Host "   Success: $($refreshResponse.success)" -ForegroundColor Green
    Write-Host "   Message: $($refreshResponse.message)" -ForegroundColor Green
    if ($refreshResponse.success) {
        Write-Host "   Entity Count: $($refreshResponse.entityCount)" -ForegroundColor Green
    }
} catch {
    Write-Host "Manual Refresh Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nOFAC Integration Test Summary" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "All tests completed. Check results above for any failures." -ForegroundColor Green
Write-Host "`nExpected Results:" -ForegroundColor Yellow
Write-Host "   - Jane Doe should NOT be flagged as sanctioned" -ForegroundColor White
Write-Host "   - Ali Mohammed/Ali Mohammad should be flagged as potentially sanctioned" -ForegroundColor White
Write-Host "   - Transaction ingestion should trigger alerts for sanctioned entities" -ForegroundColor White
Write-Host "   - OFAC data should be loaded and cached successfully" -ForegroundColor White 