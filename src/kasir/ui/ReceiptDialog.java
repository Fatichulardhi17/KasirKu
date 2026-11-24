package kasir.ui;

import kasir.data.Settings;
import kasir.model.Transaction;
import kasir.util.ReceiptPrinter;
import kasir.util.ReceiptText;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Menampilkan struk setelah pembayaran dan menyediakan cetak/simpan. */
public class ReceiptDialog extends JDialog {

    private final String text;

    public ReceiptDialog(Window owner, Transaction transaction, Settings settings, boolean success) {
        super(owner, "Struk " + transaction.getInvoice(), ModalityType.APPLICATION_MODAL);
        this.text = ReceiptText.build(transaction, settings);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG);

        RoundedPanel root = new RoundedPanel(new BorderLayout(0, 12), Theme.SURFACE, 18);
        root.setBorder(new EmptyBorder(20, 20, 18, 20));
        setContentPane(root);

        root.add(buildHeader(transaction, success), BorderLayout.NORTH);

        JTextArea area = new JTextArea(this.text);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setBackground(new Color(0xFAFAF9));
        area.setForeground(Theme.TEXT);
        area.setBorder(new EmptyBorder(14, 14, 14, 14));
        area.setCaretPosition(0);
        area.setFocusable(false);

        JScrollPane scroll = Theme.scroll(area);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(new Color(0xFAFAF9));
        root.add(scroll, BorderLayout.CENTER);
        root.add(buildButtons(), BorderLayout.SOUTH);

        setSize(440, 640);
        setLocationRelativeTo(owner);
    }

    private JPanel buildHeader(Transaction transaction, boolean success) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(success ? "Pembayaran Berhasil" : "Struk Transaksi");
        title.setFont(Theme.font(Font.BOLD, 17));
        title.setForeground(success ? Theme.SUCCESS : Theme.TEXT);

        JLabel subtitle = new JLabel(transaction.getInvoice() + "  ·  "
                + kasir.util.Money.rupiah(transaction.getTotal()));
        subtitle.setFont(Theme.font(Font.PLAIN, 11));
        subtitle.setForeground(Theme.MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(2));
        header.add(subtitle);
        return header;
    }

    private JPanel buildButtons() {
        JPanel buttons = new JPanel(new java.awt.GridLayout(1, 3, 8, 0));
        buttons.setOpaque(false);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        FlatButton print = new FlatButton("Cetak", Theme.PRIMARY, Color.WHITE, 10);
        print.setBorder(new EmptyBorder(9, 8, 9, 8));
        print.addActionListener(e -> ReceiptPrinter.print(this, text));

        FlatButton save = FlatButton.outline("Simpan .txt", Theme.TEXT, Theme.BORDER);
        save.setBorder(new EmptyBorder(9, 8, 9, 8));
        save.addActionListener(e -> saveToFile());

        FlatButton close = FlatButton.outline("Selesai", Theme.MUTED, Theme.BORDER);
        close.setBorder(new EmptyBorder(9, 8, 9, 8));
        close.addActionListener(e -> dispose());

        buttons.add(print);
        buttons.add(save);
        buttons.add(close);
        return buttons;
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Struk");
        chooser.setFileFilter(new FileNameExtensionFilter("File teks (*.txt)", "txt"));
        if (getTitle() != null && getTitle().contains(" ")) {
            String invoice = getTitle().substring(getTitle().lastIndexOf(' ') + 1);
            chooser.setSelectedFile(new java.io.File(invoice + ".txt"));
        }
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        if (!path.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".txt")) {
            path = Path.of(path + ".txt");
        }
        try {
            Files.writeString(path, text, StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this,
                    "Struk disimpan di:\n" + path.toAbsolutePath(),
                    "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan struk: " + ex.getMessage(),
                    "Kesalahan",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
