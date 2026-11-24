@echo off
setlocal
cd /d "%~dp0"

if not exist out mkdir out

echo Mengumpulkan daftar sumber...
dir /s /b src\*.java > sources.txt

echo Mengompilasi...
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
    echo.
    echo Kompilasi gagal. Periksa pesan di atas.
    exit /b 1
)

echo.
echo Build selesai. Kelas tersimpan di folder out\
endlocal
