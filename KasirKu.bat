@echo off 
cd /d "%~dp0" 
if not exist KasirKu.jar ( 
    echo [ERROR] File KasirKu.jar tidak ditemukan! 
    echo Jalankan create-jar.bat terlebih dahulu. 
    pause 
    exit /b 1 
) 
start javaw -jar KasirKu.jar 
