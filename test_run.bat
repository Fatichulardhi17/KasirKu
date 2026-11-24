@echo off
echo Testing aplikasi kasir...
echo.

REM Pastikan sudah dikompilasi
if not exist out\kasir\Main.class (
    echo Kompilasi dulu...
    call build.bat
)

REM Buat folder data
if not exist data mkdir data

echo.
echo Menjalankan aplikasi (akan muncul GUI window)...
echo Jika tidak muncul, perhatikan error di bawah:
echo.
start /B java -cp out kasir.Main 2>&1
timeout /t 3 /nobreak > nul

echo.
echo Aplikasi sudah dijalankan!
echo Periksa apakah window GUI muncul.
echo.
echo Jika ada error, akan muncul di atas.
echo Tekan Ctrl+C untuk keluar dari script ini.
pause
