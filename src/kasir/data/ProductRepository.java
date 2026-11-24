package kasir.data;

import kasir.model.CartItem;
import kasir.model.Product;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Menyimpan daftar produk di data/produk.csv. */
public class ProductRepository {

    private static final String[] HEADER = {"id", "nama", "kategori", "harga", "stok"};

    private final Path file;
    private final List<Product> products = new ArrayList<>();

    public ProductRepository(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("file tidak boleh null");
        }
        this.file = file;
        load();
    }

    private void load() {
        try {
            List<String[]> rows = Csv.read(file);
            for (String[] row : rows) {
                if (row.length < 5 || "id".equalsIgnoreCase(row[0].trim())) {
                    continue;
                }
                // Validasi field tidak boleh kosong
                if (row[0].trim().isEmpty() || row[1].trim().isEmpty() || 
                    row[2].trim().isEmpty() || row[3].trim().isEmpty() || row[4].trim().isEmpty()) {
                    continue;
                }
                try {
                    products.add(new Product(
                            Long.parseLong(row[0].trim()),
                            row[1].trim(),
                            row[2].trim(),
                            Long.parseLong(row[3].trim()),
                            Integer.parseInt(row[4].trim())));
                } catch (NumberFormatException ignored) {
                    // lewati baris rusak
                }
            }
            if (products.isEmpty()) {
                seed();
                save();
            }
        } catch (IOException ex) {
            throw new DataException("Gagal membaca data produk: " + ex.getMessage(), ex);
        }
    }

    private void seed() {
        products.add(new Product(1, "Kopi Susu Gula Aren", "Minuman", 18000, 40));
        products.add(new Product(2, "Americano", "Minuman", 15000, 35));
        products.add(new Product(3, "Teh Manis", "Minuman", 8000, 50));
        products.add(new Product(4, "Air Mineral 600ml", "Minuman", 5000, 80));
        products.add(new Product(5, "Es Jeruk", "Minuman", 12000, 30));
        products.add(new Product(6, "Roti Bakar Coklat", "Makanan", 20000, 25));
        products.add(new Product(7, "Nasi Goreng Spesial", "Makanan", 25000, 20));
        products.add(new Product(8, "Mie Goreng", "Makanan", 22000, 20));
        products.add(new Product(9, "Ayam Geprek", "Makanan", 28000, 15));
        products.add(new Product(10, "Kentang Goreng", "Snack", 18000, 30));
        products.add(new Product(11, "Pisang Goreng", "Snack", 15000, 25));
        products.add(new Product(12, "Donat Gula", "Snack", 10000, 40));
        products.add(new Product(13, "Keripik Singkong", "Snack", 12000, 60));
        products.add(new Product(14, "Cokelat Batang", "Snack", 15000, 45));
        products.add(new Product(15, "Susu UHT 250ml", "Minuman", 7000, 70));
    }

    public List<Product> findAll() {
        return Collections.unmodifiableList(products);
    }

    public Product findById(long id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public long nextId() {
        long max = 0;
        for (Product product : products) {
            max = Math.max(max, product.getId());
        }
        return max + 1;
    }

    public void add(Product product) {
        products.add(product);
        save();
    }

    public void update(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                save();
                return;
            }
        }
    }

    public void delete(long id) {
        products.removeIf(product -> product.getId() == id);
        save();
    }

    /** Mengurangi stok sesuai isi keranjang lalu menyimpan sekali saja. */
    public void applySale(List<CartItem> items) {
        // Validasi dulu semua produk ada sebelum update
        for (CartItem item : items) {
            Product product = findById(item.getProduct().getId());
            if (product == null) {
                throw new DataException("Produk dengan ID " + item.getProduct().getId() + " tidak ditemukan");
            }
            if (product.getStock() < item.getQty()) {
                throw new DataException("Stok tidak cukup untuk produk: " + product.getName() + 
                    " (tersedia: " + product.getStock() + ", diminta: " + item.getQty() + ")");
            }
        }
        
        // Baru update stok
        for (CartItem item : items) {
            Product product = findById(item.getProduct().getId());
            product.setStock(product.getStock() - item.getQty());
        }
        save();
    }

    public void save() {
        List<String[]> rows = new ArrayList<>();
        rows.add(HEADER);
        for (Product product : products) {
            rows.add(new String[]{
                    String.valueOf(product.getId()),
                    product.getName(),
                    product.getCategory(),
                    String.valueOf(product.getPrice()),
                    String.valueOf(product.getStock())
            });
        }
        try {
            Csv.write(file, rows);
        } catch (IOException ex) {
            throw new DataException("Gagal menyimpan data produk: " + ex.getMessage(), ex);
        }
    }
}
