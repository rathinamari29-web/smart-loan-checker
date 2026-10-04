# Local Wi-Fi Mobile Server Launcher
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "   MOBILE NETWORK SERVER (OPEN ON ANY PHONE)     " -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

# Find Local IP Address
$localIP = (Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.InterfaceAlias -notlike "*Loopback*" -and $_.IPAddress -notlike "169.254*" } | Select-Object -First 1).IPAddress

if (-not $localIP) {
    $localIP = "127.0.0.1"
}

$port = 8080
$url = "http://${localIP}:${port}"

Write-Host ""
Write-Host "TO OPEN AND INSTALL ON ANY MOBILE PHONE:" -ForegroundColor Yellow
Write-Host "1. Connect your Phone and PC to the SAME Wi-Fi network." -ForegroundColor White
Write-Host "2. Open Safari or Chrome on your mobile phone." -ForegroundColor White
Write-Host "3. Type this URL into your phone browser:" -ForegroundColor White
Write-Host ""
Write-Host "   -> $url " -ForegroundColor Green
Write-Host ""
Write-Host "4. Tap 'Add to Home Screen' or 'Install App' in Chrome/Safari to install as an App icon on your phone screen!" -ForegroundColor Yellow
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# Start HTTP Server
python -m http.server $port --bind 0.0.0.0 --directory mobile
