# 📝 Git Guide - Aplikasi KasirKu

## ✅ Repository GitHub

**URL:** https://github.com/Fatichulardhi17/KasirKu.git

**Status:** ✅ Berhasil di-push ke GitHub

---

## 📊 Informasi Repository

- **Branch:** main
- **Initial Commit:** 59fc32d
- **Total Files:** 56 files
- **Total Lines:** 6,494 lines of code

---

## 🔄 Workflow Git untuk Update

### 1. Cek Status
```bash
git status
```

### 2. Tambah File Baru atau Perubahan
```bash
# Tambah semua file yang berubah
git add .

# Atau tambah file spesifik
git add src/kasir/Main.java
git add README.md
```

### 3. Commit Perubahan
```bash
git commit -m "Deskripsi perubahan Anda"
```

**Contoh commit messages yang baik:**
```bash
# Untuk fitur baru
git commit -m "feat: Tambah fitur diskon khusus member"

# Untuk bug fix
git commit -m "fix: Perbaiki kalkulasi PPN"

# Untuk dokumentasi
git commit -m "docs: Update README dengan screenshot"

# Untuk refactoring
git commit -m "refactor: Optimize query database produk"
```

### 4. Push ke GitHub
```bash
git push
```

---

## 🌿 Bekerja dengan Branch

### Membuat Branch Baru
```bash
# Buat dan pindah ke branch baru
git checkout -b feature/nama-fitur

# Contoh
git checkout -b feature/laporan-penjualan
git checkout -b bugfix/stok-negatif
```

### Push Branch Baru
```bash
git push -u origin feature/nama-fitur
```

### Pindah Branch
```bash
git checkout main
git checkout feature/nama-fitur
```

### Merge Branch
```bash
# Pindah ke main dulu
git checkout main

# Merge branch fitur
git merge feature/nama-fitur

# Push hasil merge
git push
```

### Hapus Branch (Setelah Merge)
```bash
# Hapus branch local
git branch -d feature/nama-fitur

# Hapus branch remote
git push origin --delete feature/nama-fitur
```

---

## 📥 Clone Repository (Di Komputer Lain)

```bash
# Clone repository
git clone https://github.com/Fatichulardhi17/KasirKu.git

# Masuk ke folder
cd KasirKu

# Compile aplikasi
build.bat

# Jalankan aplikasi
start.bat
```

---

## 🔙 Undo Changes

### Undo Perubahan Belum di-commit
```bash
# Buang perubahan 1 file
git checkout -- nama-file.java

# Buang semua perubahan
git checkout -- .
```

### Undo Last Commit (Keep Changes)
```bash
git reset --soft HEAD~1
```

### Undo Last Commit (Discard Changes)
```bash
git reset --hard HEAD~1
```

### Undo Changes yang Sudah di-commit
```bash
# Buat commit baru yang membatalkan commit sebelumnya
git revert HEAD

# Atau revert commit spesifik
git revert <commit-hash>
```

---

## 📜 Lihat History

### Log Commits
```bash
# Log sederhana
git log --oneline

# Log dengan graph
git log --graph --oneline --all

# Log dengan detail
git log -p

# Log file spesifik
git log -- src/kasir/Main.java
```

### Lihat Perubahan
```bash
# Lihat perubahan belum di-commit
git diff

# Lihat perubahan sudah di-stage
git diff --staged

# Lihat perubahan 2 commit
git diff commit1 commit2
```

---

## 🏷️ Tagging (Versioning)

### Buat Tag
```bash
# Lightweight tag
git tag v1.0.0

# Annotated tag (recommended)
git tag -a v1.0.0 -m "Release version 1.0.0 - Initial stable release"
```

### Push Tag
```bash
# Push 1 tag
git push origin v1.0.0

# Push semua tags
git push --tags
```

### List Tags
```bash
git tag
```

### Checkout Tag
```bash
git checkout v1.0.0
```

---

## 🔍 Search in Code

### Grep dalam Git
```bash
# Cari text di semua file
git grep "search term"

# Cari di file Java saja
git grep "search term" -- "*.java"

# Cari dengan line number
git grep -n "search term"
```

---

## 🚨 .gitignore

File `.gitignore` sudah dikonfigurasi untuk ignore:
- ✅ Compiled classes (`*.class`, `out/`)
- ✅ IDE files (`.vscode/`, `.idea/`)
- ✅ OS files (`.DS_Store`, `Thumbs.db`)
- ✅ Temporary files (`*.tmp`, `*.bak`)
- ⚠️ JAR file di-include (kecuali `KasirKu.jar`)

### Edit .gitignore
Jika ingin mengubah file yang di-ignore, edit file `.gitignore`:
```bash
notepad .gitignore
```

---

## 📦 Stashing (Simpan Perubahan Sementara)

### Simpan Perubahan
```bash
# Simpan semua perubahan
git stash

# Simpan dengan pesan
git stash save "Work in progress: fitur pembayaran"
```

### Lihat Stash
```bash
git stash list
```

### Ambil Stash
```bash
# Ambil stash terakhir
git stash pop

# Ambil stash spesifik
git stash pop stash@{0}
```

### Hapus Stash
```bash
# Hapus stash terakhir
git stash drop

# Hapus semua stash
git stash clear
```

---

## 🔄 Pull Changes dari GitHub

### Update dari Remote
```bash
# Fetch changes
git fetch origin

# Pull changes (fetch + merge)
git pull origin main

# Atau pull dengan rebase
git pull --rebase origin main
```

---

## 🤝 Kolaborasi

### Setup Git User (Jika Belum)
```bash
# Global config
git config --global user.name "Nama Anda"
git config --global user.email "email@example.com"

# Local config (hanya untuk repo ini)
git config user.name "Nama Anda"
git config user.email "email@example.com"
```

### Cek Config
```bash
git config user.name
git config user.email
git config --list
```

---

## 📋 Checklist Workflow Harian

### Pagi (Sebelum Mulai Kerja)
- [ ] `git pull origin main` - Update dari remote
- [ ] `git status` - Cek status repository

### Saat Kerja
- [ ] Edit files
- [ ] Test aplikasi
- [ ] `git status` - Cek file yang berubah

### Sebelum Pulang
- [ ] `git add .` - Stage semua perubahan
- [ ] `git commit -m "Deskripsi perubahan"` - Commit
- [ ] `git push` - Push ke GitHub
- [ ] Verify di GitHub web

---

## 🆘 Troubleshooting

### "Permission denied" atau Authentication Error
```bash
# Gunakan GitHub Personal Access Token
# 1. Buat token di: https://github.com/settings/tokens
# 2. Gunakan token sebagai password saat push
```

### "Merge conflict"
```bash
# 1. Edit file yang conflict
# 2. Hapus marker <<<<<<, ======, >>>>>>
# 3. Simpan file
# 4. Add dan commit
git add conflicted-file.java
git commit -m "Resolve merge conflict"
```

### "detached HEAD state"
```bash
# Kembali ke branch main
git checkout main
```

### Push ditolak (rejected)
```bash
# Pull dulu, lalu push
git pull origin main
git push
```

---

## 🔗 Useful Links

- **Repository:** https://github.com/Fatichulardhi17/KasirKu.git
- **Git Documentation:** https://git-scm.com/doc
- **GitHub Guides:** https://guides.github.com/

---

## 📝 Notes

- Repository ini sudah di-push pertama kali pada: **24 November 2026**
- Initial commit hash: `59fc32d`
- Branch default: `main`
- Total 56 files dengan 6,494 baris kode

---

**Happy Coding! 🎉**

*Last updated: November 24, 2026*
