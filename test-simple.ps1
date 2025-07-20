# Simple test script to check basic endpoints
Write-Host "Testing basic endpoints..."

$endpoints = @(
    "http://localhost:8080/admin/health",
    "http://localhost:8080/admin/status", 
    "http://localhost:8080/public/db-health",
    "http://localhost:8080/users/register"
)

foreach ($endpoint in $endpoints) {
    Write-Host "`nTesting: $endpoint"
    try {
        $response = Invoke-RestMethod -Uri $endpoint -Method GET -ErrorAction Stop
        Write-Host "SUCCESS: $($response | ConvertTo-Json -Depth 3)"
    } catch {
        Write-Host "ERROR: $($_.Exception.Message)"
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
    }
} 