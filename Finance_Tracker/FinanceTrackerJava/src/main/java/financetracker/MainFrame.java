package financetracker;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/** Main application window and refresh coordinator for all tabs. */
public class MainFrame extends JFrame {
    private final Database database;
    private final ReportService reports = new ReportService();
    private final DashboardPanel dashboard;
    private final TransactionFormPanel form;
    private final ManagePanel manage;
    private final SummaryPanel summary;
    private final ReportsPanel charts;
    private final ExportPanel export;
    private final JPanel content = new JPanel(new CardLayout());
    private final Map<String, JButton> navigation = new LinkedHashMap<>();
    private final JLabel status = new JLabel(" ");

    /** Creates the database and all application tabs. */
    public MainFrame() throws SQLException {
        super("FinTrack - Smart Personal Finance");
        database = new Database();
        Theme.install();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1200, 760));
        setLocationRelativeTo(null);
        dashboard = new DashboardPanel(this);
        form = new TransactionFormPanel(this);
        manage = new ManagePanel(this);
        summary = new SummaryPanel(this);
        charts = new ReportsPanel(this);
        export = new ExportPanel(this);
        content.add(dashboard, "Dashboard"); content.add(form, "Add Transaction");
        content.add(manage, "Manage Transactions"); content.add(summary, "Monthly Summary");
        content.add(charts, "Visual Reports"); content.add(export, "Export");
        add(createSidebar(), BorderLayout.WEST); add(content, BorderLayout.CENTER);
        refreshAll();
        showPage("Dashboard");
    }

    /** Returns the shared database. */
    public Database database() { return database; }
    /** Returns the shared reporting service. */
    public ReportService reportService() { return reports; }

    /** Refreshes every view after a data change. */
    public void refreshAll() {
        try {
            List<Transaction> transactions = database.all();
            dashboard.refresh(transactions);
            manage.refresh();
            summary.refresh();
            charts.refresh(transactions);
            export.refresh();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** Displays a consistent database error dialog. */
    public void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows a form validation error. */
    public void showValidation(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check your input", JOptionPane.WARNING_MESSAGE);
    }

    /** Shows a page selected from the sidebar. */
    public void showPage(String page) {
        ((CardLayout) content.getLayout()).show(content, page);
        for (Map.Entry<String, JButton> entry : navigation.entrySet()) {
            boolean active = entry.getKey().equals(page);
            entry.getValue().setBackground(active ? Theme.ACCENT : new Color(0, 0, 0, 0));
            entry.getValue().setForeground(active ? Color.WHITE : Theme.TEXT);
            entry.getValue().setIcon(new VectorIcon(iconFor(entry.getKey()), active ? Color.WHITE : Theme.MUTED, 19));
        }
    }

    /** Shows a non-blocking success message in the content shell. */
    public void showSuccess(String message) { status.setText(message); status.setForeground(Theme.INCOME); }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), Color.decode("#EEF2FF")));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setMinimumSize(new Dimension(260, 0));
        sidebar.setMaximumSize(new Dimension(260, Integer.MAX_VALUE));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
        JPanel top = new JPanel(); top.setOpaque(false); top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0)); brand.setOpaque(false);
        JLabel badge = new JLabel(new VectorIcon("balance", Color.WHITE, 26)); badge.setOpaque(true); badge.setBackground(Theme.ACCENT);
        badge.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("FinTrack"); logo.setFont(new Font("Segoe UI", Font.BOLD, 23)); logo.setForeground(Theme.TEXT);
        JLabel tagline = new JLabel("Smart Personal Finance"); tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12)); tagline.setForeground(Theme.MUTED);
        brandText.add(logo);
        brandText.add(tagline);
        brand.add(badge); brand.add(brandText);
        top.add(Box.createVerticalStrut(24)); top.add(brand); top.add(Box.createVerticalStrut(28));
        JLabel menu = new JLabel("MENU"); menu.setFont(new Font("Segoe UI", Font.BOLD, 11)); menu.setForeground(Theme.MUTED); menu.setBorder(BorderFactory.createEmptyBorder(0, 14, 8, 0)); top.add(menu);
        String[] pages = {"Dashboard", "Add Transaction", "Manage Transactions", "Monthly Summary", "Visual Reports", "Export"};
        for (int i = 0; i < pages.length; i++) {
            JButton item = new JButton(pages[i], new VectorIcon(iconFor(pages[i]), Theme.MUTED, 19)); item.setHorizontalAlignment(SwingConstants.LEFT);
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.setIconTextGap(12); item.setFont(new Font("Segoe UI", Font.PLAIN, 14)); item.setFocusPainted(false); item.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 12));
            item.setPreferredSize(new Dimension(244, 48));
            item.setMinimumSize(new Dimension(244, 48));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            item.setOpaque(true); item.setBackground(new Color(0, 0, 0, 0)); item.setForeground(Theme.TEXT);
            final String page = pages[i]; item.addActionListener(e -> showPage(page)); navigation.put(page, item); top.add(item);
        }
        sidebar.add(top, BorderLayout.NORTH);
        JPanel bottom = new JPanel(); bottom.setOpaque(false); bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JPanel user = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4)); user.setOpaque(false);
        JLabel avatar = new JLabel("A", SwingConstants.CENTER); avatar.setForeground(Color.WHITE); avatar.setFont(Theme.BOLD); avatar.setOpaque(true); avatar.setBackground(Theme.PRIMARY_DARK); avatar.setPreferredSize(new Dimension(34, 34));
        JLabel userText = new JLabel("<html><b>Aryashree</b><br><font color='#6B7280' size='2'>Personal Account</font></html>"); user.add(avatar); user.add(userText);
        JButton seed = new JButton("Load Sample Data", new VectorIcon("database", Theme.ACCENT, 16)); Theme.secondary(seed); seed.setAlignmentX(Component.CENTER_ALIGNMENT);
        seed.addActionListener(e -> { try { SeedData.load(database); refreshAll(); showSuccess("Sample data loaded successfully."); } catch (Exception ex) { showError(ex); } });
        JLabel version = new JLabel("v1.0"); version.setForeground(Theme.MUTED); version.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(user); bottom.add(Box.createVerticalStrut(10)); bottom.add(seed); bottom.add(Box.createVerticalStrut(12)); bottom.add(version); bottom.add(Box.createVerticalStrut(18)); sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private String iconFor(String page) {
        return switch (page) {
            case "Dashboard" -> "dashboard";
            case "Add Transaction" -> "add";
            case "Manage Transactions" -> "list";
            case "Monthly Summary" -> "calendar";
            case "Visual Reports" -> "chart";
            default -> "export";
        };
    }
}
