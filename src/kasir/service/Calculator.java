package kasir.service;

import kasir.model.CartItem;

import java.util.List;

/** Perhitungan subtotal, diskon, pajak, dan total. */
public final class Calculator {

    private Calculator() {
    }

    public static Totals calculate(List<CartItem> items, double discountPercent, double taxPercent) {
        long subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getSubtotal();
        }

        long discount = Math.round(subtotal * (discountPercent / 100.0));
        if (discount > subtotal) {
            discount = subtotal;
        }
        if (discount < 0) {
            discount = 0;
        }

        long taxable = subtotal - discount;
        long tax = Math.round(taxable * (taxPercent / 100.0));
        long total = taxable + tax;

        return new Totals(subtotal, discount, tax, total);
    }

    public record Totals(long subtotal, long discount, long tax, long total) {
    }
}
