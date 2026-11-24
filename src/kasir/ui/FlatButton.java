package kasir.ui;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Tombol datar dengan sudut membulat, warna dasar, hover, dan garis tepi opsional. */
public class FlatButton extends JButton {

    private Color base;
    private Color hover;
    private Color press;
    private Color line;
    private final int radius;

    public FlatButton(String text, Color base, Color foreground, int radius) {
        super(text);
        this.radius = radius;
        setBase(base);
        setForeground(foreground);
        setFont(Theme.BODY_BOLD);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(9, 16, 9, 16));
    }

    public static FlatButton outline(String text, Color foreground, Color line) {
        FlatButton button = new FlatButton(text, Theme.SURFACE, foreground, 10);
        button.setLine(line);
        return button;
    }

    public void setBase(Color base) {
        this.base = base;
        boolean dark = brightness(base) < 140;
        this.hover = blend(base, dark ? Color.WHITE : Color.BLACK, dark ? 0.10 : 0.05);
        this.press = blend(base, dark ? Color.WHITE : Color.BLACK, dark ? 0.20 : 0.12);
        repaint();
    }

    public void setLine(Color line) {
        this.line = line;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill;
        if (!isEnabled()) {
            fill = new Color(0xE2E8F0);
        } else if (getModel().isPressed()) {
            fill = press;
        } else if (getModel().isRollover()) {
            fill = hover;
        } else {
            fill = base;
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        if (line != null) {
            g2.setColor(isEnabled() ? line : Theme.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private static double brightness(Color color) {
        return 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
    }

    private static Color blend(Color color, Color target, double amount) {
        int r = (int) Math.round(color.getRed() + (target.getRed() - color.getRed()) * amount);
        int g = (int) Math.round(color.getGreen() + (target.getGreen() - color.getGreen()) * amount);
        int b = (int) Math.round(color.getBlue() + (target.getBlue() - color.getBlue()) * amount);
        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
