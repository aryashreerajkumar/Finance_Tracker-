package financetracker;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/** Tab for exporting filtered transactions or the current monthly report. */
public class ExportPanel extends JPanel {
    private final MainFrame frame;
    private final JComboBox<String> type = new JComboBox<>(new String[]{"All","Income","Expense"});
    private final JComboBox<String> category = new JComboBox<>(new String[]{"All","Food","Travel","Rent","Bills","Shopping","Health","Entertainment","Education","Salary","Other"});
    private final JTextField from = new JTextField(10), to = new JTextField(10);

    /** Creates the export controls. */
    public ExportPanel(MainFrame frame) {
        this.frame = frame; setLayout(new GridBagLayout()); setBackground(Theme.CONTENT); setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        RoundedPanel card = new RoundedPanel(new GridBagLayout()); GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(8, 8, 8, 8); c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Export"); title.setFont(Theme.TITLE); title.setForeground(Theme.TEXT);
        JLabel subtitle = new JLabel("Download your filtered data for backup or analysis."); subtitle.setFont(Theme.BODY); subtitle.setForeground(Theme.MUTED);
        heading.add(title); heading.add(Box.createVerticalStrut(5)); heading.add(subtitle); c.gridx = 0; c.gridy = 0; c.gridwidth = 2; card.add(heading, c);
        c.gridy++; c.gridwidth = 1; card.add(new JLabel("From"), c); c.gridx = 1; card.add(from, c); c.gridx = 0; c.gridy++; card.add(new JLabel("To"), c); c.gridx = 1; card.add(to, c); c.gridx = 0; c.gridy++; card.add(new JLabel("Category"), c); c.gridx = 1; card.add(category, c); c.gridx = 0; c.gridy++; card.add(new JLabel("Type"), c); c.gridx = 1; card.add(type, c);
        JButton transactions = new JButton("Export Transactions (CSV)"), summary = new JButton("Export Monthly Summary (CSV)"); Theme.primary(transactions); Theme.secondary(summary); c.gridx = 0; c.gridy++; card.add(transactions, c); c.gridx = 1; card.add(summary, c); add(card);
        transactions.addActionListener(e -> exportTransactions()); summary.addActionListener(e -> exportSummary());
    }

    /** Refreshes export controls after data changes. */
    public void refresh() { }

    private List<Transaction> filtered() throws Exception {
        return frame.database().query(from.getText().isBlank() ? null : Validator.parseDate(from.getText()), to.getText().isBlank() ? null : Validator.parseDate(to.getText()), (String) category.getSelectedItem(), (String) type.getSelectedItem());
    }

    private void exportTransactions() {
        try { File file = choose("transactions.csv"); if (file != null) CsvExporter.exportTransactions(file, filtered()); }
        catch (Exception ex) { frame.showError(ex); }
    }

    private void exportSummary() {
        try {
            File file = choose("monthly-summary.csv"); if (file == null) return; List<Transaction> rows = filtered(); var service = frame.reportService();
            java.util.Map<java.time.YearMonth, long[]> months = service.monthlySummary(rows); java.util.List<String[]> monthlyRows = new java.util.ArrayList<>();
            months.forEach((m, values) -> monthlyRows.add(new String[]{m.toString(), Long.toString(values[0]), Long.toString(values[1]), Long.toString(values[0] - values[1])}));
            java.util.List<String[]> categoryRows = service.categoryTotals(rows).entrySet().stream().map(e -> new String[]{e.getKey(), Long.toString(e.getValue())}).toList();
            CsvExporter.exportSummary(file, "Filtered data", service.totalIncome(rows), service.totalExpense(rows), service.balance(rows), categoryRows, monthlyRows);
        } catch (Exception ex) { frame.showError(ex); }
    }

    private File choose(String name) {
        JFileChooser chooser = new JFileChooser(); chooser.setSelectedFile(new File(name));
        return chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }
}
