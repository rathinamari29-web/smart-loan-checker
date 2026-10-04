# Compile Script for Smart Loan Eligibility Checker
Write-Host "Compiling Java sources..." -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName

javac -cp "lib/sqlite-jdbc.jar" -d bin $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation successful! Classes built in bin/" -ForegroundColor Green
} else {
    Write-Host "❌ Compilation failed." -ForegroundColor Red
}
