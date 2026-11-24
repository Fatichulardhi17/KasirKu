# 🚀 Cara Menjalankan Aplikasi Kasir

## Prasyarat
- **Java JDK 11 atau lebih baru** harus sudah terinstall
- Untuk cek versi Java Anda, buka Command Prompt dan ketik:
  ```
  java -version
  ```

---

## 📦 Cara 1: Menggunakan Script Start (PALING MUDAH)

### Windows:
1. **Double-click** file `start.bat`
   
   ATAU
   
2. Buka Command Prompt di folder ini, lalu ketik:
   ```
   start.bat
   ```

Script ini akan:
- ✅ Auto-compile jika belum dikompilasi
- ✅ Membuat folder `data/` jika belum ada
- ✅ Menjalankan aplikasi
- ✅ Menampilkan error jika ada masalah

---

## 📦 Cara 2: Menggunakan Script Run.bat (Original)

Jalankan di Command Prompt:
```
run.bat
```

---

## 📦 Cara 3: Manual (Step by Step)

### Step 1: Kompilasi
```
build.bat
```

### Step 2: Jalankan
```
java -cp out kasir.Main
```

---

## 🎯 Apa yang Terjadi Saat Pertama Kali Jalan?

1. **Folder `data/` akan dibuat** otomatis
2. **File CSV akan dibuat** otomatis:
   - `data/produk.csv` - Data produk (akan diisi dengan 15 produk contoh)
   - `data/transaksi.csv` - Riwayat transaksi
   - `data/transaksi_item.csv` - Detail item transaksi
   - `data/pengaturan.properties` - Pengaturan toko

3. **Aplikasi GUI akan muncul** dengan tampilan:
   - Panel kiri: Grid produk
   - Panel kanan: Keranjang belanja
   - Tombol atas: Kelola Produk, Riwayat, Pengaturan

---

## 🖱️ Cara Menggunakan Aplikasi

### Melakukan Transaksi:
1. **Klik produk** di panel kiri untuk menambah ke keranjang
2. **Edit quantity** langsung di tabel keranjang (kolom Qty)
3. **Atur diskon** jika perlu (dalam %)
4. **Klik tombol BAYAR** atau tekan `F9`
5. **Pilih metode pembayaran** (Tunai/QRIS/Debit/Kredit)
6. **Masukkan jumlah uang** (untuk pembayaran tunai)
7. **Konfirmasi** dan struk akan muncul

### Shortcut Keyboard:
- `F2` - Fokus ke pencarian produk
- `F9` - Proses pembayaran
- `F4` - Kosongkan keranjang
- `Delete` - Hapus item terpilih dari keranjang

### Kelola Produk:
1. Klik tombol **"Kelola Produk"** di toolbar
2. Klik **"+ Tambah Produk"** untuk produk baru
3. **Double-click** baris untuk edit
4. **Hapus** produk yang tidak diperlukan

### Lihat Riwayat:
1. Klik tombol **"Riwayat"** di toolbar
2. Lihat semua transaksi yang pernah dilakukan
3. **Klik transaksi** untuk melihat detail
4. **Cetak ulang struk** jika diperlukan

### Pengaturan:
1. Klik tombol **"Pengaturan"** di toolbar
2. Edit:
   - Nama toko
   - Alamat
   - Telepon
   - Nama kasir
   - PPN (%)
   - Footer struk

---

## 🐛 Troubleshooting

### ❌ Error: "java is not recognized"
**Solusi:** Java belum terinstall atau belum di PATH
- Download Java dari: https://www.oracle.com/java/technologies/downloads/
- Atau gunakan OpenJDK: https://adoptium.net/

### ❌ Error: "javac is not recognized" saat build
**Solusi:** Anda perlu JDK (bukan hanya JRE)
- Install JDK lengkap

### ❌ Aplikasi tidak muncul / langsung tutup
**Solusi:** 
1. Jalankan dari Command Prompt (bukan double-click) untuk melihat error
2. Pastikan folder `out/` ada hasil kompilasi
3. Coba hapus folder `out/` dan compile ulang:
   ```
   rmdir /s /q out
   build.bat
   start.bat
   ```

### ❌ Error: "Gagal membaca data produk"
**Solusi:**
1. Pastikan folder `data/` ada dan bisa ditulis
2. Jika file CSV corrupt, hapus file `data/produk.csv` dan jalankan ulang
   (akan auto-generate data contoh)

### ❌ Tombol tidak bisa diklik / UI freeze
**Solusi:**
- Pastikan menggunakan Java 11 atau lebih baru
- Coba restart aplikasi

---

## 📁 Struktur File Penting

```
kasir/
├── start.bat              ← JALANKAN INI! (Rekomendasi)
├── run.bat                ← Atau ini (original)
├── build.bat              ← Untuk compile manual
├── data/                  ← Data aplikasi (auto-generated)
│   ├── produk.csv
│   ├── transaksi.csv
│   ├── transaksi_item.csv
│   └── pengaturan.properties
├── out/                   ← Hasil kompilasi
│   └── kasir/
└── src/                   ← Source code
    └── kasir/
```

---

## 💾 Backup Data

Untuk backup data transaksi dan produk Anda, cukup copy folder `data/`:
```
xcopy data data_backup\ /E /I
```

Untuk restore:
```
xcopy data_backup\* data\ /E /Y
```

---

## 🎨 Fitur Aplikasi

✅ Point of Sale (POS) lengkap dengan UI modern  
✅ Manajemen produk (tambah, edit, hapus)  
✅ Keranjang belanja dengan diskon  
✅ Multi metode pembayaran (Tunai, QRIS, Debit, Kredit)  
✅ Perhitungan kembalian otomatis  
✅ Riwayat transaksi lengkap  
✅ Cetak struk (virtual printer)  
✅ Pengaturan toko  
✅ Keyboard shortcuts  
✅ Validasi stok real-time  
✅ Pencarian produk  
✅ Filter kategori  

---

## 📞 Butuh Bantuan?

Jika menemui masalah:
1. Cek file `CHANGELOG_FIXES.md` untuk informasi perbaikan bug
2. Pastikan semua prasyarat terpenuhi (Java JDK installed)
3. Jalankan dari Command Prompt untuk melihat error message

---

**Selamat menggunakan! 🎉**
