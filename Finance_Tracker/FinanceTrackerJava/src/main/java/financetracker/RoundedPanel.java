package financetracker;

import javax.swing.*;
import java.awt.*;

/** Reusable white card with a soft border and rounded corners. */
public class RoundedPanel extends JPanel {
    private final int radius;

    /** Creates a card with the requested layout and padding. */
    public RoundedPanel(LayoutManager layout) {
        super(layout);
        radius = 20;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    }

    /** Paints the card background and border. */
    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(17, 24, 39, 18));
        g.fillRoundRect(3, 5, getWidth() - 4, getHeight() - 3, radius, radius);
        g.setColor(Theme.SURFACE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g.setColor(Theme.BORDER);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g.dispose();
        super.paintComponent(graphics);
    }
}
