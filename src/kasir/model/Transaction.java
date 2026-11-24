package kasir.model;

import java.time.LocalDateTime;
import java.util.List;

/** Satu transaksi pembayaran yang sudah selesai. */
public class Transaction {

    private final String invoice;
    private final LocalDateTime time;
    private final String cashier;
    private final long subtotal;
    private final long discount;
    private final long tax;
    private final long total;
    private final String method;
    private final long paid;
    private final long change;
    private final List<TransactionItem> items;

    public Transaction(String invoice,
                       LocalDateTime time,
                       String cashier,
                       long subtotal,
                       long discount,
                       long tax,
                       long total,
                       String method,
                       long paid,
                       long change,
                       List<TransactionItem> items) {
        this.invoice = invoice;
        this.time = time;
        this.cashier = cashier;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
        this.method = method;
        this.paid = paid;
        this.change = change;
        this.items = List.copyOf(items);
    }

    public String getInvoice() {
        return invoice;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getCashier() {
        return cashier;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public long getDiscount() {
        return discount;
    }

    public long getTax() {
        return tax;
    }

    public long getTotal() {
        return total;
    }

    public String getMethod() {
        return method;
    }

    public long getPaid() {
        return paid;
    }

    public long getChange() {
        return change;
    }

    public List<TransactionItem> getItems() {
        return items;
    }
}
