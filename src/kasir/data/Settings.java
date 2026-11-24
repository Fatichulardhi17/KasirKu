package kasir.data;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Pengaturan toko yang disimpan di data/pengaturan.properties. */
public class Settings {

    private final Path file;

    private String storeName = "Toko Sejahtera";
    private String address = "Jl. Merdeka No. 123, Jakarta";
    private String phone = "021-555-0123";
    private String cashier = "Kasir 1";
    private String footer = "Terima kasih atas kunjungan Anda";
    private double taxRate = 11.0;

    public Settings(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("file tidak boleh null");
        }
        this.file = file;
        load();
    }

    private void load() {
        if (!Files.exists(file)) {
            return;
        }
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (IOException ex) {
            throw new DataException("Gagal membaca pengaturan: " + ex.getMessage(), ex);
        }
        storeName = props.getProperty("toko.nama", storeName);
        address = props.getProperty("toko.alamat", address);
        phone = props.getProperty("toko.telepon", phone);
        cashier = props.getProperty("kasir.nama", cashier);
        footer = props.getProperty("struk.footer", footer);
        try {
            taxRate = Double.parseDouble(props.getProperty("pajak.persen", String.valueOf(taxRate)));
        } catch (NumberFormatException ignored) {
            taxRate = 11.0;
        }
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty("toko.nama", storeName);
        props.setProperty("toko.alamat", address);
        props.setProperty("toko.telepon", phone);
        props.setProperty("kasir.nama", cashier);
        props.setProperty("struk.footer", footer);
        props.setProperty("pajak.persen", String.valueOf(taxRate));
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                props.store(writer, "Pengaturan aplikasi kasir");
            }
        } catch (IOException ex) {
            throw new DataException("Gagal menyimpan pengaturan: " + ex.getMessage(), ex);
        }
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }
}
