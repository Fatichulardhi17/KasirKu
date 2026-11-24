package kasir.ui;

import kasir.data.DataException;
import kasir.data.ProductRepository;
import kasir.model.Product;
import kasir.util.Money;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Dialog untuk menambah, mengubah, dan menghapus produk. */
public class ProductManagerDialog extends JDialog {

    private final ProductRepository repository;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final HintTextField searchField = new HintTextField("Cari nama atau kategori...");
    private final JLabel countLabel = new JLabel();
    private final FlatButton editButton = FlatButton.outline("Ubah", Theme.TEXT, Theme.BORDER);
    private final FlatButton deleteButton = FlatButton.outline("Hapus", Theme.DANGER, Theme.BORDER);
    private final List<Product> visible = new ArrayList<>();

    public ProductManagerDialog(Window owner, ProductRepository repository) {
        super(owner, "Kelola Produk", ModalityType.APPLICATION_MODAL);
        this.repository = repository;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG);

        RoundedPanel root = new RoundedPanel(new BorderLayout(0, 12), Theme.SURFACE, 18);
        root.setBorder(new EmptyBorder(20, 20, 18, 20));
        setContentPane(root);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Produk", "Kategori", "Harga", "Stok"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        Theme.styleTable(table);
        table.setRowHeight(38);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(64);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);

        table.getSelectionModel().addListSelectionListener(e -> updateActionState());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelected();
                }
            }
        });

        root.add(buildHeader(), BorderLayout.NORTH);
        JScrollPane scroll = Theme.scroll(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        root.add(scroll, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                load();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                load();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                load();
            }
        });

        load();
        setSize(920, 620);
        setLocationRelativeTo(owner);
        javax.swing.SwingUtilities.invokeLater(
                () -> table.scrollRectToVisible(table.getCellRect(0, 0, true)));
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Kelola Produk");
        title.setFont(Theme.font(Font.BOLD, 18));
        title.setForeground(Theme.TEXT);
        countLabel.setFont(Theme.font(Font.PLAIN, 11));
        countLabel.setForeground(Theme.MUTED);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(countLabel, BorderLayout.EAST);

        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);
        searchField.setPreferredSize(new Dimension(220, 38));
        FlatButton add = new FlatButton("+ Tambah Produk", Theme.PRIMARY, Color.WHITE, 10);
        add.setBorder(new EmptyBorder(9, 16, 9, 16));
        add.addActionListener(e -> addProduct());
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(add, BorderLayout.EAST);

        header.add(titleRow);
        header.add(Box.createVerticalStrut(10));
        header.add(searchRow);
        return header;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        editButton.setBorder(new EmptyBorder(8, 16, 8, 16));
        deleteButton.setBorder(new EmptyBorder(8, 16, 8, 16));
        editButton.addActionListener(e -> editSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        left.add(editButton);
        left.add(deleteButton);

        FlatButton close = FlatButton.outline("Tutup", Theme.MUTED, Theme.BORDER);
        close.setBorder(new EmptyBorder(8, 20, 8, 20));
        close.addActionListener(e -> dispose());

        footer.add(left, BorderLayout.WEST);
        footer.add(close, BorderLayout.EAST);
        return footer;
    }

    private void load() {
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        tableModel.setRowCount(0);
        visible.clear();
        for (Product product : repository.findAll()) {
            if (!query.isEmpty()
                    && !product.getName().toLowerCase(Locale.ROOT).contains(query)
                    && !product.getCategory().toLowerCase(Locale.ROOT).contains(query)) {
                continue;
            }
            visible.add(product);
            tableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    Money.rupiah(product.getPrice()),
                    product.getStock()
            });
        }
        countLabel.setText(visible.size() + " produk");
        updateActionState();
    }

    private void updateActionState() {
        boolean selected = table.getSelectedRow() >= 0;
        editButton.setEnabled(selected);
        deleteButton.setEnabled(selected);
    }

    private Product selectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visible.size()) {
            return null;
        }
        return visible.get(row);
    }

    private void addProduct() {
        ProductForm form = new ProductForm(this, repository.nextId(), null);
        Product product = form.showForm();
        if (product == null) {
            return;
        }
        try {
            repository.add(product);
            load();
        } catch (DataException ex) {
            error(ex.getMessage());
        }
    }

    private void editSelected() {
        Product selected = selectedProduct();
        if (selected == null) {
            return;
        }
        ProductForm form = new ProductForm(this, selected.getId(), selected);
        Product product = form.showForm();
        if (product == null) {
            return;
        }
        try {
            repository.update(product);
            load();
        } catch (DataException ex) {
            error(ex.getMessage());
        }
    }

    private void deleteSelected() {
        Product selected = selectedProduct();
        if (selected == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Hapus produk \"" + selected.getName() + "\"?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            repository.delete(selected.getId());
            load();
        } catch (DataException ex) {
            error(ex.getMessage());
        }
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    /** Form tambah/ubah produk. */
    private static final class ProductForm extends JDialog {

        private final long id;
        private final JTextField nameField = new JTextField();
        private final JComboBox<String> categoryBox =
                new JComboBox<>(new String[]{"Minuman", "Makanan", "Snack", "Lainnya"});
        private final JTextField priceField = new JTextField();
        private final JTextField stockField = new JTextField();
        private final JLabel errorLabel = new JLabel(" ");
        private Product result;

        private ProductForm(Window owner, long id, Product existing) {
            super(owner, existing == null ? "Tambah Produk" : "Ubah Produk",
                    ModalityType.APPLICATION_MODAL);
            this.id = id;

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            getContentPane().setBackground(Theme.BG);
            RoundedPanel root = new RoundedPanel(new BorderLayout(), Theme.SURFACE, 18);
            root.setBorder(new EmptyBorder(22, 24, 20, 24));
            setContentPane(root);

            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);

            int row = 0;

            JLabel title = new JLabel(existing == null ? "Produk Baru" : "Ubah Produk");
            title.setFont(Theme.font(Font.BOLD, 18));
            title.setForeground(Theme.TEXT);
            form.add(title, rowConstraints(row++, 0, 16));

            nameField.setText(existing == null ? "" : existing.getName());
            Theme.styleField(nameField);
            nameField.setPreferredSize(new Dimension(200, 38));
            form.add(caption("Nama Produk"), rowConstraints(row++, 0, 4));
            form.add(nameField, rowConstraints(row++, 0, 12));

            categoryBox.setEditable(true);
            categoryBox.setFont(Theme.BODY);
            categoryBox.setPreferredSize(new Dimension(200, 36));
            categoryBox.setBackground(Theme.SURFACE);
            categoryBox.setOpaque(true);
            categoryBox.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    new Theme.RoundBorder(Theme.BORDER, 10, 4),
                    new EmptyBorder(0, 6, 0, 4)));
            if (categoryBox.getEditor() instanceof javax.swing.plaf.basic.BasicComboBoxEditor basicEditor
                    && basicEditor.getEditorComponent() instanceof JTextField editorField) {
                editorField.setFont(Theme.BODY);
                editorField.setForeground(Theme.TEXT);
                editorField.setOpaque(false);
                editorField.setBorder(new EmptyBorder(0, 4, 0, 4));
            }
            if (existing != null) {
                categoryBox.setSelectedItem(existing.getCategory());
            }
            form.add(caption("Kategori"), rowConstraints(row++, 0, 4));
            form.add(categoryBox, rowConstraints(row++, 0, 12));

            priceField.setText(existing == null ? "" : String.valueOf(existing.getPrice()));
            stockField.setText(existing == null ? "" : String.valueOf(existing.getStock()));
            Theme.styleField(priceField);
            Theme.styleField(stockField);
            digitsOnly(priceField, 10);
            digitsOnly(stockField, 5);

            JPanel numbers = new JPanel(new GridLayout(1, 2, 12, 0));
            numbers.setOpaque(false);
            numbers.add(column("Harga (Rp)", priceField));
            numbers.add(column("Stok", stockField));
            form.add(numbers, rowConstraints(row++, 0, 10));

            errorLabel.setFont(Theme.font(Font.PLAIN, 11));
            errorLabel.setForeground(Theme.DANGER);
            form.add(errorLabel, rowConstraints(row++, 0, 0));

            GridBagConstraints glue = rowConstraints(row++, 0, 0);
            glue.weighty = 1.0;
            glue.fill = GridBagConstraints.BOTH;
            form.add(Box.createGlue(), glue);

            JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
            buttons.setOpaque(false);
            FlatButton save = new FlatButton("Simpan", Theme.PRIMARY, Color.WHITE, 10);
            save.setBorder(new EmptyBorder(9, 8, 9, 8));
            save.addActionListener(e -> save());
            FlatButton cancel = FlatButton.outline("Batal", Theme.MUTED, Theme.BORDER);
            cancel.setBorder(new EmptyBorder(9, 8, 9, 8));
            cancel.addActionListener(e -> dispose());
            buttons.add(save);
            buttons.add(cancel);
            form.add(buttons, rowConstraints(row, 0, 0));

            root.add(form, BorderLayout.CENTER);
            setSize(440, 470);
            setLocationRelativeTo(owner);
            getRootPane().setDefaultButton(save);
        }

        private static GridBagConstraints rowConstraints(int row, int top, int bottom) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.weightx = 1.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(top, 0, bottom, 0);
            return constraints;
        }

        private JPanel column(String caption, java.awt.Component field) {
            JPanel panel = new JPanel(new BorderLayout(0, 4));
            panel.setOpaque(false);
            panel.add(caption(caption), BorderLayout.NORTH);
            panel.add(field, BorderLayout.CENTER);
            return panel;
        }

        private JLabel caption(String text) {
            JLabel label = new JLabel(text);
            label.setFont(Theme.font(Font.BOLD, 11));
            label.setForeground(Theme.MUTED);
            return label;
        }

        private void save() {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("Nama produk wajib diisi.");
                return;
            }
            String category = String.valueOf(categoryBox.getEditor().getItem()).trim();
            if (category.isEmpty()) {
                category = "Lainnya";
            }
            long price = Money.parse(priceField.getText());
            if (price <= 0) {
                errorLabel.setText("Harga harus lebih dari 0.");
                return;
            }
            int stock = (int) Math.min(Integer.MAX_VALUE, Money.parse(stockField.getText()));
            result = new Product(id, name, category, price, stock);
            dispose();
        }

        private Product showForm() {
            setVisible(true);
            return result;
        }

        private static void digitsOnly(JTextField field, int maxLength) {
            ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                        throws BadLocationException {
                    replace(fb, offset, 0, text, attrs);
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {
                    String digits = text == null ? "" : text.replaceAll("\\D", "");
                    if (fb.getDocument().getLength() - length + digits.length() <= maxLength) {
                        super.replace(fb, offset, length, digits, attrs);
                    }
                }
            });
        }
    }
}
