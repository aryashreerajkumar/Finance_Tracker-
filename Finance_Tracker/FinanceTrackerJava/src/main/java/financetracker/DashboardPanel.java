package financetracker;

import org.jfree.chart.ChartPanel;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/** Showpiece dashboard with a fintech-style overview of the account. */
public class DashboardPanel extends JPanel {
    private final MainFrame frame;
    private final JPanel cards = new JPanel(new GridLayout(1, 4, 20, 0));
    private final JPanel chartGrid = new FixedHeightPanel(new GridLayout(1, 2, 20, 0), 400);
    private final JPanel categories = new JPanel();
    private final JPanel recent = new JPanel();
    private final JLabel balance = new JLabel();
    private final JScrollPane scroll;

    public DashboardPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT);
        JPanel page = Theme.page("Dashboard", "A clear view of your money, spending and savings.");
        ScrollablePage body = new ScrollablePage();
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        JPanel hero = hero();
        body.add(fixedRow(hero, 120));
        body.add(Box.createVerticalStrut(20));
        cards.setOpaque(false);
        body.add(fixedRow(cards, 150));
        body.add(Box.createVerticalStrut(20));
        chartGrid.setOpaque(false);
        body.add(chartGrid);
        body.add(Box.createVerticalStrut(20));
        JPanel lower = new JPanel(new GridLayout(1, 2, 20, 0));
        lower.setOpaque(false);
        lower.add(section("Top Spending Categories", categories));
        lower.add(section("Recent Transactions", recent));
        body.add(fixedRow(lower, 340));
        scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setOpaque(true);
        scroll.setBackground(Theme.CONTENT);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(Theme.CONTENT);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        page.add(scroll, BorderLayout.CENTER);
        add(page);
    }

    private JPanel hero() {
        JPanel hero = new JPanel(new BorderLayout(20, 0)) {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Theme.PRIMARY_DARK, getWidth(), getHeight(), Theme.TEAL));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g.setColor(new Color(255, 255, 255, 30));
                g.fillOval(getWidth() - 150, -45, 170, 170);
                g.fillOval(getWidth() - 80, 65, 130, 130);
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        hero.setOpaque(false);
        hero.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel welcome = new JLabel("Welcome back, Aryashree");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(Color.WHITE);
        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM uuuu")));
        date.setFont(Theme.BODY);
        date.setForeground(new Color(255, 255, 255, 190));
        text.add(welcome);
        text.add(Box.createVerticalStrut(8));
        text.add(date);
        hero.add(text, BorderLayout.WEST);
        balance.setFont(new Font("Segoe UI", Font.BOLD, 22));
        balance.setForeground(Color.WHITE);
        hero.add(balance, BorderLayout.EAST);
        hero.setPreferredSize(new Dimension(0, 120));
        return hero;
    }

    public void refresh(List<Transaction> transactions) {
        long income = frame.reportService().totalIncome(transactions);
        long expense = frame.reportService().totalExpense(transactions);
        List<Transaction> month = transactions.stream().filter(t -> YearMonth.from(t.getDate()).equals(YearMonth.now())).toList();
        balance.setText("Available Balance " + CurrencyUtil.formatINR(income - expense));
        cards.removeAll();
        stat("income", "Total Income", CurrencyUtil.formatINR(income), "All time", Theme.INCOME);
        stat("expense", "Total Expense", CurrencyUtil.formatINR(expense), "All time", Theme.EXPENSE);
        stat("income", "This Month Income", CurrencyUtil.formatINR(frame.reportService().totalIncome(month)), "This month", Theme.INCOME);
        stat("expense", "This Month Expense", CurrencyUtil.formatINR(frame.reportService().totalExpense(month)), "This month", Theme.EXPENSE);

        chartGrid.removeAll();
        ChartService service = new ChartService();
        chartGrid.add(chartCard("Spending by Category", service.createPieChart(frame.reportService().categoryTotals(transactions))));
        chartGrid.add(chartCard("Income vs Expense", service.createBarChart(frame.reportService().monthlySummary(transactions))));

        categories.removeAll();
        categories.setLayout(new BoxLayout(categories, BoxLayout.Y_AXIS));
        Map<String, Long> totals = frame.reportService().categoryTotals(transactions);
        long maximum = totals.values().stream().mapToLong(Long::longValue).max().orElse(1);
        int index = 0;
        for (Map.Entry<String, Long> entry : frame.reportService().topThreeCategories(transactions)) {
            JPanel row = new JPanel(new BorderLayout(10, 4));
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(0, 52));
            row.setMinimumSize(new Dimension(0, 52));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            JLabel name = new JLabel(entry.getKey());
            name.setFont(Theme.BOLD);
            JLabel amount = new JLabel(CurrencyUtil.formatINR(entry.getValue()));
            amount.setForeground(Theme.MUTED);
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) (entry.getValue() * 100 / maximum));
            bar.setForeground(new Color[]{Theme.ACCENT, Theme.TEAL, Theme.WARNING}[index++ % 3]);
            bar.setBorderPainted(false);
            JPanel labels = new JPanel(new BorderLayout());
            labels.setOpaque(false);
            labels.add(name, BorderLayout.WEST);
            labels.add(amount, BorderLayout.EAST);
            JPanel wrapper = new JPanel(new BorderLayout(0, 6));
            wrapper.setOpaque(false);
            wrapper.add(labels, BorderLayout.NORTH);
            wrapper.add(bar, BorderLayout.CENTER);
            categories.add(wrapper);
            categories.add(Box.createVerticalStrut(12));
        }
        if (totals.isEmpty()) categories.add(empty("No expense data yet."));

        recent.removeAll();
        recent.setLayout(new BoxLayout(recent, BoxLayout.Y_AXIS));
        transactions.stream().limit(5).forEach(t -> {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(0, 52));
            row.setMinimumSize(new Dimension(0, 52));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            JLabel icon = new JLabel(new VectorIcon("transactions", "Income".equals(t.getType()) ? Theme.INCOME : Theme.EXPENSE, 18));
            JLabel details = new JLabel("<html><b>" + t.getCategory() + "</b><br><font color='#6B7280'>" + t.getDate() + "</font></html>");
            JLabel amount = new JLabel(("Income".equals(t.getType()) ? "+" : "-") + CurrencyUtil.formatINR(t.getAmount()));
            amount.setForeground("Income".equals(t.getType()) ? Theme.INCOME : Theme.EXPENSE);
            amount.setFont(Theme.BOLD);
            row.add(icon, BorderLayout.WEST);
            row.add(details, BorderLayout.CENTER);
            row.add(amount, BorderLayout.EAST);
            recent.add(row);
            recent.add(Box.createVerticalStrut(0));
        });
        if (transactions.isEmpty()) recent.add(empty("No transactions yet."));
        bodyRevalidate();
    }

    private void bodyRevalidate() {
        scroll.getViewport().getView().revalidate();
        scroll.getViewport().getView().repaint();
        scroll.revalidate();
        scroll.repaint();
    }

    private JPanel fixedRow(JComponent component, int height) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(component, BorderLayout.CENTER);
        row.setPreferredSize(new Dimension(0, height));
        row.setMinimumSize(new Dimension(0, height));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return row;
    }

    private RoundedPanel chartCard(String title, org.jfree.chart.JFreeChart chart) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(520, 360));
        card.setMinimumSize(new Dimension(360, 360));
        JLabel heading = new JLabel(title);
        heading.setFont(Theme.BOLD);
        card.add(heading, BorderLayout.NORTH);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(520, 320));
        chartPanel.setMinimumSize(new Dimension(300, 260));
        chartPanel.setMaximumDrawWidth(4000);
        chartPanel.setMaximumDrawHeight(4000);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.addMouseWheelListener(event -> forwardWheel(chartPanel, event));
        chartPanel.setBorder(BorderFactory.createEmptyBorder());
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    private void stat(String icon, String title, String value, String caption, Color color) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(12, 8));
        card.add(Theme.iconBadge(icon, color), BorderLayout.WEST);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(title);
        label.setForeground(Theme.MUTED);
        JLabel amount = new JLabel(value);
        amount.setFont(Theme.VALUE);
        amount.setForeground(Theme.TEXT);
        JLabel note = new JLabel(caption);
        note.setForeground(Theme.MUTED);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        text.add(label);
        text.add(Box.createVerticalStrut(5));
        text.add(amount);
        text.add(Box.createVerticalStrut(4));
        text.add(note);
        card.add(text, BorderLayout.CENTER);
        cards.add(card);
    }

    private RoundedPanel section(String title, Component body) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 14));
        JLabel heading = new JLabel(title);
        heading.setFont(Theme.BOLD);
        card.add(heading, BorderLayout.NORTH);
        if (body instanceof JPanel panel) panel.setOpaque(false);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JLabel empty(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.MUTED);
        return label;
    }

    private static final class FixedHeightPanel extends JPanel {
        private final int height;

        private FixedHeightPanel(LayoutManager layout, int height) {
            super(layout);
            this.height = height;
        }

        @Override public Dimension getMinimumSize() {
            return new Dimension(0, height);
        }

        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, height);
        }

        @Override public Dimension getPreferredSize() {
            Dimension preferred = super.getPreferredSize();
            return new Dimension(preferred.width, height);
        }
    }

    private static void forwardWheel(Component source, java.awt.event.MouseWheelEvent event) {
        JScrollPane pane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, source);
        if (pane == null) return;
        java.awt.event.MouseEvent converted = SwingUtilities.convertMouseEvent(source, event, pane);
        pane.dispatchEvent(converted);
        event.consume();
    }
}
