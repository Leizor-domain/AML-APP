# =====================================================
# AML Dashboard Connection Test Script
# =====================================================
# This script tests the connection between frontend and backend
# to help troubleshoot why alerts aren't showing on the dashboard
# =====================================================

Write-Host "🔍 Testing AML Dashboard Connection..." -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# 1. Check if backend is running
Write-Host "`n1️⃣ Checking if backend is running on port 8080..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Backend is running!" -ForegroundColor Green
        Write-Host "   Status: $($response.Content)" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Backend is NOT running on port 8080" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
    Write-Host "`n💡 To start the backend:" -ForegroundColor Yellow
    Write-Host "   cd aml-admin" -ForegroundColor Gray
    Write-Host "   mvn spring-boot:run" -ForegroundColor Gray
}

# 2. Test alerts endpoint
Write-Host "`n2️⃣ Testing alerts endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/alerts" -Method GET -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Alerts endpoint is accessible!" -ForegroundColor Green
        $alerts = $response.Content | ConvertFrom-Json
        Write-Host "   Total alerts: $($alerts.content.Count)" -ForegroundColor Gray
        if ($alerts.content.Count -gt 0) {
            Write-Host "   Sample alert: $($alerts.content[0].alert_id)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "❌ Alerts endpoint failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# 3. Test alerts count endpoint
Write-Host "`n3️⃣ Testing alerts count endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/alerts/count" -Method GET -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Alerts count endpoint is accessible!" -ForegroundColor Green
        $count = $response.Content | ConvertFrom-Json
        Write-Host "   Total alerts in database: $($count.count)" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Alerts count endpoint failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# 4. Test database connection via backend
Write-Host "`n4️⃣ Testing database connection via backend..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/health/db" -Method GET -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Database connection is working!" -ForegroundColor Green
        Write-Host "   Status: $($response.Content)" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Database health check failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# 5. Test frontend API configuration
Write-Host "`n5️⃣ Testing frontend API configuration..." -ForegroundColor Yellow
Write-Host "   Frontend expects backend at: http://localhost:8080" -ForegroundColor Gray
Write-Host "   API service configured to use: adminApi (baseURL: http://localhost:8080)" -ForegroundColor Gray

# 6. Check if frontend is running
Write-Host "`n6️⃣ Checking if frontend is running..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -Method GET -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Frontend is running on port 3000!" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Frontend is NOT running on port 3000" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
    Write-Host "`n💡 To start the frontend:" -ForegroundColor Yellow
    Write-Host "   npm run dev" -ForegroundColor Gray
}

# 7. Summary and recommendations
Write-Host "`n📋 SUMMARY AND RECOMMENDATIONS:" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

Write-Host "`n🔧 If backend is not running:" -ForegroundColor Yellow
Write-Host "   1. Open a new terminal" -ForegroundColor Gray
Write-Host "   2. Navigate to: cd aml-admin" -ForegroundColor Gray
Write-Host "   3. Run: mvn spring-boot:run" -ForegroundColor Gray
Write-Host "   4. Wait for 'Started AMLAdminApplication' message" -ForegroundColor Gray

Write-Host "`n🔧 If frontend is not running:" -ForegroundColor Yellow
Write-Host "   1. Open a new terminal" -ForegroundColor Gray
Write-Host "   2. Navigate to project root" -ForegroundColor Gray
Write-Host "   3. Run: npm run dev" -ForegroundColor Gray
Write-Host "   4. Wait for 'Local: http://localhost:3000' message" -ForegroundColor Gray

Write-Host "`n🔧 If database connection fails:" -ForegroundColor Yellow
Write-Host "   1. Ensure PostgreSQL is running" -ForegroundColor Gray
Write-Host "   2. Check database credentials in application.properties" -ForegroundColor Gray
Write-Host "   3. Verify database 'amlengine_db' exists" -ForegroundColor Gray

Write-Host "`n🔧 To verify alerts in database:" -ForegroundColor Yellow
Write-Host "   1. Connect to PostgreSQL: psql -U postgres -d amlengine_db" -ForegroundColor Gray
Write-Host "   2. Run: SELECT COUNT(*) FROM alerts;" -ForegroundColor Gray
Write-Host "   3. Run: SELECT * FROM alerts LIMIT 5;" -ForegroundColor Gray

Write-Host "`n✅ Test completed!" -ForegroundColor Green
