# Run Console Application Script
powershell -ExecutionPolicy Bypass -File .\compile.ps1
if ($LASTEXITCODE -eq 0) {
    Write-Host "Launching Console CLI..." -ForegroundColor Green
    java -cp "bin;lib/sqlite-jdbc.jar" ui.MainConsole
}
