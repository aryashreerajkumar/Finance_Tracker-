package financetracker;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/** Small font-independent vector icon set used throughout the interface. */
public final class VectorIcon implements Icon {
    private final String name;
    private final Color color;
    private final int size;

    public VectorIcon(String name, Color color, int size) {
        this.name = name;
        this.color = color;
        this.size = size;
    }

    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }

    @Override public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(Math.max(1.5f, size / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double s = size / 24d;
        g.translate(x, y);
        g.scale(s, s);
        switch (name) {
            case "dashboard" -> { g.fillRoundRect(3, 3, 7, 7, 2, 2); g.fillRoundRect(14, 3, 7, 7, 2, 2); g.fillRoundRect(3, 14, 7, 7, 2, 2); g.fillRoundRect(14, 14, 7, 7, 2, 2); }
            case "add" -> { g.drawRoundRect(4, 4, 16, 16, 4, 4); g.drawLine(12, 8, 12, 16); g.drawLine(8, 12, 16, 12); }
            case "list" -> { for (int i = 0; i < 3; i++) { g.fillOval(4, 6 + i * 6, 2, 2); g.drawLine(9, 7 + i * 6, 20, 7 + i * 6); } }
            case "calendar" -> { g.drawRoundRect(3, 5, 18, 16, 3, 3); g.drawLine(3, 10, 21, 10); g.drawLine(8, 3, 8, 7); g.drawLine(16, 3, 16, 7); }
            case "chart" -> { g.drawLine(4, 19, 4, 5); g.drawLine(4, 19, 21, 19); g.drawLine(7, 15, 11, 11); g.drawLine(11, 11, 14, 14); g.drawLine(14, 14, 20, 7); }
            case "export" -> { g.drawRect(4, 4, 16, 16); g.drawLine(12, 16, 12, 7); g.drawLine(8, 11, 12, 7); g.drawLine(16, 11, 12, 7); }
            case "income" -> { g.drawLine(12, 19, 12, 5); g.drawLine(7, 10, 12, 5); g.drawLine(17, 10, 12, 5); }
            case "expense" -> { g.drawLine(12, 5, 12, 19); g.drawLine(7, 14, 12, 19); g.drawLine(17, 14, 12, 19); }
            case "balance" -> { g.drawOval(4, 4, 16, 16); g.drawLine(12, 7, 12, 17); g.drawLine(9, 9, 15, 9); g.drawLine(9, 15, 15, 15); }
            case "transactions" -> { g.drawRoundRect(4, 3, 16, 18, 3, 3); g.drawLine(8, 8, 16, 8); g.drawLine(8, 12, 16, 12); g.drawLine(8, 16, 14, 16); }
            case "database" -> { g.drawOval(4, 3, 16, 6); g.drawLine(4, 6, 4, 18); g.drawLine(20, 6, 20, 18); g.drawArc(4, 13, 16, 6, 0, -180); g.drawArc(4, 8, 16, 6, 0, -180); }
            default -> g.drawOval(4, 4, 16, 16);
        }
        g.dispose();
    }
}
