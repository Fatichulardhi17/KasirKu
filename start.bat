@echo off
echo ========================================
echo   Aplikasi Kasir - KasirKu
echo ========================================
echo.

REM Pastikan sudah dikompilasi
if not exist out\kasir\Main.class (
    echo [INFO] Belum dikompilasi, kompilasi dulu...
    call build.bat
    if errorlevel 1 (
        echo [ERROR] Kompilasi gagal!
        pause
        exit /b 1
    )
    echo.
)

REM Buat direktori data jika belum ada
if not exist data mkdir data

echo [INFO] Menjalankan aplikasi...
echo.
java -cp out kasir.Main

if errorlevel 1 (
    echo.
    echo [ERROR] Aplikasi berhenti dengan error!
    pause
) else (
    echo.
    echo [INFO] Aplikasi ditutup dengan normal.
)
