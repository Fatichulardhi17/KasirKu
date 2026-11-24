# 🚀 Cara Deploy Aplikasi Kasir untuk Production

## 📋 Pilihan Deployment

Aplikasi ini adalah **aplikasi desktop standalone** yang TIDAK memerlukan hosting web.
Data disimpan secara lokal dalam file CSV.

---

## 🖥️ Skenario 1: Satu Komputer Kasir (Paling Simpel)

### Langkah Deploy:

1. **Copy seluruh folder project** ke komputer kasir:
   ```
   Copy folder: C:\Users\LENOVO\OneDrive\Documents\java
   Paste ke: D:\AplikasiKasir (atau lokasi lain)
   ```

2. **Pastikan Java terinstall** di komputer kasir:
   - Download: https://adoptium.net/
   - Install JDK 11 atau lebih baru
   - Cek: `java -version`

3. **Jalankan aplikasi:**
   ```
   Double-click: start.bat
   ```

4. **Setting aplikasi:**
   - Klik tombol "Pengaturan"
   - Isi nama toko, alamat, kasir, dll
   - Simpan

5. **Data otomatis tersimpan** di folder `data/`

### ✅ Keuntungan:
- Setup sangat mudah
- Tidak perlu internet
- Tidak ada biaya bulanan
- Data aman di komputer lokal

### ❌ Keterbatasan:
- Hanya 1 komputer yang bisa akses
- Tidak bisa akses dari HP/tablet
- Kalau komputer rusak, data hilang (kecuali di-backup)

---

## 🌐 Skenario 2: Multi Komputer di Jaringan LAN

**Jika punya beberapa komputer kasir dalam 1 toko:**

### Opsi A: Shared Folder (Simpel)

1. **Setup komputer utama:**
   - Install aplikasi di 1 komputer (misal: Komputer A)
   - Share folder `data/` via jaringan Windows

2. **Setup komputer lain:**
   - Install aplikasi di komputer lain (Komputer B, C, dst)
   - Mapping folder `data/` ke shared folder dari Komputer A
   
3. **Jalankan aplikasi** di semua komputer

**⚠️ Warning:** 
- CSV tidak mendukung concurrent write dengan baik
- Bisa terjadi data corrupt jika 2 kasir checkout bersamaan
- **Tidak direkomendasikan untuk production serius**

### Opsi B: Migrate ke Database (Advanced)

**Untuk multi-kasir yang aman, perlu upgrade ke database:**

1. **Install database lokal:**
   - SQLite (file-based, simpel)
   - PostgreSQL/MySQL (lebih robust)

2. **Modifikasi kode:**
   - Ganti CSV dengan JDBC
   - Implementasi connection pooling
   - Handle concurrent transactions

3. **Deploy:**
   - 1 komputer jadi database server
   - Komputer lain connect via jaringan LAN

**Estimasi effort:** 2-3 hari development

---

## 💼 Skenario 3: Distribusi ke Banyak Toko

**Jika mau deploy ke banyak toko berbeda:**

### Langkah:

1. **Buat installer executable** (opsional):
   ```
   - Gunakan tool: jpackage (bundled di JDK 14+)
   - Atau Launch4j untuk buat .exe
   - Atau Inno Setup untuk installer wizard
   ```

2. **Package lengkap:**
   ```
   AplikasiKasir_v1.0/
   ├── KasirKu.exe          ← Executable (opsional)
   ├── jre/                 ← Bundled Java Runtime
   ├── lib/                 ← JAR files
   ├── data/                ← Data folder (empty)
   ├── MULAI_DISINI.txt
   └── Panduan.pdf
   ```

3. **Distribusi:**
   - Copy ke flashdisk
   - Kirim via email (zip)
   - Upload ke Google Drive/Dropbox
   - Share link download

4. **Install di setiap toko:**
   - Extract folder
   - Double-click installer/start.bat
   - Setup data toko masing-masing

### ✅ Keuntungan:
- Setiap toko punya data sendiri
- Tidak perlu internet
- Privasi data terjaga

### ❌ Keterbatasan:
- Tidak ada sinkronisasi antar toko
- Update aplikasi harus manual ke setiap toko

---

## 📱 Skenario 4: Akses dari HP/Tablet (Butuh Upgrade)

**Jika ingin akses dari HP, butuh ubah jadi aplikasi web:**

### Opsi A: Hybrid Desktop + Web API

1. **Backend:** Java Spring Boot + REST API
2. **Database:** PostgreSQL/MySQL
3. **Frontend Mobile:** React Native / Flutter
4. **Deploy:** 
   - Backend di VPS/Cloud (Rp 50-200k/bulan)
   - Database di cloud atau local server
   - Mobile app di Google Play/App Store

**Estimasi effort:** 1-2 bulan development

### Opsi B: Full Web Application

1. **Frontend:** React / Vue.js
2. **Backend:** Java Spring Boot
3. **Database:** PostgreSQL
4. **Deploy:** VPS (Rp 50-200k/bulan) atau Cloud
5. **Akses:** Browser (desktop/mobile)

**Estimasi effort:** 1-2 bulan development

---

## 🔐 Backup & Disaster Recovery

### Backup Manual:

**Windows Task Scheduler untuk auto-backup:**

```batch
@echo off
REM backup_data.bat

set SOURCE=D:\AplikasiKasir\data
set BACKUP=D:\Backup\KasirBackup_%date:~-4,4%%date:~-10,2%%date:~-7,2%

xcopy "%SOURCE%" "%BACKUP%\" /E /I /Y

echo Backup selesai: %BACKUP%
```

**Setup Windows Task Scheduler:**
1. Buat file `backup_data.bat` seperti di atas
2. Task Scheduler → Create Basic Task
3. Trigger: Daily, jam 23:00
4. Action: Run `backup_data.bat`

### Backup Cloud (Otomatis):

**Opsi 1: OneDrive/Google Drive**
- Install OneDrive/Google Drive
- Simpan folder `data/` di folder yang di-sync
- Otomatis backup ke cloud

**Opsi 2: Script rsync/robocopy ke NAS**
```batch
robocopy D:\AplikasiKasir\data \\NAS\Backup\Kasir /MIR /R:3 /W:5
```

---

## 🚀 Recommended Deployment untuk Production

### Untuk TOKO KECIL (1 kasir):
✅ **Deploy Skenario 1**
- Copy folder ke komputer kasir
- Install Java
- Jalankan `start.bat`
- **Biaya:** Rp 0 (gratis)

### Untuk TOKO MENENGAH (2-3 kasir):
✅ **Deploy Skenario 2 Opsi B** (dengan database)
- 1 komputer server lokal (database)
- Aplikasi di setiap kasir
- **Biaya:** Rp 0 (hanya biaya hardware)

### Untuk CHAIN TOKO / Multi-Lokasi:
✅ **Deploy Skenario 4** (Web Application)
- Backend API di cloud
- Database cloud
- Access via browser
- **Biaya:** Rp 100-500k/bulan (tergantung traffic)

---

## 🛠️ Tools untuk Membuat Installer Profesional

### 1. jpackage (Built-in Java 14+)
```bash
jpackage --input out --main-jar kasir.jar --main-class kasir.Main --name KasirKu --type exe --win-shortcut --win-menu
```

### 2. Launch4j (Buat .exe dari JAR)
- Download: http://launch4j.sourceforge.net/
- Buat .exe wrapper untuk JAR
- Bisa bundle JRE

### 3. Inno Setup (Installer Wizard)
- Download: https://jrsoftware.org/isinfo.php
- Buat installer lengkap dengan wizard
- Registrasi di Windows Programs

### 4. Install4j (Commercial, paling profesional)
- Multi-platform installer
- Auto-update feature
- License: ~$600

---

## 📊 Perbandingan Biaya

| Deployment | Setup | Biaya Bulanan | Cocok Untuk |
|------------|-------|---------------|-------------|
| **Desktop Standalone** | 15 menit | Rp 0 | Toko kecil 1-2 kasir |
| **LAN + Database** | 2 hari | Rp 0 | Toko 3-5 kasir |
| **Cloud VPS** | 1 minggu | Rp 50-200k | Multi-toko, butuh remote |
| **Full Cloud** | 2-4 minggu | Rp 200-1jt | Enterprise, banyak toko |

---

## 🎯 Rekomendasi Saya

**Untuk memulai:**
1. ✅ Gunakan **Skenario 1** (standalone desktop)
2. ✅ Setup backup otomatis ke OneDrive/Google Drive
3. ✅ Monitor 1-2 bulan pertama
4. ✅ Kalau perlu upgrade (multi-kasir), baru migrate ke database

**Jangan over-engineering!** 
Mulai dari yang simpel, upgrade kalau butuh.

---

## 💡 Tips Production

### 1. Shortcut Desktop
```batch
REM Buat file: BukaKasir.bat
@echo off
cd /d "D:\AplikasiKasir"
start javaw -cp out kasir.Main
```
Klik kanan → Send to → Desktop (create shortcut)

### 2. Auto-start Saat Windows Booting
- Copy `start.bat` ke:
  ```
  C:\Users\[Username]\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup
  ```

### 3. Lock Komputer Kasir
- Gunakan Windows Kiosk Mode
- Disable Task Manager (Ctrl+Alt+Del)
- Full-screen app only

### 4. Printer Thermal Setup
- Install driver printer thermal
- Set as default printer
- Test dari aplikasi (tombol Cetak Struk)

---

## 📞 Support & Maintenance

### Update Aplikasi:
1. Backup folder `data/` dulu
2. Copy folder aplikasi baru
3. Paste folder `data/` lama
4. Jalankan ulang

### Troubleshooting Production:
- Cek folder `data/` size tidak terlalu besar (>100MB)
- Arsip transaksi lama (pindah ke `data_archive/`)
- Restart aplikasi setiap hari (via Task Scheduler)

---

**Kesimpulan:** 
Aplikasi ini **siap pakai langsung** tanpa hosting. 
Deploy ke komputer kasir, install Java, jalankan `start.bat`. Selesai! 🎉
