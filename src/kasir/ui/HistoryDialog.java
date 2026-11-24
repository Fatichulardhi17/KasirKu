package kasir.ui;

import kasir.data.Settings;
import kasir.data.TransactionRepository;
import kasir.model.Transaction;
import kasir.util.Money;
import kasir.util.ReceiptText;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Riwayat transaksi beserta detail struk. */
public class HistoryDialog extends JDialog {

    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.of("id", "ID"));

    private final Settings settings;
    private final List<Transaction> all;
    private final List<Transaction> visible = new ArrayList<>();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Invoice", "Waktu", "Kasir", "Metode", "Total"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable table = new JTable(tableModel);
    private final JTextArea detail = new JTextArea();
    private final HintTextField searchField = new HintTextField("Cari invoice, kasir, atau metode...");
    private final JLabel summary = new JLabel();
    private final JLabel detailTitle = new JLabel("Detail Transaksi");

    public HistoryDialog(Window owner, TransactionRepository repository, Settings settings) {
        super(owner, "Riwayat Transaksi", ModalityType.APPLICATION_MODAL);
        this.settings = settings;
        this.all = repository.findAll();
        this.all.sort(Comparator.comparing(Transaction::getTime).reversed());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG);

        RoundedPanel root = new RoundedPanel(new BorderLayout(0, 12), Theme.SURFACE, 18);
        root.setBorder(new EmptyBorder(20, 20, 18, 20));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
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
        setSize(1020, 660);
        setLocationRelativeTo(owner);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Riwayat Transaksi");
        title.setFont(Theme.font(Font.BOLD, 18));
        title.setForeground(Theme.TEXT);
        summary.setFont(Theme.font(Font.PLAIN, 11));
        summary.setForeground(Theme.MUTED);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(summary, BorderLayout.EAST);

        searchField.setPreferredSize(new Dimension(220, 38));

        header.add(titleRow);
        header.add(Box.createVerticalStrut(10));
        header.add(searchField);
        return header;
    }

    private JPanel buildCenter() {
        Theme.styleTable(table);
        table.setRowHeight(36);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(right);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetail();
            }
        });

        JScrollPane tableScroll = Theme.scroll(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        detail.setEditable(false);
        detail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        detail.setBackground(new Color(0xFAFAF9));
        detail.setForeground(Theme.TEXT);
        detail.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane detailScroll = Theme.scroll(detail);
        detailScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));
        detailScroll.getViewport().setBackground(new Color(0xFAFAF9));

        detailTitle.setFont(Theme.font(Font.BOLD, 12));
        detailTitle.setForeground(Theme.MUTED);
        detailTitle.setBorder(new EmptyBorder(8, 2, 8, 2));

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setOpaque(false);
        detailPanel.add(detailTitle, BorderLayout.NORTH);
        detailPanel.add(detailScroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailPanel);
        split.setResizeWeight(0.55);
        split.setDividerSize(10);
        split.setBorder(null);
        split.setOpaque(true);
        split.setBackground(Theme.SURFACE);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(split, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel hint = new JLabel("Pilih transaksi untuk melihat struk lengkap.");
        hint.setFont(Theme.font(Font.PLAIN, 11));
        hint.setForeground(Theme.MUTED);
        FlatButton close = FlatButton.outline("Tutup", Theme.MUTED, Theme.BORDER);
        close.setBorder(new EmptyBorder(8, 20, 8, 20));
        close.addActionListener(e -> dispose());
        footer.add(hint, BorderLayout.WEST);
        footer.add(close, BorderLayout.EAST);
        return footer;
    }

    private void load() {
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        tableModel.setRowCount(0);
        visible.clear();
        long revenue = 0;
        for (Transaction transaction : all) {
            if (!query.isEmpty()
                    && !transaction.getInvoice().toLowerCase(Locale.ROOT).contains(query)
                    && !transaction.getCashier().toLowerCase(Locale.ROOT).contains(query)
                    && !transaction.getMethod().toLowerCase(Locale.ROOT).contains(query)
                    && !TIME.format(transaction.getTime()).contains(query)) {
                continue;
            }
            visible.add(transaction);
            revenue += transaction.getTotal();
            tableModel.addRow(new Object[]{
                    transaction.getInvoice(),
                    TIME.format(transaction.getTime()),
                    transaction.getCashier(),
                    transaction.getMethod(),
                    Money.rupiah(transaction.getTotal())
            });
        }
        summary.setText(visible.size() + " transaksi  ·  Total " + Money.rupiah(revenue));
        if (!visible.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
            showDetail();
        } else {
            detailTitle.setText("Detail Transaksi");
            detail.setText("Belum ada transaksi.");
        }
    }

    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visible.size()) {
            return;
        }
        Transaction transaction = visible.get(row);
        detailTitle.setText("Struk " + transaction.getInvoice());
        detail.setText(ReceiptText.build(transaction, settings));
        detail.setCaretPosition(0);
    }
}
