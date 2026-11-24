@echo off
setlocal
cd /d "%~dp0"

if not exist out\kasir\Main.class (
    call build.bat
    if errorlevel 1 exit /b 1
)

echo Menjalankan KasirKu...
java -cp out kasir.Main
endlocal
