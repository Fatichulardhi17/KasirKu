package kasir.util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/** Mencetak teks struk ke printer default. */
public final class ReceiptPrinter {

    private ReceiptPrinter() {
    }

    public static void print(Component parent, String text) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Struk Kasir");

        final int[] index = {0};
        final String[] lines = text.split("\n", -1);

        job.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
            if (index[0] >= lines.length) {
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 9));
            java.awt.FontMetrics metrics = g2.getFontMetrics();
            int lineHeight = metrics.getHeight();
            int y = metrics.getAscent();
            int maxY = (int) pageFormat.getImageableHeight();

            while (index[0] < lines.length && y < maxY) {
                g2.drawString(lines[index[0]++], 0, y);
                y += lineHeight;
            }
            return Printable.PAGE_EXISTS;
        });

        if (!job.printDialog()) {
            return;
        }
        try {
            job.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Gagal mencetak struk: " + ex.getMessage(),
                    "Cetak Struk",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
