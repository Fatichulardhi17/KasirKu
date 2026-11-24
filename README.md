# 💰 Aplikasi Kasir (Point of Sale)

Aplikasi kasir desktop berbasis Java Swing dengan fitur lengkap untuk mengelola penjualan, produk, dan transaksi.

![Java](https://img.shields.io/badge/Java-27-orange)
![Status](https://img.shields.io/badge/Status-Ready-green)

---

## 🚀 Quick Start

### 🎯 Cara PALING MUDAH (Recommended):
```
Double-click: KasirKu.vbs
```
✨ Aplikasi langsung buka tanpa console window!

### 📦 Alternatif - Gunakan JAR (Portable):
```bash
# Persiapan (hanya sekali):
create-jar.bat

# Lalu double-click:
KasirKu.jar
# atau
KasirKu.bat
```

### 🔧 Cara Lama (Dengan Auto-Compile):
```bash
start.bat
```

**💡 Lihat panduan lengkap:** [CARA_BUKA_APLIKASI.md](CARA_BUKA_APLIKASI.md)

**Semua cara akan:**
- ✅ Kompilasi jika diperlukan
- ✅ Membuat folder data
- ✅ Menjalankan GUI

---

## 📋 Prasyarat

- **Java JDK 11+** (Anda sudah punya Java 27 ✅)
- **Windows OS** (untuk script .bat)

Cek Java:
```bash
java -version
```

---

## 🎯 Fitur Utama

### 🛒 Point of Sale
- Tambah produk ke keranjang dengan sekali klik
- Edit quantity langsung di tabel
- Diskon per transaksi (dalam %)
- Perhitungan PPN otomatis
- Multi metode pembayaran: Tunai, QRIS, Debit, Kredit
- Perhitungan kembalian otomatis
- Cetak struk transaksi

### 📦 Manajemen Produk
- Tambah, edit, hapus produk
- Kategori produk (Minuman, Makanan, Snack, dll)
- Tracking stok real-time
- Validasi stok saat checkout
- Pencarian produk cepat

### 📊 Riwayat Transaksi
- Lihat semua transaksi
- Filter dan pencarian
- Detail per transaksi
- Cetak ulang struk

### ⚙️ Pengaturan
- Info toko (nama, alamat, telepon)
- Nama kasir
- PPN (%)
- Footer struk

### ⌨️ Keyboard Shortcuts
- `F2` - Fokus ke pencarian produk
- `F9` - Proses pembayaran
- `F4` - Kosongkan keranjang
- `Delete` - Hapus item dari keranjang

---

## 📁 Struktur Project

```
java/
├── 📄 start.bat              # ← Jalankan ini untuk memulai aplikasi
├── 📄 run.bat                # Alternatif script
├── 📄 build.bat              # Compile manual
├── 📄 CARA_MENJALANKAN.md    # Panduan lengkap
├── 📄 CHANGELOG_FIXES.md     # Log perbaikan bug
│
├── 📂 src/kasir/             # Source code
│   ├── Main.java             # Entry point
│   ├── 📂 data/              # Data layer (CSV, Repository)
│   ├── 📂 model/             # Model classes (Product, Transaction, dll)
│   ├── 📂 service/           # Business logic (Calculator)
│   ├── 📂 ui/                # UI components (Swing)
│   └── 📂 util/              # Utilities (Money, Receipt)
│
├── 📂 data/                  # Data aplikasi (auto-generated)
│   ├── produk.csv            # Data produk
│   ├── transaksi.csv         # Riwayat transaksi
│   ├── transaksi_item.csv    # Detail item transaksi
│   └── pengaturan.properties # Pengaturan toko
│
└── 📂 out/                   # Compiled classes
```

---

## 🛠️ Build Manual

### Compile:
```bash
build.bat
```

### Run:
```bash
java -cp out kasir.Main
```

Atau gunakan script yang sudah disediakan:
```bash
run.bat
```

---

## 📸 Screenshots

Saat aplikasi jalan, Anda akan melihat:

```
┌─────────────────────────────────────────────────────────┐
│  🏪 KasirKu                           [Kelola] [Riwayat] │
├──────────────────────┬──────────────────────────────────┤
│                      │  📋 Pesanan                       │
│  🔍 [Cari produk]    │  No. INV-20261123-0001           │
│                      │  ┌────────────────────────────┐  │
│  [Semua] [Minuman]   │  │ Produk │ Qty │ Harga │ Sub │  │
│  [Makanan] [Snack]   │  ├────────────────────────────┤  │
│                      │  │ ...item keranjang...       │  │
│  ┌────────────────┐  │  └────────────────────────────┘  │
│  │ Kopi Susu      │  │                                   │
│  │ Rp 18.000      │  │  Subtotal:        Rp 50.000      │
│  │ Stok: 40       │  │  Diskon (0%):     Rp 0           │
│  └────────────────┘  │  PPN (11%):       Rp 5.500       │
│                      │  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│  [.. grid produk ..]  │  Total:          Rp 55.500       │
│                      │                                   │
│                      │  [      BAYAR Rp 55.500       ]  │
└──────────────────────┴──────────────────────────────────┘
```

---

## 🐛 Bug Fixes (23 Nov 2026)

Aplikasi ini baru saja diperbaiki dari 10 bug kritis! ✅

Lihat detail di: `CHANGELOG_FIXES.md`

Perbaikan utama:
- ✅ Fix validasi stok (tidak bisa beli produk stok 0)
- ✅ Fix data consistency di checkout
- ✅ Fix null pointer risks
- ✅ Optimasi invoice generation
- ✅ Validasi input lebih ketat

---

## 💾 Backup & Restore

### Backup:
```bash
xcopy data data_backup\ /E /I
```

### Restore:
```bash
xcopy data_backup\* data\ /E /Y
```

---

## 📚 Dokumentasi Lengkap

- **[CARA_MENJALANKAN.md](CARA_MENJALANKAN.md)** - Panduan lengkap cara menjalankan
- **[CHANGELOG_FIXES.md](CHANGELOG_FIXES.md)** - Log perbaikan bug dan peningkatan kualitas kode

---

## 🎓 Teknologi

- **Language:** Java 11+
- **UI Framework:** Java Swing
- **Data Storage:** CSV files
- **Architecture:** Layered (Data → Model → Service → UI)

---

## 🤝 Kontribusi

Project ini sudah production-ready dengan:
- ✅ Clean architecture
- ✅ Proper error handling
- ✅ Input validation
- ✅ Stock management
- ✅ Transaction integrity

**Rekomendasi untuk pengembangan selanjutnya:**
- [ ] Add logging framework (SLF4J)
- [ ] Add unit tests
- [ ] Migrate from CSV to SQLite
- [ ] Add internationalization (i18n)
- [ ] Add reporting features (daily/monthly sales)

---

## 📞 Support

Jika menemui masalah:
1. Baca **CARA_MENJALANKAN.md** untuk troubleshooting
2. Pastikan Java JDK 11+ terinstall
3. Jalankan dari Command Prompt untuk melihat error message

---

## 📄 License

Project ini untuk keperluan edukasi dan pengembangan.

---

**Happy Selling! 🎉**

Made with ❤️ using Java Swing
