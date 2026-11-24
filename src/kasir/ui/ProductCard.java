package kasir.ui;

import kasir.model.Product;
import kasir.util.Money;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/** Kartu produk pada katalog kasir. */
public class ProductCard extends RoundedPanel {

    private final Product product;
    private final Consumer<Product> onPick;
    private boolean hovered;

    public ProductCard(Product product, Consumer<Product> onPick) {
        super(new BorderLayout(0, 8), Theme.SURFACE, 14);
        this.product = product;
        this.onPick = onPick;

        setLine(Theme.BORDER);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setPreferredSize(new Dimension(202, 116));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout(0, 3));
        top.setOpaque(false);

        JLabel name = new JLabel(truncate(product.getName(), 21));
        name.setFont(Theme.font(Font.BOLD, 13));
        name.setForeground(product.getStock() > 0 ? Theme.TEXT : Theme.MUTED);

        JLabel category = new JLabel(product.getCategory().toUpperCase());
        category.setFont(Theme.font(Font.BOLD, 9));
        category.setForeground(Theme.MUTED);

        top.add(name, BorderLayout.NORTH);
        top.add(category, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel price = new JLabel(Money.rupiah(product.getPrice()));
        price.setFont(Theme.font(Font.BOLD, 15));
        price.setForeground(product.getStock() > 0 ? Theme.PRIMARY : Theme.MUTED);

        JLabel stock = new JLabel(stockText());
        stock.setFont(Theme.font(Font.PLAIN, 10));
        stock.setForeground(stockColor());

        bottom.add(price, BorderLayout.WEST);
        bottom.add(stock, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (product.getStock() > 0) {
                    hovered = true;
                    setFill(new java.awt.Color(0xF8FAFF));
                    setLine(Theme.PRIMARY);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                setFill(Theme.SURFACE);
                setLine(Theme.BORDER);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (product.getStock() > 0) {
                    onPick.accept(product);
                } else {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                }
            }
        };
        attach(this, mouse);
    }

    private String stockText() {
        if (product.getStock() <= 0) {
            return "Stok habis";
        }
        return "Stok " + product.getStock();
    }

    private java.awt.Color stockColor() {
        if (product.getStock() <= 0) {
            return Theme.DANGER;
        }
        if (product.getStock() <= 5) {
            return Theme.WARNING;
        }
        return Theme.MUTED;
    }

    private void attach(Component component, MouseAdapter mouse) {
        component.addMouseListener(mouse);
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                attach(child, mouse);
            }
        }
    }

    private static String truncate(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 1) + "…";
    }
}
