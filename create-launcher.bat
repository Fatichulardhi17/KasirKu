@echo off
echo [INFO] Membuat launcher KasirKu...

REM Buat launcher simple yang menjalankan JAR
echo @echo off > KasirKu.bat
echo cd /d "%%~dp0" >> KasirKu.bat
echo if not exist KasirKu.jar ( >> KasirKu.bat
echo     echo [ERROR] File KasirKu.jar tidak ditemukan! >> KasirKu.bat
echo     echo Jalankan create-jar.bat terlebih dahulu. >> KasirKu.bat
echo     pause >> KasirKu.bat
echo     exit /b 1 >> KasirKu.bat
echo ^) >> KasirKu.bat
echo start javaw -jar KasirKu.jar >> KasirKu.bat

echo.
echo ========================================
echo   Launcher berhasil dibuat!
echo ========================================
echo.
echo File: KasirKu.bat
echo.
echo Cara pakai:
echo   - Double-click KasirKu.bat untuk buka aplikasi
echo   - Aplikasi akan buka tanpa console window
echo.
