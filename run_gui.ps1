# Run GUI Application Script
powershell -ExecutionPolicy Bypass -File .\compile.ps1
if ($LASTEXITCODE -eq 0) {
    Write-Host "Launching Swing GUI..." -ForegroundColor Green
    java -cp "bin;lib/sqlite-jdbc.jar" ui.MainGUI
}
