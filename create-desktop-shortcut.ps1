# Script untuk membuat desktop shortcut ke KasirKu

$SourcePath = "$PSScriptRoot\KasirKu.vbs"
$DesktopPath = [Environment]::GetFolderPath("Desktop")
$ShortcutPath = "$DesktopPath\KasirKu.lnk"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Membuat Desktop Shortcut" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Cek apakah KasirKu.vbs ada
if (-not (Test-Path $SourcePath)) {
    Write-Host "[ERROR] File KasirKu.vbs tidak ditemukan!" -ForegroundColor Red
    Write-Host "Path: $SourcePath" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# Buat shortcut
Write-Host "[INFO] Membuat shortcut..." -ForegroundColor Yellow

$WshShell = New-Object -comObject WScript.Shell
$Shortcut = $WshShell.CreateShortcut($ShortcutPath)
$Shortcut.TargetPath = $SourcePath
$Shortcut.WorkingDirectory = $PSScriptRoot
$Shortcut.Description = "Aplikasi Kasir KasirKu - Point of Sale"
$Shortcut.Save()

if (Test-Path $ShortcutPath) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  Shortcut berhasil dibuat!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Location: $DesktopPath" -ForegroundColor Cyan
    Write-Host "File: KasirKu.lnk" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Sekarang Anda bisa buka aplikasi dari desktop! " -NoNewline
    Write-Host "🎉" -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "[ERROR] Gagal membuat shortcut!" -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to exit"
