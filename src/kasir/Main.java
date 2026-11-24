package kasir;

import kasir.data.DataException;
import kasir.data.ProductRepository;
import kasir.data.Settings;
import kasir.data.TransactionRepository;
import kasir.ui.MainFrame;
import kasir.ui.Theme;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import java.nio.file.Files;
import java.nio.file.Path;

/** Titik masuk aplikasi kasir. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // pakai look and feel bawaan
            }
            Theme.install();
            ToolTipManager.sharedInstance().setInitialDelay(250);

            try {
                Path dataDir = Path.of("data");
                Files.createDirectories(dataDir);

                Settings settings = new Settings(dataDir.resolve("pengaturan.properties"));
                ProductRepository products = new ProductRepository(dataDir.resolve("produk.csv"));
                TransactionRepository transactions = new TransactionRepository(dataDir);

                new MainFrame(products, transactions, settings).setVisible(true);
            } catch (DataException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Gagal memuat data", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.toString(),
                        "Terjadi kesalahan", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                System.exit(1);
            }
        });
    }
}
