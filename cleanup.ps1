# AML App Comprehensive Cleanup Script
Write-Host "=== AML App Professional Cleanup ===" -ForegroundColor Green
Write-Host ""

# Define redundant files to remove
$redundantFiles = @(
    # Test files that are no longer needed
    "test-mock-alerts.ps1",
    "debug_test_transactions.csv",
    "rule_test_transactions.csv",
    "local_sanctions_test_transactions.csv",
    "high_risk_countries_test_transactions.csv",

    # Documentation files that can be consolidated
    "ALERT_GENERATION_DETAILED_GUIDE.md",
    "HIGH_RISK_COUNTRIES_DOCUMENTATION.md",
    "LOCAL_SANCTIONS_LIST_DOCUMENTATION.md",
    "OFAC_API_OFFLINE_ANALYSIS.md",

    # Environment and setup files
    "local_env_example.txt",
    "setup_local_database.ps1",

    # Old test scripts
    "test-admin-navigation.ps1",
    "test-api-error-fixes.ps1",
    "test-api-integration-fixes.ps1",
    "test-app.ps1",
    "test-batch-ingestion.ps1",
    "test-cors.html",
    "test-currency-api.ps1",
    "test-db-health.ps1",
    "test-deployed-apis.ps1",
    "test-deployed-endpoints.ps1",
    "test-deployment-status.ps1",
    "test-endpoints.ps1",
    "test-ofac-integration-curl.ps1",
    "test-ofac-integration.ps1",
    "test-register.ps1",
    "test-registration.html",
    "test-render-apis.ps1",
    "test-simple.ps1",
    "test-stock-api.ps1",

    # Old documentation files
    "ADMIN_DASHBOARD_NAVIGATION_FIX_SUMMARY.md",
    "REGISTRATION_ISSUE_FIX.md",
    "AML_LOGIC_DEDUPLICATION_SUMMARY.md",
    "API_ERROR_FIXES_SUMMARY.md",
    "API_INTEGRATION_FIXES_SUMMARY.md",
    "CIRCULAR_DEPENDENCY_RESOLUTION_SUMMARY.md",
    "COMPREHENSIVE_TEST_ENHANCEMENT_SUMMARY.md",
    "CURRENCY_CONVERTER_IMPLEMENTATION_SUMMARY.md",
    "DEPLOYMENT_FIXES.md",
    "DEPLOYMENT_STATUS_SUMMARY.md",
    "FINAL_API_FIX_SUMMARY.md",
    "FINAL_COMPREHENSIVE_TEST_SUMMARY.md",
    "FINAL_TEST_VERIFICATION_SUMMARY.md",
    "PRODUCTION_ENDPOINT_DEBUG_VALIDATION_SUMMARY.md",
    "REDUNDANT_FILES_CLEANUP_SUMMARY.md",
    "RENDER_API_FIXES_SUMMARY.md",
    "RULE_ENGINE_REFACTOR_SUMMARY.md",
    "SOLUTION_SUMMARY.md",

    # Old cleanup scripts
    "cleanup_redundant_files.ps1",

    # Test data files
    "empty_file.csv",
    "invalid_file.txt",
    "large_file.csv",
    "malformed.csv",
    "mixed_data.csv",
    "test_transactions.csv",

    # Misc files
    "tatus",
    "Image Jul 28, 2025, 08_44_58 AM.png",
    "babel.config.cjs",
    "setupTests.js"
)

Write-Host "Starting cleanup process..." -ForegroundColor Yellow

$removedCount = 0
$skippedCount = 0

foreach ($file in $redundantFiles) {
    if (Test-Path $file) {
        try {
            Remove-Item $file -Force -ErrorAction Stop
            Write-Host "✓ Removed: $file" -ForegroundColor Green
            $removedCount++
        } catch {
            Write-Host "✗ Failed to remove: $file" -ForegroundColor Red
            $skippedCount++
        }
    } else {
        Write-Host "- Skipped (not found): $file" -ForegroundColor Gray
        $skippedCount++
    }
}

Write-Host ""
Write-Host "=== Cleanup Summary ===" -ForegroundColor Cyan
Write-Host "Files removed: $removedCount" -ForegroundColor Green
Write-Host "Files skipped: $skippedCount" -ForegroundColor Yellow
Write-Host "Total processed: $($redundantFiles.Count)" -ForegroundColor White

Write-Host ""
Write-Host "✓ Cleanup completed successfully!" -ForegroundColor Green
Write-Host "✓ Codebase is now more organized and professional" -ForegroundColor Green
