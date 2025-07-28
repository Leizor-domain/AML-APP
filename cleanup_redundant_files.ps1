# AML App Redundant Files Cleanup Script
# This script removes all redundant files after testing and upgrades are complete

Write-Host "Starting cleanup of redundant files..." -ForegroundColor Green

# Stop any running Java processes
Write-Host "Stopping any running Java processes..." -ForegroundColor Yellow
Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*mvn*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# Remove test and debug files
Write-Host "Removing test and debug files..." -ForegroundColor Yellow
$testFiles = @(
    "test_sanctions_debug.csv",
    "sample_sanctions_trigger_transactions.csv",
    "confirmed_alert_trigger_transactions.csv",
    "test-*.ps1",
    "test-*.html",
    "test-*.txt",
    "test-*.csv",
    "sample_*.csv",
    "malformed.csv",
    "mixed_data.csv",
    "large_file.csv",
    "empty_file.csv",
    "invalid_file.txt"
)

foreach ($file in $testFiles) {
    if (Test-Path $file) {
        Remove-Item $file -Force
        Write-Host "Removed: $file" -ForegroundColor Red
    }
}

# Remove H2 database files (test database)
Write-Host "Removing H2 database files..." -ForegroundColor Yellow
$h2Files = @(
    "*.db",
    "*.trace.db",
    "*.lock.db",
    "testdb.*",
    "*.h2.db"
)

foreach ($pattern in $h2Files) {
    Get-ChildItem -Path . -Filter $pattern -Recurse | ForEach-Object {
        Remove-Item $_.FullName -Force
        Write-Host "Removed H2 file: $($_.Name)" -ForegroundColor Red
    }
}

# Remove build artifacts
Write-Host "Removing build artifacts..." -ForegroundColor Yellow
$buildDirs = @(
    "target",
    "build",
    "out",
    "bin",
    "dist"
)

foreach ($dir in $buildDirs) {
    if (Test-Path $dir) {
        Remove-Item $dir -Recurse -Force
        Write-Host "Removed build directory: $dir" -ForegroundColor Red
    }
}

# Remove IDE and editor files
Write-Host "Removing IDE and editor files..." -ForegroundColor Yellow
$ideFiles = @(
    ".idea",
    ".vscode",
    "*.iml",
    "*.ipr",
    "*.iws",
    ".project",
    ".classpath",
    ".settings",
    "*.swp",
    "*.swo",
    "*~",
    ".DS_Store",
    "Thumbs.db"
)

foreach ($pattern in $ideFiles) {
    Get-ChildItem -Path . -Filter $pattern -Recurse | ForEach-Object {
        Remove-Item $_.FullName -Force -Recurse
        Write-Host "Removed IDE file: $($_.Name)" -ForegroundColor Red
    }
}

# Remove log files
Write-Host "Removing log files..." -ForegroundColor Yellow
$logFiles = @(
    "*.log",
    "logs",
    "*.out",
    "*.err"
)

foreach ($pattern in $logFiles) {
    Get-ChildItem -Path . -Filter $pattern -Recurse | ForEach-Object {
        Remove-Item $_.FullName -Force
        Write-Host "Removed log file: $($_.Name)" -ForegroundColor Red
    }
}

# Remove temporary files
Write-Host "Removing temporary files..." -ForegroundColor Yellow
$tempFiles = @(
    "*.tmp",
    "*.temp",
    "*.bak",
    "*.backup",
    "*.old",
    "*.orig"
)

foreach ($pattern in $tempFiles) {
    Get-ChildItem -Path . -Filter $pattern -Recurse | ForEach-Object {
        Remove-Item $_.FullName -Force
        Write-Host "Removed temp file: $($_.Name)" -ForegroundColor Red
    }
}

# Remove test summary files
Write-Host "Removing test summary files..." -ForegroundColor Yellow
$summaryFiles = @(
    "*_SUMMARY.md",
    "*_FIXES.md",
    "*_RESULTS.md",
    "*_STATUS.md",
    "*_VERIFICATION.md",
    "*_ENHANCEMENT.md",
    "*_RESOLUTION.md",
    "*_CLEANUP.md"
)

foreach ($pattern in $summaryFiles) {
    Get-ChildItem -Path . -Filter $pattern | ForEach-Object {
        Remove-Item $_.FullName -Force
        Write-Host "Removed summary file: $($_.Name)" -ForegroundColor Red
    }
}

# Remove coverage reports
Write-Host "Removing coverage reports..." -ForegroundColor Yellow
if (Test-Path "coverage") {
    Remove-Item "coverage" -Recurse -Force
    Write-Host "Removed coverage directory" -ForegroundColor Red
}

# Remove node_modules (if exists)
Write-Host "Removing node_modules..." -ForegroundColor Yellow
if (Test-Path "node_modules") {
    try {
        Remove-Item "node_modules" -Recurse -Force
        Write-Host "Removed node_modules directory" -ForegroundColor Red
    } catch {
        Write-Host "Warning: Could not remove node_modules (may be in use)" -ForegroundColor Yellow
    }
}

# Remove package-lock.json (if exists)
if (Test-Path "package-lock.json") {
    Remove-Item "package-lock.json" -Force
    Write-Host "Removed package-lock.json" -ForegroundColor Red
}

# Remove test configuration files
Write-Host "Removing test configuration files..." -ForegroundColor Yellow
$testConfigFiles = @(
    "application-test.properties",
    "test.properties",
    "test.yml",
    "test.yaml"
)

foreach ($file in $testConfigFiles) {
    if (Test-Path $file) {
        Remove-Item $file -Force
        Write-Host "Removed test config: $file" -ForegroundColor Red
    }
}

# Remove debug logging modifications from source files
Write-Host "Cleaning up debug logging in source files..." -ForegroundColor Yellow
Write-Host "Note: Manual cleanup of debug logging may be required in source files" -ForegroundColor Yellow

Write-Host "Cleanup completed!" -ForegroundColor Green
Write-Host "Remaining files should be production-ready." -ForegroundColor Green 