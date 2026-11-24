package kasir.ui;

import kasir.data.DataException;
import kasir.data.ProductRepository;
import kasir.data.Settings;
import kasir.data.TransactionRepository;
import kasir.model.CartItem;
import kasir.model.Product;
import kasir.model.Transaction;
import kasir.model.TransactionItem;
import kasir.service.Calculator;
import kasir.util.Money;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Jendela utama aplikasi kasir. */
public class MainFrame extends JFrame {

    private static final DateTimeFormatter CLOCK =
            DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy  ·  HH:mm:ss", Locale.of("id", "ID"));
    private static final String HINT = "F2 Cari produk   ·   F9 Bayar   ·   F4 Kosongkan keranjang";

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final Settings settings;

    private final ProductGridPanel productGrid;
    private final CartPanel cartPanel;

    private final JLabel storeLabel = new JLabel();
    private final JLabel addressLabel = new JLabel();
    private final JLabel clockLabel = new JLabel();
    private final JLabel cashierValue = new JLabel();
    private final JLabel statusLabel = new JLabel(HINT);
    private final JLabel itemCountLabel = new JLabel("0 item");

    private Timer statusTimer;

    public MainFrame(ProductRepository productRepository,
                     TransactionRepository transactionRepository,
                     Settings settings) {
        super("KasirKu");
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.settings = settings;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1360, (int) Math.round(screen.width * 0.95));
        int height = Math.min(860, (int) Math.round(screen.height * 0.88));
        setSize(width, height);
        setMinimumSize(new Dimension(Math.min(1040, width), Math.min(640, height)));
        setLocationRelativeTo(null);
        setIconImage(buildIcon());

        productGrid = new ProductGridPanel(productRepository, this::addToCart);
        cartPanel = new CartPanel(settings.getTaxRate(), this::showStatus);
        cartPanel.setOnCheckout(this::checkout);
        cartPanel.setOnItemCount(count -> itemCountLabel.setText(count + " item"));

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        installShortcuts();
        applySettings();
        cartPanel.setInvoice(transactionRepository.nextInvoice());

        new Timer(1000, e -> updateClock()).start();
        updateClock();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Theme.HEADER);
        header.setBorder(new EmptyBorder(14, 22, 14, 22));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        storeLabel.setFont(Theme.font(Font.BOLD, 20));
        storeLabel.setForeground(Color.WHITE);
        addressLabel.setFont(Theme.font(Font.PLAIN, 11));
        addressLabel.setForeground(new Color(0x94A3B8));
        brand.add(storeLabel);
        brand.add(Box.createVerticalStrut(2));
        brand.add(addressLabel);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        clockLabel.setFont(Theme.font(Font.PLAIN, 11));
        clockLabel.setForeground(new Color(0x94A3B8));

        RoundedPanel cashierChip = new RoundedPanel(new FlowLayout(FlowLayout.CENTER, 8, 0),
                Theme.HEADER_SOFT, 16);
        cashierChip.setBorder(new EmptyBorder(7, 12, 7, 12));
        cashierValue.setFont(Theme.font(Font.BOLD, 11));
        cashierValue.setForeground(new Color(0xE2E8F0));
        cashierChip.add(cashierValue);

        FlatButton productsButton = headerButton("Produk");
        productsButton.addActionListener(e -> openProductManager());
        FlatButton historyButton = headerButton("Riwayat");
        historyButton.addActionListener(e -> openHistory());
        FlatButton settingsButton = headerButton("Pengaturan");
        settingsButton.addActionListener(e -> openSettings());

        right.add(clockLabel);
        right.add(Box.createHorizontalStrut(16));
        right.add(cashierChip);
        right.add(Box.createHorizontalStrut(14));
        right.add(productsButton);
        right.add(Box.createHorizontalStrut(8));
        right.add(historyButton);
        right.add(Box.createHorizontalStrut(8));
        right.add(settingsButton);

        header.add(brand, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private FlatButton headerButton(String text) {
        FlatButton button = new FlatButton(text, Theme.HEADER_SOFT, Color.WHITE, 10);
        button.setFont(Theme.font(Font.BOLD, 11));
        button.setBorder(new EmptyBorder(7, 14, 7, 14));
        return button;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBackground(Theme.BG);
        content.setBorder(new EmptyBorder(18, 18, 12, 18));
        content.add(productGrid, BorderLayout.CENTER);
        content.add(cartPanel, BorderLayout.EAST);
        return content;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG);
        bar.setBorder(new EmptyBorder(0, 22, 10, 22));

        statusLabel.setFont(Theme.font(Font.PLAIN, 11));
        statusLabel.setForeground(Theme.MUTED);

        itemCountLabel.setFont(Theme.font(Font.BOLD, 11));
        itemCountLabel.setForeground(Theme.MUTED);
        itemCountLabel.setHorizontalAlignment(JLabel.RIGHT);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(itemCountLabel, BorderLayout.EAST);
        return bar;
    }

    private void installShortcuts() {
        JComponent root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "cari");
        root.getActionMap().put("cari", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productGrid.focusSearch();
            }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "bayar");
        root.getActionMap().put("bayar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cartPanel.requestCheckout();
            }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "kosongkan");
        root.getActionMap().put("kosongkan", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cartPanel.confirmClear();
            }
        });
    }

    private void addToCart(Product product) {
        cartPanel.addProduct(product);
        cartPanel.requestFocusInWindow();
    }

    private void checkout() {
        List<CartItem> cartItems = cartPanel.getItems();
        if (cartItems.isEmpty()) {
            showStatus("Keranjang masih kosong.");
            return;
        }

        Calculator.Totals totals = Calculator.calculate(
                cartItems, cartPanel.getDiscountPercent(), settings.getTaxRate());

        PaymentDialog.Result result = PaymentDialog.show(this, totals);
        if (result == null) {
            return;
        }

        String invoice = transactionRepository.nextInvoice();
        List<TransactionItem> items = cartItems.stream().map(TransactionItem::of).toList();
        Transaction transaction = new Transaction(
                invoice,
                LocalDateTime.now(),
                settings.getCashier(),
                totals.subtotal(),
                totals.discount(),
                totals.tax(),
                totals.total(),
                result.method(),
                result.paid(),
                result.paid() - totals.total(),
                items);

        try {
            // Validasi stok dulu sebelum simpan transaksi
            // Ini mencegah transaksi tersimpan tapi stok gagal dikurangi
            for (CartItem item : cartItems) {
                Product product = productRepository.findById(item.getProduct().getId());
                if (product == null) {
                    throw new DataException("Produk " + item.getProduct().getName() + " tidak ditemukan");
                }
                if (product.getStock() < item.getQty()) {
                    throw new DataException("Stok tidak cukup untuk " + product.getName() + 
                        "\nTersedia: " + product.getStock() + ", diminta: " + item.getQty());
                }
            }
            
            // Simpan transaksi dulu, lalu kurangi stok
            transactionRepository.append(transaction);
            productRepository.applySale(cartItems);
        } catch (DataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Gagal menyimpan transaksi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cartPanel.clear();
        cartPanel.setInvoice(transactionRepository.nextInvoice());
        productGrid.refresh();
        showStatus("Transaksi " + invoice + " berhasil  ·  " + Money.rupiah(transaction.getTotal()));

        new ReceiptDialog(this, transaction, settings, true).setVisible(true);
    }

    private void openProductManager() {
        new ProductManagerDialog(this, productRepository).setVisible(true);
        productGrid.refresh();
    }

    private void openHistory() {
        new HistoryDialog(this, transactionRepository, settings).setVisible(true);
    }

    private void openSettings() {
        new SettingsDialog(this, settings, this::applySettings).setVisible(true);
    }

    private void applySettings() {
        setTitle("KasirKu  ·  " + settings.getStoreName());
        storeLabel.setText(settings.getStoreName());
        String extra = settings.getPhone().isBlank() ? "" : "   ·   " + settings.getPhone();
        addressLabel.setText(settings.getAddress() + extra);
        cashierValue.setText("Kasir  ·  " + settings.getCashier());
        cartPanel.setTaxRate(settings.getTaxRate());
    }

    private void updateClock() {
        clockLabel.setText(CLOCK.format(LocalDateTime.now()));
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Theme.PRIMARY_DARK);
        if (statusTimer != null) {
            statusTimer.stop();
        }
        statusTimer = new Timer(4000, e -> {
            statusLabel.setText(HINT);
            statusLabel.setForeground(Theme.MUTED);
        });
        statusTimer.setRepeats(false);
        statusTimer.start();
    }

    private BufferedImage buildIcon() {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = image.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Theme.PRIMARY);
        g.fillRoundRect(0, 0, 64, 64, 16, 16);
        g.setColor(Color.WHITE);
        g.setFont(Theme.font(Font.BOLD, 36));
        java.awt.FontMetrics metrics = g.getFontMetrics();
        String text = "K";
        g.drawString(text, (64 - metrics.stringWidth(text)) / 2,
                32 + (metrics.getAscent() - metrics.getDescent()) / 2);
        g.dispose();
        return image;
    }
}
