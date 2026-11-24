package kasir.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Format dan parsing angka Rupiah (tanpa desimal). */
public final class Money {

    private static final DecimalFormat FORMAT;

    static {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.of("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        FORMAT = new DecimalFormat("#,##0", symbols);
    }

    private Money() {
    }

    /** Contoh: 15000 -> "Rp 15.000". */
    public static String rupiah(long value) {
        return "Rp " + plain(value);
    }

    /** Contoh: 15000 -> "15.000". */
    public static String plain(long value) {
        return FORMAT.format(value);
    }

    /** Mengambil semua digit dari teks, mengabaikan titik, "Rp", spasi, dan simbol lain. */
    public static long parse(String text) {
        if (text == null) {
            return 0;
        }
        StringBuilder digits = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            }
        }
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Long.parseLong(digits.toString());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
