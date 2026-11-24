package kasir.ui;

import kasir.service.Calculator;
import kasir.util.Money;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** Dialog pembayaran: metode, uang diterima, dan kembalian. */
public class PaymentDialog extends JDialog {

    public record Result(String method, long paid) {
    }

    private static final String[] METHODS = {"Tunai", "QRIS", "Kartu Debit", "E-Wallet"};

    private final Calculator.Totals totals;
    private final Map<String, FlatButton> methodButtons = new LinkedHashMap<>();

    private final JTextField cashField = new JTextField();
    private final JLabel paidValue = new JLabel(Money.rupiah(0));
    private final JLabel changeValue = new JLabel(Money.rupiah(0));
    private final JLabel message = new JLabel(" ");
    private final FlatButton confirm =
            new FlatButton("Selesaikan Pembayaran", Theme.SUCCESS, Color.WHITE, 12);

    private String method = "Tunai";
    private Result result;

    private PaymentDialog(Window owner, Calculator.Totals totals) {
        super(owner, "Pembayaran", ModalityType.APPLICATION_MODAL);
        this.totals = totals;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Theme.BG);

        RoundedPanel root = new RoundedPanel(new BorderLayout(), Theme.SURFACE, 18);
        root.setBorder(new EmptyBorder(22, 24, 20, 24));
        setContentPane(root);

        root.add(buildContent(), BorderLayout.CENTER);

        setSize(480, 575);
        setLocationRelativeTo(owner);

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> confirm.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        selectMethod("Tunai");
    }

    public static Result show(Window owner, Calculator.Totals totals) {
        PaymentDialog dialog = new PaymentDialog(owner, totals);
        dialog.setVisible(true);
        return dialog.result;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel caption = new JLabel("Total Tagihan");
        caption.setFont(Theme.font(Font.BOLD, 12));
        caption.setForeground(Theme.MUTED);
        caption.setAlignmentX(LEFT_ALIGNMENT);

        JLabel total = new JLabel(Money.rupiah(totals.total()));
        total.setFont(Theme.font(Font.BOLD, 32));
        total.setForeground(Theme.TEXT);
        total.setAlignmentX(LEFT_ALIGNMENT);

        JLabel breakdown = new JLabel("Subtotal " + Money.plain(totals.subtotal())
                + "  ·  Diskon " + Money.plain(totals.discount())
                + "  ·  PPN " + Money.plain(totals.tax()));
        breakdown.setFont(Theme.font(Font.PLAIN, 11));
        breakdown.setForeground(Theme.MUTED);
        breakdown.setAlignmentX(LEFT_ALIGNMENT);

        content.add(caption);
        content.add(Box.createVerticalStrut(2));
        content.add(total);
        content.add(Box.createVerticalStrut(2));
        content.add(breakdown);
        content.add(Box.createVerticalStrut(16));
        content.add(divider());
        content.add(Box.createVerticalStrut(14));

        JLabel methodCaption = new JLabel("Metode Pembayaran");
        methodCaption.setFont(Theme.font(Font.BOLD, 12));
        methodCaption.setForeground(Theme.MUTED);
        methodCaption.setAlignmentX(LEFT_ALIGNMENT);
        content.add(methodCaption);
        content.add(Box.createVerticalStrut(8));
        content.add(buildMethods());
        content.add(Box.createVerticalStrut(16));

        JPanel paidRow = new JPanel(new BorderLayout());
        paidRow.setOpaque(false);
        paidRow.setAlignmentX(LEFT_ALIGNMENT);
        paidRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel paidCaption = new JLabel("Uang Diterima");
        paidCaption.setFont(Theme.font(Font.BOLD, 12));
        paidCaption.setForeground(Theme.MUTED);
        paidValue.setFont(Theme.font(Font.BOLD, 12));
        paidValue.setForeground(Theme.TEXT);
        paidValue.setHorizontalAlignment(SwingConstants.RIGHT);
        paidRow.add(paidCaption, BorderLayout.WEST);
        paidRow.add(paidValue, BorderLayout.EAST);

        content.add(paidRow);
        content.add(Box.createVerticalStrut(6));

        cashField.setFont(Theme.font(Font.BOLD, 22));
        Theme.styleField(cashField);
        cashField.setHorizontalAlignment(SwingConstants.RIGHT);
        cashField.setPreferredSize(new Dimension(200, 50));
        cashField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cashField.setAlignmentX(LEFT_ALIGNMENT);
        cashField.setBorder(new Theme.RoundBorder(Theme.BORDER, 12, 14, 12));
        installDigitFilter();
        cashField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateState();
            }
        });
        content.add(cashField);
        content.add(Box.createVerticalStrut(8));
        content.add(buildQuickAmounts());
        content.add(Box.createVerticalStrut(14));
        content.add(divider());
        content.add(Box.createVerticalStrut(12));

        JPanel changeRow = new JPanel(new BorderLayout());
        changeRow.setOpaque(false);
        changeRow.setAlignmentX(LEFT_ALIGNMENT);
        changeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel changeCaption = new JLabel("Kembalian");
        changeCaption.setFont(Theme.font(Font.BOLD, 13));
        changeCaption.setForeground(Theme.MUTED);
        changeValue.setFont(Theme.font(Font.BOLD, 26));
        changeValue.setForeground(Theme.MUTED);
        changeValue.setHorizontalAlignment(SwingConstants.RIGHT);
        changeRow.add(changeCaption, BorderLayout.WEST);
        changeRow.add(changeValue, BorderLayout.EAST);

        content.add(changeRow);
        content.add(Box.createVerticalStrut(4));

        message.setFont(Theme.font(Font.PLAIN, 11));
        message.setForeground(Theme.MUTED);
        message.setAlignmentX(LEFT_ALIGNMENT);
        content.add(message);
        content.add(Box.createVerticalStrut(14));

        confirm.setFont(Theme.font(Font.BOLD, 15));
        confirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        confirm.setPreferredSize(new Dimension(200, 52));
        confirm.setAlignmentX(LEFT_ALIGNMENT);
        confirm.setEnabled(false);
        confirm.addActionListener(e -> finish());
        content.add(confirm);
        content.add(Box.createVerticalStrut(8));

        FlatButton cancel = FlatButton.outline("Batalkan", Theme.MUTED, Theme.BORDER);
        cancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cancel.setAlignmentX(LEFT_ALIGNMENT);
        cancel.addActionListener(e -> dispose());
        content.add(cancel);

        content.add(Box.createVerticalGlue());
        return content;
    }

    private JPanel buildMethods() {
        JPanel panel = new JPanel(new GridLayout(1, METHODS.length, 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.setPreferredSize(new Dimension(200, 36));

        for (String name : METHODS) {
            FlatButton button = new FlatButton(name, Theme.SURFACE, Theme.MUTED, 10);
            button.setFont(Theme.font(Font.BOLD, 11));
            button.setBorder(new EmptyBorder(6, 4, 6, 4));
            button.setLine(Theme.BORDER);
            button.addActionListener(e -> selectMethod(name));
            methodButtons.put(name, button);
            panel.add(button);
        }
        return panel;
    }

    private JPanel buildQuickAmounts() {
        List<Long> suggestions = suggestions(totals.total());
        JPanel panel = new JPanel(new GridLayout(1, suggestions.size(), 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        panel.setPreferredSize(new Dimension(200, 32));

        for (Long amount : suggestions) {
            String text = amount == totals.total() ? "Uang Pas" : Money.rupiah(amount);
            FlatButton button = FlatButton.outline(text, Theme.MUTED, Theme.BORDER);
            button.setFont(Theme.font(Font.BOLD, 11));
            button.setBorder(new EmptyBorder(5, 4, 5, 4));
            button.addActionListener(e -> cashField.setText(String.valueOf(amount)));
            panel.add(button);
        }
        return panel;
    }

    private static List<Long> suggestions(long total) {
        LinkedHashSet<Long> values = new LinkedHashSet<>();
        values.add(total);
        for (long step : new long[]{5000, 10000, 20000, 50000, 100000}) {
            values.add(((total + step - 1) / step) * step);
        }
        return values.stream().filter(value -> value >= total).limit(4).toList();
    }

    private JPanel divider() {
        JPanel line = new JPanel();
        line.setBackground(Theme.BORDER);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(10, 1));
        return line;
    }

    private void installDigitFilter() {
        ((AbstractDocument) cashField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attrs);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String digits = text == null ? "" : text.replaceAll("\\D", "");
                int newLength = fb.getDocument().getLength() - length + digits.length();
                if (newLength <= 12) {
                    super.replace(fb, offset, length, digits, attrs);
                }
            }
        });
    }

    private void selectMethod(String selected) {
        this.method = selected;
        for (Map.Entry<String, FlatButton> entry : methodButtons.entrySet()) {
            boolean active = entry.getKey().equals(selected);
            FlatButton button = entry.getValue();
            button.setBase(active ? Theme.PRIMARY : Theme.SURFACE);
            button.setForeground(active ? Color.WHITE : Theme.MUTED);
            button.setLine(active ? Theme.PRIMARY : Theme.BORDER);
        }

        boolean cash = "Tunai".equalsIgnoreCase(selected);
        cashField.setEnabled(cash);
        if (cash) {
            cashField.setText("");
            cashField.requestFocusInWindow();
        } else {
            cashField.setText(String.valueOf(totals.total()));
        }
        updateState();
    }

    private long paid() {
        if (!"Tunai".equalsIgnoreCase(method)) {
            return totals.total();
        }
        return Money.parse(cashField.getText());
    }

    private void updateState() {
        long paid = paid();
        long change = paid - totals.total();
        boolean enough = paid >= totals.total();

        paidValue.setText(Money.rupiah(paid));

        if (!"Tunai".equalsIgnoreCase(method)) {
            changeValue.setText(Money.rupiah(0));
            changeValue.setForeground(Theme.MUTED);
            message.setForeground(Theme.MUTED);
            message.setText("Pembayaran non-tunai sebesar " + Money.rupiah(totals.total()) + ".");
            confirm.setEnabled(true);
            return;
        }

        if (enough) {
            changeValue.setText(Money.rupiah(change));
            changeValue.setForeground(change > 0 ? Theme.SUCCESS : Theme.TEXT);
            message.setForeground(Theme.MUTED);
            message.setText(change > 0
                    ? "Kembalikan " + Money.rupiah(change) + " kepada pelanggan."
                    : "Uang pas. Terima kasih.");
            confirm.setEnabled(true);
        } else {
            changeValue.setText(Money.rupiah(0));
            changeValue.setForeground(Theme.MUTED);
            message.setForeground(Theme.DANGER);
            message.setText(paid == 0
                    ? "Masukkan jumlah uang yang diterima."
                    : "Uang kurang " + Money.rupiah(totals.total() - paid) + ".");
            confirm.setEnabled(false);
        }
    }

    private void finish() {
        long paid = paid();
        if (paid < totals.total()) {
            return;
        }
        result = new Result(method, paid);
        dispose();
    }
}
