package kasir.ui;

import kasir.model.CartItem;
import kasir.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Model tabel keranjang belanja. */
public class CartModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"Produk", "Qty", "Harga", "Subtotal"};

    private final List<CartItem> items = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();

    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyChanged() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int qtyOf(long productId) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                return item.getQty();
            }
        }
        return 0;
    }

    public boolean add(Product product) {
        // Validasi stok dulu
        if (product.getStock() <= 0) {
            return false;
        }
        
        for (int row = 0; row < items.size(); row++) {
            CartItem item = items.get(row);
            if (item.getProduct().getId() == product.getId()) {
                if (item.getQty() >= product.getStock()) {
                    return false;
                }
                item.setQty(item.getQty() + 1);
                fireTableRowsUpdated(row, row);
                notifyChanged();
                return true;
            }
        }
        items.add(new CartItem(product, 1));
        int row = items.size() - 1;
        fireTableRowsInserted(row, row);
        notifyChanged();
        return true;
    }

    public void increase(int row) {
        if (row < 0 || row >= items.size()) {
            return;
        }
        CartItem item = items.get(row);
        if (item.getQty() >= item.getProduct().getStock()) {
            return;
        }
        item.setQty(item.getQty() + 1);
        fireTableRowsUpdated(row, row);
        notifyChanged();
    }

    public void decrease(int row) {
        if (row < 0 || row >= items.size()) {
            return;
        }
        CartItem item = items.get(row);
        if (item.getQty() <= 1) {
            items.remove(row);
            fireTableRowsDeleted(row, row);
        } else {
            item.setQty(item.getQty() - 1);
            fireTableRowsUpdated(row, row);
        }
        notifyChanged();
    }

    public void remove(int row) {
        if (row < 0 || row >= items.size()) {
            return;
        }
        items.remove(row);
        fireTableRowsDeleted(row, row);
        notifyChanged();
    }

    public void clear() {
        if (items.isEmpty()) {
            return;
        }
        items.clear();
        fireTableDataChanged();
        notifyChanged();
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == 1 ? Integer.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        CartItem item = items.get(row);
        return switch (column) {
            case 0 -> item.getProduct().getName();
            case 1 -> item.getQty();
            case 2 -> item.getProduct().getPrice();
            default -> item.getSubtotal();
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column != 1 || row < 0 || row >= items.size()) {
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            fireTableRowsUpdated(row, row);
            return;
        }
        CartItem item = items.get(row);
        // Perbaikan: Tidak boleh pesan jika stok 0
        int max = item.getProduct().getStock();
        if (max <= 0) {
            fireTableRowsUpdated(row, row);
            return;
        }
        item.setQty(Math.max(1, Math.min(qty, max)));
        fireTableRowsUpdated(row, row);
        notifyChanged();
    }
}
