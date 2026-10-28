package financetracker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/** Central visual language for the FinTrack light theme. */
public final class Theme {
    public static final Color CONTENT = Color.decode("#F4F6FB");
    public static final Color SURFACE = Color.WHITE;
    public static final Color SIDEBAR = Color.WHITE;
    public static final Color ACCENT = Color.decode("#4F46E5");
    public static final Color PRIMARY_DARK = Color.decode("#3730A3");
    public static final Color TEAL = Color.decode("#14B8A6");
    public static final Color INCOME = Color.decode("#16A34A");
    public static final Color EXPENSE = Color.decode("#DC2626");
    public static final Color WARNING = Color.decode("#F59E0B");
    public static final Color TEXT = Color.decode("#111827");
    public static final Color MUTED = Color.decode("#6B7280");
    public static final Color BORDER = Color.decode("#E5E7EB");
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font VALUE = new Font("Segoe UI", Font.BOLD, 24);

    private Theme() { }

    /** Applies global FlatLaf component properties and table defaults. */
    public static void install() {
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 14);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Component.focusColor", ACCENT);
        UIManager.put("TextField.margin", new Insets(9, 12, 9, 12));
        UIManager.put("ComboBox.padding", new Insets(6, 10, 6, 10));
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("Table.rowHeight", 40);
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.alternateRowColor", Color.decode("#FAFBFF"));
        UIManager.put("TableHeader.background", Color.decode("#F3F4F6"));
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TableHeader.font", BOLD);
    }

    /** Styles a primary action button. */
    public static void primary(JButton button) {
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFont(BOLD);
        button.setBorder(new EmptyBorder(11, 20, 11, 20));
        button.setFocusPainted(false);
    }

    /** Styles a secondary action button. */
    public static void secondary(JButton button) {
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFont(BOLD);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setFocusPainted(false);
    }

    /** Applies the common table presentation. */
    public static void table(JTable table) {
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(BODY);
        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD);
        header.setPreferredSize(new Dimension(0, 38));
    }

    public static JPanel page(String title, String subtitle) {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(32, 32, 32, 32));
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE);
        titleLabel.setForeground(TEXT);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(BODY);
        subtitleLabel.setForeground(MUTED);
        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitleLabel);
        page.add(heading, BorderLayout.NORTH);
        return page;
    }

    public static JPanel iconBadge(String name, Color color) {
        JPanel badge = new JPanel(new GridBagLayout());
        badge.setOpaque(true);
        badge.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 24));
        badge.setPreferredSize(new Dimension(44, 44));
        badge.add(new JLabel(new VectorIcon(name, color, 20)));
        return badge;
    }
}
