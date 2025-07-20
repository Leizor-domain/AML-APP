# PowerShell script to test database health endpoint
$uri = "http://localhost:8080/public/db-health"

Write-Host "Testing database health endpoint..."
Write-Host "URL: $uri"

try {
    $response = Invoke-RestMethod -Uri $uri -Method GET
    Write-Host "Success! Response: $($response | ConvertTo-Json -Depth 10)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    Write-Host "Response: $($_.Exception.Response)"
} 