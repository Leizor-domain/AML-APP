# AML App Cleanup Script
Write-Host "=== AML App Cleanup ===" -ForegroundColor Green

$redundantFiles = @(
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
    "cleanup_redundant_files.ps1",
    "empty_file.csv",
    "invalid_file.txt",
    "large_file.csv",
    "malformed.csv",
    "mixed_data.csv",
    "test_transactions.csv",
    "tatus",
    "Image Jul 28, 2025, 08_44_58 AM.png",
    "babel.config.cjs",
    "setupTests.js"
)

$removedCount = 0
foreach ($file in $redundantFiles) {
    if (Test-Path $file) {
        Remove-Item $file -Force -ErrorAction SilentlyContinue
        Write-Host "Removed: $file" -ForegroundColor Green
        $removedCount++
    }
}

Write-Host "Cleanup complete. Removed $removedCount files." -ForegroundColor Cyan