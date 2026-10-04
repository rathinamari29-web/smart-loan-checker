# Launch Mobile Web Application Script
Write-Host "Launching Mobile Application in default Web Browser..." -ForegroundColor Green

$htmlPath = (Resolve-Path "mobile/index.html").Path
Start-Process $htmlPath
