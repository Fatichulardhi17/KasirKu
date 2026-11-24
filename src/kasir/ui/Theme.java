package kasir.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Set;

/** Warna, font, dan komponen dasar untuk seluruh antarmuka. */
public final class Theme {

    public static final Color BG = new Color(0xF1F5F9);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_SOFT = new Color(0xF8FAFC);
    public static final Color HEADER = new Color(0x0F172A);
    public static final Color HEADER_SOFT = new Color(0x1E293B);
    public static final Color PRIMARY = new Color(0x2563EB);
    public static final Color PRIMARY_DARK = new Color(0x1D4ED8);
    public static final Color SUCCESS = new Color(0x16A34A);
    public static final Color DANGER = new Color(0xDC2626);
    public static final Color WARNING = new Color(0xD97706);
    public static final Color TEXT = new Color(0x0F172A);
    public static final Color MUTED = new Color(0x64748B);
    public static final Color BORDER = new Color(0xE2E8F0);
    public static final Color SELECTED = new Color(0xEFF6FF);

    public static final String FAMILY = pickFamily();
    public static final Font BODY = new Font(FAMILY, Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font(FAMILY, Font.BOLD, 13);
    public static final Font SMALL = new Font(FAMILY, Font.PLAIN, 11);
    public static final Font SMALL_BOLD = new Font(FAMILY, Font.BOLD, 11);
    public static final Font TITLE = new Font(FAMILY, Font.BOLD, 18);
    public static final Font BIG = new Font(FAMILY, Font.BOLD, 26);

    private Theme() {
    }

    private static String pickFamily() {
        try {
            Set<String> available = Set.of(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            for (String candidate : new String[]{"Segoe UI", "Inter", "Roboto", "Calibri", "SansSerif"}) {
                if (available.contains(candidate)) {
                    return candidate;
                }
            }
        } catch (RuntimeException ignored) {
            // fallback
        }
        return "SansSerif";
    }

    public static Font font(int style, int size) {
        return new Font(FAMILY, style, size);
    }

    public static void install() {
        UIManager.put("Label.font", BODY);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Panel.font", BODY);
        UIManager.put("Panel.background", BG);
        UIManager.put("Button.font", BODY_BOLD);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("TextField.font", BODY);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", PRIMARY);
        UIManager.put("TextArea.font", BODY);
        UIManager.put("TextArea.foreground", TEXT);
        UIManager.put("Table.font", BODY);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.background", SURFACE);
        UIManager.put("Table.selectionBackground", SELECTED);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.font", SMALL_BOLD);
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("Spinner.font", BODY);
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BODY_BOLD);
        UIManager.put("ToolTip.font", BODY);
        UIManager.put("ToolTip.background", HEADER);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("FileChooser.font", BODY);
        UIManager.put("CheckBox.font", BODY);
        UIManager.put("RadioButton.font", BODY);
        UIManager.put("FormattedTextField.font", BODY);
        UIManager.put("ScrollPane.background", BG);
    }

    public static void styleField(JTextComponent field) {
        field.setOpaque(false);
        field.setFont(BODY);
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setBorder(new RoundBorder(BORDER, 10, 8));
        field.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setFont(BODY);
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(SELECTED);
        table.setSelectionForeground(TEXT);
        table.setAutoCreateRowSorter(false);
        table.setFocusable(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        t, value, selected, focused, row, column);
                label.setFont(SMALL_BOLD);
                label.setForeground(MUTED);
                label.setBackground(SURFACE_SOFT);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                return label;
            }
        });
    }

    public static JScrollPane scroll(Component view) {
        JScrollPane pane = new JScrollPane(view);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setBackground(SURFACE);
        pane.getViewport().setBackground(SURFACE);
        pane.getVerticalScrollBar().setUnitIncrement(18);
        return pane;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /** Border kotak membulat, warna bisa diubah saat fokus. */
    public static final class RoundBorder extends AbstractBorder {
        private Color color;
        private final int radius;
        private final int padH;
        private final int padV;

        public RoundBorder(Color color, int radius, int padding) {
            this(color, radius, padding, padding);
        }

        public RoundBorder(Color color, int radius, int padH, int padV) {
            this.color = color;
            this.radius = radius;
            this.padH = padH;
            this.padV = padV;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(padV + 2, padH + 2, padV + 2, padH + 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(padV + 2, padH + 2, padV + 2, padH + 2);
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
