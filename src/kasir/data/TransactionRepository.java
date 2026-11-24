package kasir.data;

import kasir.model.Transaction;
import kasir.model.TransactionItem;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Menyimpan transaksi di data/transaksi.csv dan data/transaksi_item.csv. */
public class TransactionRepository {

    private static final String[] TX_HEADER = {
            "invoice", "waktu", "kasir", "subtotal", "diskon", "pajak", "total", "metode", "dibayar", "kembali"
    };
    private static final String[] ITEM_HEADER = {
            "invoice", "produk_id", "nama", "qty", "harga", "subtotal"
    };
    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Path txFile;
    private final Path itemFile;
    private List<Transaction> cache;

    public TransactionRepository(Path dataDir) {
        if (dataDir == null) {
            throw new IllegalArgumentException("dataDir tidak boleh null");
        }
        this.txFile = dataDir.resolve("transaksi.csv");
        this.itemFile = dataDir.resolve("transaksi_item.csv");
    }

    public void append(Transaction transaction) {
        List<String[]> txRows = java.util.Collections.singletonList(new String[]{
                transaction.getInvoice(),
                transaction.getTime().toString(),
                transaction.getCashier(),
                String.valueOf(transaction.getSubtotal()),
                String.valueOf(transaction.getDiscount()),
                String.valueOf(transaction.getTax()),
                String.valueOf(transaction.getTotal()),
                transaction.getMethod(),
                String.valueOf(transaction.getPaid()),
                String.valueOf(transaction.getChange())
        });
        List<String[]> itemRows = new ArrayList<>();
        for (TransactionItem item : transaction.getItems()) {
            itemRows.add(new String[]{
                    transaction.getInvoice(),
                    String.valueOf(item.productId()),
                    item.name(),
                    String.valueOf(item.qty()),
                    String.valueOf(item.price()),
                    String.valueOf(item.subtotal())
            });
        }
        try {
            Csv.append(txFile, TX_HEADER, txRows);
            Csv.append(itemFile, ITEM_HEADER, itemRows);
        } catch (IOException ex) {
            throw new DataException("Gagal menyimpan transaksi: " + ex.getMessage(), ex);
        }
        if (cache != null) {
            cache.add(transaction);
        }
    }

    public List<Transaction> findAll() {
        if (cache == null) {
            cache = load();
        }
        return new ArrayList<>(cache);
    }

    private List<Transaction> load() {
        Map<String, List<TransactionItem>> itemsByInvoice = new HashMap<>();
        try {
            for (String[] row : Csv.read(itemFile)) {
                if (row.length < 6 || "invoice".equalsIgnoreCase(row[0].trim())) {
                    continue;
                }
                TransactionItem item = new TransactionItem(
                        parseLong(row[1]),
                        row[2],
                        (int) parseLong(row[3]),
                        parseLong(row[4]),
                        parseLong(row[5]));
                itemsByInvoice.computeIfAbsent(row[0], key -> new ArrayList<>()).add(item);
            }

            List<Transaction> result = new ArrayList<>();
            for (String[] row : Csv.read(txFile)) {
                if (row.length < 10 || "invoice".equalsIgnoreCase(row[0].trim())) {
                    continue;
                }
                try {
                    result.add(new Transaction(
                            row[0],
                            LocalDateTime.parse(row[1]),
                            row[2],
                            parseLong(row[3]),
                            parseLong(row[4]),
                            parseLong(row[5]),
                            parseLong(row[6]),
                            row[7],
                            parseLong(row[8]),
                            parseLong(row[9]),
                            itemsByInvoice.getOrDefault(row[0], List.of())));
                } catch (RuntimeException ignored) {
                    // lewati baris rusak
                }
            }
            return result;
        } catch (IOException ex) {
            throw new DataException("Gagal membaca transaksi: " + ex.getMessage(), ex);
        }
    }

    /** Nomor invoice berikutnya, contoh INV-20261123-0001. */
    public String nextInvoice() {
        LocalDate today = LocalDate.now();
        String prefix = "INV-" + today.format(BASIC_DATE) + "-";
        int max = 0;
        
        // Hanya proses invoice hari ini saja, lebih efisien
        for (Transaction transaction : findAll()) {
            String invoice = transaction.getInvoice();
            // Pastikan invoice dari hari ini
            if (!invoice.startsWith(prefix)) {
                continue;
            }
            try {
                int num = Integer.parseInt(invoice.substring(prefix.length()));
                max = Math.max(max, num);
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                // abaikan invoice dengan format tidak valid
            }
        }
        return prefix + String.format("%04d", max + 1);
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
