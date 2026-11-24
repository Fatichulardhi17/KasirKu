# 🚀 Cara Mudah Membuka Aplikasi KasirKu

Ada **3 cara super mudah** untuk membuka aplikasi ini!

---

## ⚡ Cara 1: Double-Click VBS (PALING MUDAH - Tanpa Console)

### Langkah:
1. **Double-click file**: `KasirKu.vbs`
2. Selesai! ✅

**Keuntungan:**
- ✅ Tidak muncul console window hitam
- ✅ Langsung buka GUI
- ✅ Paling clean dan profesional

**Catatan:** Jika belum pernah dijalankan, aplikasi akan otomatis kompilasi dulu.

---

## 📦 Cara 2: Gunakan JAR File (Portable)

### Persiapan (Hanya Sekali):
```bash
# Double-click atau jalankan:
create-jar.bat
```

Ini akan membuat:
- `KasirKu.jar` - File aplikasi portable
- `KasirKu.bat` - Launcher simple

### Cara Pakai Setelah JAR Dibuat:

**Opsi A - Double-click JAR:**
```
Double-click: KasirKu.jar
```

**Opsi B - Pakai Launcher BAT:**
```
Double-click: KasirKu.bat
```

**Opsi C - Command Line:**
```bash
java -jar KasirKu.jar
```

**Keuntungan:**
- ✅ File single executable
- ✅ Bisa dicopy ke komputer lain
- ✅ Tidak perlu compile lagi

---

## 🎯 Cara 3: Script Otomatis (Original)

```bash
# Double-click atau jalankan:
start.bat
```

**Keuntungan:**
- ✅ Otomatis compile jika belum
- ✅ Membuat folder data otomatis
- ✅ Menampilkan status di console

---

## 📊 Perbandingan Cara

| Cara | Kemudahan | Console | Auto Compile | Portable |
|------|-----------|---------|--------------|----------|
| **KasirKu.vbs** | ⭐⭐⭐⭐⭐ | ❌ Tidak | ✅ Ya | ❌ |
| **KasirKu.jar** | ⭐⭐⭐⭐ | ❌ Tidak | ❌ | ✅ Ya |
| **start.bat** | ⭐⭐⭐ | ✅ Ya | ✅ Ya | ❌ |

---

## 🎨 Rekomendasi

### Untuk Penggunaan Sehari-hari:
👉 **Gunakan `KasirKu.vbs`** - Paling mudah, langsung buka, tanpa console

### Untuk Distribusi/Deploy:
👉 **Gunakan `KasirKu.jar`** - Portable, bisa dicopy kemana-mana

### Untuk Development/Testing:
👉 **Gunakan `start.bat`** - Lihat log dan error messages

---

## 🔧 Troubleshooting

### "Java tidak ditemukan"
```bash
# Cek Java:
java -version

# Jika tidak ada, install Java JDK 11+
```

### "File tidak ditemukan"
- Pastikan Anda berada di folder yang benar
- Pastikan semua file ada (src/, data/, dll)

### "Kompilasi gagal"
```bash
# Kompilasi manual:
build.bat

# Lihat error message untuk detail
```

### JAR tidak bisa dibuat
```bash
# Pastikan jar command tersedia:
jar --version

# Jika tidak ada, pastikan JDK terinstall (bukan hanya JRE)
```

---

## 🎁 Bonus: Buat Desktop Shortcut

### Windows:
1. Right-click `KasirKu.vbs`
2. Pilih **"Create shortcut"**
3. Drag shortcut ke Desktop
4. Rename menjadi "KasirKu" atau "Aplikasi Kasir"
5. *(Optional)* Right-click shortcut → Properties → Change Icon

Sekarang aplikasi bisa dibuka dari desktop! 🎉

---

## 📂 File-file Launcher

```
java/
├── KasirKu.vbs           ← Double-click ini (RECOMMENDED)
├── KasirKu.jar           ← Atau ini (setelah create-jar.bat)
├── KasirKu.bat           ← Atau ini (launcher BAT)
├── start.bat             ← Atau ini (dengan console)
├── create-jar.bat        ← Jalankan sekali untuk buat JAR
└── create-launcher.bat   ← Auto-run saat create-jar
```

---

## ✨ Quick Reference

| File | Fungsi | Kapan Pakai |
|------|--------|-------------|
| `KasirKu.vbs` | Buka aplikasi (silent) | Sehari-hari ⭐ |
| `KasirKu.jar` | Portable executable | Deploy/distribusi |
| `start.bat` | Buka + auto compile | Development |
| `create-jar.bat` | Buat JAR file | Setup awal (1x) |

---

**Selamat menggunakan! 🎉**

**Tip:** Buat shortcut `KasirKu.vbs` di desktop untuk akses super cepat!
