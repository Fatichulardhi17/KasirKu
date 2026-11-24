package kasir.ui;

import javax.swing.JTextField;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

/** JTextField yang menampilkan teks petunjuk saat kosong. */
public class HintTextField extends JTextField {

    private final String hint;

    public HintTextField(String hint) {
        this.hint = hint;
        Theme.styleField(this);
        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (getBorder() instanceof Theme.RoundBorder border) {
                    border.setColor(Theme.PRIMARY);
                    repaint();
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (getBorder() instanceof Theme.RoundBorder border) {
                    border.setColor(Theme.BORDER);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!getText().isEmpty() || hint == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Theme.MUTED);
        g2.setFont(getFont());
        Insets insets = getInsets();
        int y = (getHeight() + getFontMetrics(getFont()).getAscent()) / 2 - 2;
        g2.drawString(hint, insets.left, y);
        g2.dispose();
    }
}
