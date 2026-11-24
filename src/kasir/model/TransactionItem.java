package kasir.model;

/** Salinan item transaksi (harga dikunci saat pembayaran). */
public record TransactionItem(long productId, String name, int qty, long price, long subtotal) {

    public static TransactionItem of(CartItem item) {
        return new TransactionItem(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQty(),
                item.getProduct().getPrice(),
                item.getSubtotal());
    }
}
