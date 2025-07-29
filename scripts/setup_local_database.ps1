# AML App Local Database Setup Script
# This script sets up the local PostgreSQL database for development

Write-Host "Setting up local PostgreSQL database for AML App..." -ForegroundColor Green

# PostgreSQL installation path
$PSQL_PATH = "C:\Program Files\PostgreSQL\17\bin\psql.exe"
$CREATEDB_PATH = "C:\Program Files\PostgreSQL\17\bin\createdb.exe"

# Database configuration
$DB_NAME = "amlengine_db"
$DB_USER = "postgres"

Write-Host "PostgreSQL Path: $PSQL_PATH" -ForegroundColor Yellow
Write-Host "Database Name: $DB_NAME" -ForegroundColor Yellow
Write-Host "Database User: $DB_USER" -ForegroundColor Yellow

# Check if PostgreSQL tools exist
if (-not (Test-Path $PSQL_PATH)) {
    Write-Host "Error: PostgreSQL not found at $PSQL_PATH" -ForegroundColor Red
    Write-Host "Please ensure PostgreSQL is installed and the path is correct." -ForegroundColor Red
    exit 1
}

# Check if database already exists
Write-Host "Checking if database '$DB_NAME' exists..." -ForegroundColor Yellow
$dbExists = & $PSQL_PATH -U $DB_USER -t -c "SELECT 1 FROM pg_database WHERE datname='$DB_NAME';" 2>$null

if ($dbExists) {
    Write-Host "Database '$DB_NAME' already exists." -ForegroundColor Green
} else {
    Write-Host "Creating database '$DB_NAME'..." -ForegroundColor Yellow
    & $CREATEDB_PATH -U $DB_USER $DB_NAME
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database '$DB_NAME' created successfully!" -ForegroundColor Green
    } else {
        Write-Host "Error creating database. Please check your PostgreSQL credentials." -ForegroundColor Red
        exit 1
    }
}

# Test connection
Write-Host "Testing database connection..." -ForegroundColor Yellow
$testResult = & $PSQL_PATH -U $DB_USER -d $DB_NAME -c "SELECT version();" 2>$null

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database connection successful!" -ForegroundColor Green
    Write-Host "PostgreSQL Version:" -ForegroundColor Cyan
    Write-Host $testResult -ForegroundColor White
} else {
    Write-Host "Error connecting to database. Please check your credentials." -ForegroundColor Red
}

Write-Host "`nNext steps:" -ForegroundColor Cyan
Write-Host "1. Update your application.properties with your PostgreSQL password" -ForegroundColor White
Write-Host "2. Replace 'your_postgres_password_here' with your actual password" -ForegroundColor White
Write-Host "3. Start your Spring Boot application" -ForegroundColor White
Write-Host "4. The application will automatically create tables on first run" -ForegroundColor White

Write-Host "`nDatabase setup completed!" -ForegroundColor Green 