package financetracker;

import org.jfree.chart.ChartPanel;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/** Tab containing pie, bar, and line visual reports. */
public class ReportsPanel extends JPanel {
    private final MainFrame frame;
    private final JPanel charts = new JPanel(new GridLayout(0, 2, 20, 20));
    private final JTextField from = new JTextField(10), to = new JTextField(10);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"All","Food","Travel","Rent","Bills","Shopping","Health","Entertainment","Education","Salary","Other"});
    private final JComboBox<String> type = new JComboBox<>(new String[]{"All","Income","Expense"});

    /** Creates the reports panel. */
    public ReportsPanel(MainFrame frame) {
        this.frame = frame; setLayout(new BorderLayout(16, 16)); setBackground(Theme.CONTENT); setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)); filters.setBackground(Theme.SURFACE); filters.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12)); JLabel title = new JLabel("Visual Reports"); title.setFont(Theme.TITLE); title.setForeground(Theme.TEXT); filters.add(title); filters.add(new JLabel("From")); filters.add(from); filters.add(new JLabel("To")); filters.add(to);
        filters.add(new JLabel("Category")); filters.add(category); filters.add(new JLabel("Type")); filters.add(type);
        JButton apply = new JButton("Apply Filters"); Theme.primary(apply); filters.add(apply); apply.addActionListener(e -> refreshFromFilters());
        JScrollPane chartScroll = new JScrollPane(charts);
        chartScroll.setBorder(null);
        chartScroll.setBackground(Theme.CONTENT);
        chartScroll.getViewport().setBackground(Theme.CONTENT);
        chartScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        chartScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chartScroll.getVerticalScrollBar().setUnitIncrement(24);
        add(filters, BorderLayout.NORTH); add(chartScroll, BorderLayout.CENTER);
    }

    /** Rebuilds all charts from the supplied filtered dataset. */
    public void refresh(List<Transaction> transactions) {
        ChartService service = new ChartService(); charts.removeAll();
        charts.setOpaque(false);
        charts.add(chartCard("Spending", service.createPieChart(frame.reportService().categoryTotals(transactions))));
        charts.add(chartCard("Monthly", service.createBarChart(frame.reportService().monthlySummary(transactions))));
        charts.add(chartCard("Balance Trend", service.createLineChart(frame.reportService().monthlySummary(transactions))));
    }

    private JPanel chartCard(String title, org.jfree.chart.JFreeChart chart) {
        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(520, 360));
        card.setMinimumSize(new Dimension(360, 360));
        JLabel heading = new JLabel(title); heading.setFont(Theme.BOLD); card.add(heading, BorderLayout.NORTH);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(520, 320));
        chartPanel.setMinimumSize(new Dimension(300, 260));
        chartPanel.setMaximumDrawWidth(4000);
        chartPanel.setMaximumDrawHeight(4000);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.addMouseWheelListener(event -> {
            JScrollPane pane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, chartPanel);
            if (pane != null) {
                pane.dispatchEvent(SwingUtilities.convertMouseEvent(chartPanel, event, pane));
                event.consume();
            }
        });
        chartPanel.setBorder(BorderFactory.createEmptyBorder());
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    /** Rebuilds charts from this tab's selected filters. */
    private void refreshFromFilters() {
        try {
            LocalDate start = from.getText().isBlank() ? null : Validator.parseDate(from.getText());
            LocalDate end = to.getText().isBlank() ? null : Validator.parseDate(to.getText());
            if (start != null && end != null && start.isAfter(end)) throw new IllegalArgumentException("From date must not be after To date.");
            refresh(frame.database().query(start, end, (String) category.getSelectedItem(), (String) type.getSelectedItem()));
        } catch (IllegalArgumentException ex) { frame.showValidation(ex.getMessage()); } catch (Exception ex) { frame.showError(ex); }
    }
}
