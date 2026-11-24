@echo off
echo ========================================
echo   Membuat JAR File - KasirKu
echo ========================================
echo.

REM Kompilasi dulu jika belum
if not exist out\kasir\Main.class (
    echo [INFO] Kompilasi aplikasi...
    call build.bat
    if errorlevel 1 (
        echo [ERROR] Kompilasi gagal!
        pause
        exit /b 1
    )
)

REM Buat manifest file
echo Main-Class: kasir.Main > manifest.txt

REM Buat JAR file
echo [INFO] Membuat file KasirKu.jar...
jar cvfm KasirKu.jar manifest.txt -C out .

REM Hapus manifest temporary
del manifest.txt

if exist KasirKu.jar (
    echo.
    echo ========================================
    echo   JAR berhasil dibuat!
    echo ========================================
    echo.
    echo File: KasirKu.jar
    echo.
    echo Cara pakai:
    echo   1. Double-click KasirKu.jar
    echo   2. Atau ketik: java -jar KasirKu.jar
    echo.
    echo [INFO] Membuat shortcut launcher...
    call create-launcher.bat
) else (
    echo [ERROR] Gagal membuat JAR file!
    pause
    exit /b 1
)

pause
