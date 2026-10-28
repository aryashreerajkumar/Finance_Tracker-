package financetracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Tab showing a selected month's totals, category breakdown, and monthly history. */
public class SummaryPanel extends JPanel {
    private final MainFrame frame;
    private final JComboBox<String> month = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    private final JSpinner year = new JSpinner(new SpinnerNumberModel(java.time.Year.now().getValue(), 2000, 2100, 1));
    private final JLabel totals = new JLabel();
    private final DefaultTableModel categories = new DefaultTableModel(new String[]{"Category","Amount"}, 0);
    private final DefaultTableModel monthly = new DefaultTableModel(new String[]{"Month","Income","Expense","Balance"}, 0);

    /** Creates the summary panel. */
    public SummaryPanel(MainFrame frame) {
        this.frame = frame; setLayout(new BorderLayout(16, 16)); setBackground(Theme.CONTENT); setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32)); JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); top.setOpaque(false);
        JLabel title = new JLabel("Monthly Summary"); title.setFont(Theme.TITLE); title.setForeground(Theme.TEXT); top.add(title); top.add(new JLabel("Month")); top.add(month); top.add(new JLabel("Year")); top.add(year);
        JButton refresh = new JButton("Show Summary"); Theme.primary(refresh); top.add(refresh); refresh.addActionListener(e -> refresh()); add(top, BorderLayout.NORTH);
        JTable categoryTable = new JTable(categories), monthlyTable = new JTable(monthly); Theme.table(categoryTable); Theme.table(monthlyTable);
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 16)); center.setOpaque(false); RoundedPanel categoryCard = new RoundedPanel(new BorderLayout()); categoryCard.add(new JScrollPane(categoryTable)); RoundedPanel monthlyCard = new RoundedPanel(new BorderLayout()); monthlyCard.add(new JScrollPane(monthlyTable)); center.add(categoryCard); center.add(monthlyCard); add(center, BorderLayout.CENTER); totals.setFont(Theme.BOLD); add(totals, BorderLayout.SOUTH);
        month.setSelectedItem(String.format("%02d", java.time.MonthDay.now().getMonthValue()));
    }

    /** Refreshes the selected month and all month-by-month rows. */
    public void refresh() {
        try {
            List<Transaction> all = frame.database().all(); YearMonth selected = YearMonth.of((Integer) year.getValue(), Integer.parseInt((String) month.getSelectedItem()));
            List<Transaction> selectedRows = all.stream().filter(t -> YearMonth.from(t.getDate()).equals(selected)).toList();
            long income = frame.reportService().totalIncome(selectedRows), expense = frame.reportService().totalExpense(selectedRows);
            totals.setText("  " + selected + " | Income: " + CurrencyUtil.formatINR(income) + "   Expense: " + CurrencyUtil.formatINR(expense) + "   Balance: " + CurrencyUtil.formatINR(income - expense));
            categories.setRowCount(0); frame.reportService().categoryTotals(selectedRows).forEach((key, value) -> categories.addRow(new Object[]{key, CurrencyUtil.formatINR(value)}));
            monthly.setRowCount(0); frame.reportService().monthlySummary(all).forEach((key, values) -> monthly.addRow(new Object[]{key, CurrencyUtil.formatINR(values[0]), CurrencyUtil.formatINR(values[1]), CurrencyUtil.formatINR(values[0] - values[1])}));
        } catch (Exception ex) { frame.showError(ex); }
    }
}
