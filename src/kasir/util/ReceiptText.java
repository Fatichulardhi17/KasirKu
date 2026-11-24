package kasir.util;

import kasir.data.Settings;
import kasir.model.Transaction;
import kasir.model.TransactionItem;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Pembuat teks struk belanja gaya printer thermal 42 karakter. */
public final class ReceiptText {

    private static final int WIDTH = 42;
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.of("id", "ID"));

    private ReceiptText() {
    }

    public static String build(Transaction transaction, Settings settings) {
        StringBuilder sb = new StringBuilder();
        center(sb, settings.getStoreName());
        if (!settings.getAddress().isBlank()) {
            center(sb, settings.getAddress());
        }
        if (!settings.getPhone().isBlank()) {
            center(sb, "Telp. " + settings.getPhone());
        }
        rule(sb, '=');
        kv(sb, "No.", transaction.getInvoice());
        kv(sb, "Tanggal", DATE.format(transaction.getTime()));
        kv(sb, "Kasir", transaction.getCashier());
        rule(sb, '-');

        for (TransactionItem item : transaction.getItems()) {
            sb.append(item.qty()).append("x ").append(truncate(item.name(), 28)).append('\n');
            kv(sb, "   @ " + Money.plain(item.price()), Money.plain(item.subtotal()));
        }

        rule(sb, '-');
        kv(sb, "Subtotal", Money.rupiah(transaction.getSubtotal()));
        if (transaction.getDiscount() > 0) {
            kv(sb, "Diskon", "-" + Money.rupiah(transaction.getDiscount()));
        }
        kv(sb, "PPN", Money.rupiah(transaction.getTax()));
        kv(sb, "TOTAL", Money.rupiah(transaction.getTotal()));
        kv(sb, transaction.getMethod(), Money.rupiah(transaction.getPaid()));
        kv(sb, "Kembali", Money.rupiah(transaction.getChange()));
        rule(sb, '=');

        if (!settings.getFooter().isBlank()) {
            center(sb, settings.getFooter());
        }
        center(sb, "Simpan struk ini sebagai bukti pembayaran");
        return sb.toString();
    }

    private static void center(StringBuilder sb, String text) {
        for (String line : wrap(text, WIDTH)) {
            int padding = Math.max(0, (WIDTH - line.length()) / 2);
            sb.append(" ".repeat(padding)).append(line).append('\n');
        }
    }

    private static void kv(StringBuilder sb, String left, String right) {
        int space = Math.max(1, WIDTH - left.length() - right.length());
        sb.append(left).append(" ".repeat(space)).append(right).append('\n');
    }

    private static void rule(StringBuilder sb, char character) {
        sb.append(String.valueOf(character).repeat(WIDTH)).append('\n');
    }

    private static String truncate(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 1) + "…";
    }

    private static List<String> wrap(String text, int width) {
        List<String> lines = new ArrayList<>();
        String remaining = text;
        while (remaining.length() > width) {
            int cut = remaining.lastIndexOf(' ', width);
            if (cut <= 0) {
                cut = width;
            }
            lines.add(remaining.substring(0, cut).trim());
            remaining = remaining.substring(cut).trim();
        }
        lines.add(remaining);
        return lines;
    }
}
