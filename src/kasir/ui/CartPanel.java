package kasir.ui;

import kasir.model.CartItem;
import kasir.model.Product;
import kasir.service.Calculator;
import kasir.util.Money;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

/** Panel keranjang: daftar item, diskon, total, dan tombol bayar. */
public class CartPanel extends RoundedPanel {

    private final CartModel model = new CartModel();
    private final JTable table;

    private final JLabel invoiceValue = new JLabel("-");
    private final JLabel subtotalValue = valueLabel(Theme.TEXT, false);
    private final JLabel discountValue = valueLabel(Theme.MUTED, true);
    private final JLabel taxLabel = new JLabel("PPN (11%)");
    private final JLabel taxValue = valueLabel(Theme.TEXT, false);
    private final JLabel totalValue = new JLabel(Money.rupiah(0));

    private final JSpinner discountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final FlatButton payButton =
            new FlatButton("BAYAR", Theme.SUCCESS, java.awt.Color.WHITE, 12);

    private final Consumer<String> notifier;
    private Runnable onCheckout;
    private Consumer<Integer> onItemCount;
    private double taxRate;
    private long total;

    public CartPanel(double taxRate, Consumer<String> notifier) {
        super(new BorderLayout(0, 12), Theme.SURFACE, 16);
        this.taxRate = taxRate;
        this.notifier = notifier;

        setBorder(new EmptyBorder(16, 16, 16, 16));
        setPreferredSize(new Dimension(392, 0));

        this.table = buildTable();

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        model.addChangeListener(this::recalculate);
        discountSpinner.addChangeListener(e -> recalculate());
        recalculate();
    }

    private static JLabel valueLabel(java.awt.Color color, boolean dangerWhenActive) {
        JLabel label = new JLabel(Money.rupiah(0));
        label.setFont(Theme.font(Font.BOLD, 12));
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JTable buildTable() {
        JTable result = new JTable(model) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (model.getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    String line1 = "Keranjang masih kosong";
                    String line2 = "Klik produk untuk menambahkan";
                    g2.setColor(Theme.MUTED);
                    g2.setFont(Theme.font(Font.BOLD, 13));
                    java.awt.FontMetrics fm1 = g2.getFontMetrics();
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    g2.drawString(line1, centerX - fm1.stringWidth(line1) / 2, centerY - 4);
                    g2.setFont(Theme.font(Font.PLAIN, 11));
                    java.awt.FontMetrics fm2 = g2.getFontMetrics();
                    g2.drawString(line2, centerX - fm2.stringWidth(line2) / 2, centerY + 16);
                    g2.dispose();
                }
            }
        };
        Theme.styleTable(result);
        result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        result.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        TableColumnModel columns = result.getColumnModel();
        columns.getColumn(0).setPreferredWidth(150);
        columns.getColumn(0).setMinWidth(100);
        columns.getColumn(1).setPreferredWidth(42);
        columns.getColumn(1).setMaxWidth(56);
        columns.getColumn(2).setPreferredWidth(86);
        columns.getColumn(3).setPreferredWidth(92);

        DefaultTableCellRenderer nameRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                c.setFont(Theme.font(Font.BOLD, 12));
                setForeground(Theme.TEXT);
                setBorder(new EmptyBorder(0, 8, 0, 4));
                return c;
            }
        };

        DefaultTableCellRenderer qtyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setFont(Theme.font(Font.BOLD, 12));
                setForeground(Theme.TEXT);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 2, 0, 2));
                return c;
            }
        };

        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setFont(Theme.font(Font.PLAIN, 12));
                setForeground(Theme.MUTED);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 4, 0, 6));
                if (value instanceof Long number) {
                    setText(Money.plain(number));
                }
                return c;
            }
        };

        DefaultTableCellRenderer subtotalRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setFont(Theme.font(Font.BOLD, 12));
                setForeground(Theme.TEXT);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 4, 0, 8));
                if (value instanceof Long number) {
                    setText(Money.plain(number));
                }
                return c;
            }
        };

        result.getColumnModel().getColumn(0).setCellRenderer(nameRenderer);
        result.getColumnModel().getColumn(1).setCellRenderer(qtyRenderer);
        result.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);
        result.getColumnModel().getColumn(3).setCellRenderer(subtotalRenderer);

        result.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "hapusItem");
        result.getActionMap().put("hapusItem", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelected();
            }
        });

        return result;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 6));
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Pesanan");
        title.setFont(Theme.font(Font.BOLD, 16));
        title.setForeground(Theme.TEXT);

        JPanel invoiceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        invoiceRow.setOpaque(false);
        JLabel invoiceCaption = new JLabel("No. ");
        invoiceCaption.setFont(Theme.font(Font.PLAIN, 11));
        invoiceCaption.setForeground(Theme.MUTED);
        invoiceValue.setFont(Theme.font(Font.BOLD, 11));
        invoiceValue.setForeground(Theme.MUTED);
        invoiceRow.add(invoiceCaption);
        invoiceRow.add(invoiceValue);

        titles.add(title);
        titles.add(Box.createVerticalStrut(2));
        titles.add(invoiceRow);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        invoiceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        FlatButton clearButton = FlatButton.outline("Kosongkan", Theme.DANGER, Theme.BORDER);
        clearButton.setFont(Theme.font(Font.BOLD, 11));
        clearButton.setBorder(new EmptyBorder(6, 10, 6, 10));
        clearButton.addActionListener(e -> confirmClear());

        header.add(titles, BorderLayout.WEST);
        header.add(clearButton, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildCenter() {
        JScrollPane scroll = Theme.scroll(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.setOpaque(false);

        FlatButton minus = FlatButton.outline("−", Theme.TEXT, Theme.BORDER);
        minus.setFont(Theme.font(Font.BOLD, 15));
        minus.setBorder(new EmptyBorder(4, 0, 4, 0));
        minus.setPreferredSize(new Dimension(42, 32));
        minus.addActionListener(e -> decreaseSelected());

        FlatButton plus = FlatButton.outline("+", Theme.TEXT, Theme.BORDER);
        plus.setFont(Theme.font(Font.BOLD, 15));
        plus.setBorder(new EmptyBorder(4, 0, 4, 0));
        plus.setPreferredSize(new Dimension(42, 32));
        plus.addActionListener(e -> increaseSelected());

        FlatButton delete = FlatButton.outline("Hapus", Theme.DANGER, Theme.BORDER);
        delete.setFont(Theme.font(Font.BOLD, 11));
        delete.setBorder(new EmptyBorder(6, 12, 6, 12));
        delete.addActionListener(e -> deleteSelected());

        actions.add(minus);
        actions.add(plus);
        actions.add(Box.createHorizontalStrut(2));
        actions.add(delete);

        JPanel totals = new JPanel();
        totals.setOpaque(false);
        totals.setLayout(new BoxLayout(totals, BoxLayout.Y_AXIS));

        totals.add(row("Subtotal", subtotalValue));
        totals.add(discountRow());
        taxLabel.setFont(Theme.font(Font.PLAIN, 12));
        taxLabel.setForeground(Theme.MUTED);
        totals.add(rowComponent(taxLabel, taxValue));
        totals.add(Box.createVerticalStrut(6));
        totals.add(separator());
        totals.add(Box.createVerticalStrut(6));

        JLabel totalCaption = new JLabel("Total");
        totalCaption.setFont(Theme.font(Font.BOLD, 14));
        totalCaption.setForeground(Theme.TEXT);
        totalValue.setFont(Theme.font(Font.BOLD, 24));
        totalValue.setForeground(Theme.PRIMARY);
        totalValue.setHorizontalAlignment(SwingConstants.RIGHT);
        totals.add(rowComponent(totalCaption, totalValue));

        payButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        payButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        payButton.setPreferredSize(new Dimension(200, 54));
        payButton.setFont(Theme.font(Font.BOLD, 16));
        payButton.addActionListener(e -> requestCheckout());

        footer.add(actions);
        footer.add(Box.createVerticalStrut(10));
        footer.add(totals);
        footer.add(Box.createVerticalStrut(8));
        footer.add(payButton);
        return footer;
    }

    private JPanel row(String caption, JLabel value) {
        JLabel label = new JLabel(caption);
        label.setFont(Theme.font(Font.PLAIN, 12));
        label.setForeground(Theme.MUTED);
        return rowComponent(label, value);
    }

    private JPanel rowComponent(JLabel caption, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.add(caption, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel discountRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        JLabel caption = new JLabel("Diskon");
        caption.setFont(Theme.font(Font.PLAIN, 12));
        caption.setForeground(Theme.MUTED);
        JLabel percent = new JLabel("%");
        percent.setFont(Theme.font(Font.PLAIN, 11));
        percent.setForeground(Theme.MUTED);

        discountSpinner.setFont(Theme.font(Font.BOLD, 12));
        discountSpinner.setPreferredSize(new Dimension(54, 26));
        discountSpinner.setMaximumSize(new Dimension(54, 26));
        if (discountSpinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            JTextField field = editor.getTextField();
            field.setFont(Theme.font(Font.BOLD, 12));
            field.setHorizontalAlignment(SwingConstants.CENTER);
        }

        left.add(caption);
        left.add(discountSpinner);
        left.add(percent);

        row.add(left, BorderLayout.WEST);
        row.add(discountValue, BorderLayout.EAST);
        return row;
    }

    private JPanel separator() {
        JPanel line = new JPanel();
        line.setBackground(Theme.BORDER);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(10, 1));
        return line;
    }

    public void addProduct(Product product) {
        if (product.getStock() <= 0) {
            notifier.accept("Stok " + product.getName() + " habis.");
            return;
        }
        if (!model.add(product)) {
            notifier.accept("Stok " + product.getName() + " hanya " + product.getStock() + ".");
            return;
        }
        int row = indexOf(product.getId());
        if (row >= 0) {
            table.setRowSelectionInterval(row, row);
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
        }
    }

    private int indexOf(long productId) {
        List<CartItem> items = model.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId() == productId) {
                return i;
            }
        }
        return -1;
    }

    private int targetRow() {
        int row = table.getSelectedRow();
        if (row < 0 && model.getRowCount() > 0) {
            row = model.getRowCount() - 1;
        }
        return row;
    }

    private void increaseSelected() {
        int row = targetRow();
        if (row < 0) {
            return;
        }
        CartItem item = model.getItems().get(row);
        if (item.getQty() >= item.getProduct().getStock()) {
            notifier.accept("Stok " + item.getProduct().getName() + " hanya " + item.getProduct().getStock() + ".");
            return;
        }
        model.increase(row);
        table.setRowSelectionInterval(row, row);
    }

    private void decreaseSelected() {
        int row = targetRow();
        if (row < 0) {
            return;
        }
        model.decrease(row);
        if (model.getRowCount() > 0) {
            int next = Math.min(row, model.getRowCount() - 1);
            table.setRowSelectionInterval(next, next);
        }
    }

    private void deleteSelected() {
        int row = targetRow();
        if (row < 0) {
            return;
        }
        model.remove(row);
        if (model.getRowCount() > 0) {
            int next = Math.min(row, model.getRowCount() - 1);
            table.setRowSelectionInterval(next, next);
        }
    }

    public void confirmClear() {
        if (model.getRowCount() == 0) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Kosongkan semua item di keranjang?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            clear();
        }
    }

    public void clear() {
        model.clear();
        discountSpinner.setValue(0);
        recalculate();
    }

    public void requestCheckout() {
        if (model.getRowCount() == 0) {
            notifier.accept("Keranjang masih kosong.");
            return;
        }
        if (onCheckout != null) {
            onCheckout.run();
        }
    }

    private void recalculate() {
        Calculator.Totals totals = Calculator.calculate(model.getItems(), getDiscountPercent(), taxRate);
        subtotalValue.setText(Money.rupiah(totals.subtotal()));
        discountValue.setText(totals.discount() > 0
                ? "−" + Money.rupiah(totals.discount())
                : Money.rupiah(0));
        discountValue.setForeground(totals.discount() > 0 ? Theme.DANGER : Theme.MUTED);
        taxLabel.setText("PPN (" + formatPercent(taxRate) + "%)");
        taxValue.setText(Money.rupiah(totals.tax()));
        totalValue.setText(Money.rupiah(totals.total()));
        total = totals.total();

        int count = model.getRowCount();
        payButton.setText(count == 0 ? "BAYAR" : "BAYAR   " + Money.rupiah(totals.total()));
        payButton.setEnabled(count > 0);
        if (onItemCount != null) {
            onItemCount.accept(count);
        }
    }

    private static String formatPercent(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.001) {
            return String.valueOf((int) Math.rint(value));
        }
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    public List<CartItem> getItems() {
        return model.getItems();
    }

    public double getDiscountPercent() {
        return ((Number) discountSpinner.getValue()).doubleValue();
    }

    public long getTotal() {
        return total;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
        recalculate();
    }

    public void setInvoice(String invoice) {
        invoiceValue.setText(invoice);
    }

    public void setOnCheckout(Runnable onCheckout) {
        this.onCheckout = onCheckout;
    }

    public void setOnItemCount(Consumer<Integer> onItemCount) {
        this.onItemCount = onItemCount;
    }
}
