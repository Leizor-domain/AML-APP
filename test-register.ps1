# PowerShell script to test user registration
$uri = "http://localhost:8080/users/register"
$body = @{
    username = "testuser"
    password = "testpass123"
    role = "VIEWER"
} | ConvertTo-Json

$headers = @{
    "Content-Type" = "application/json"
}

Write-Host "Testing registration endpoint..."
Write-Host "URL: $uri"
Write-Host "Body: $body"

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $body -Headers $headers
    Write-Host "Success! Response: $($response | ConvertTo-Json -Depth 10)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    Write-Host "Response: $($_.Exception.Response)"
} 