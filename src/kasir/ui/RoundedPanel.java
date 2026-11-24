package kasir.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/** Panel dengan latar membulat dan garis tepi opsional. */
public class RoundedPanel extends JPanel {

    private Color fill;
    private Color line;
    private final int radius;

    public RoundedPanel(LayoutManager layout, Color fill, int radius) {
        super(layout);
        this.fill = fill;
        this.radius = radius;
        setOpaque(false);
    }

    public void setFill(Color fill) {
        this.fill = fill;
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
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        if (line != null) {
            g2.setColor(line);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
