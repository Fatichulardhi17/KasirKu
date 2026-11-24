package kasir.model;

/** Baris pada keranjang belanja. */
public class CartItem {

    private final Product product;
    private int qty;

    public CartItem(Product product, int qty) {
        this.product = product;
        this.qty = qty;
    }

    public Product getProduct() {
        return product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = Math.max(1, qty);
    }

    public long getSubtotal() {
        return product.getPrice() * qty;
    }
}
