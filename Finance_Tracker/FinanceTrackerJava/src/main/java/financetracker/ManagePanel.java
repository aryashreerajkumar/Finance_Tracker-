package financetracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/** Tab for browsing, filtering, editing, and deleting transactions. */
public class ManagePanel extends JPanel {
    private final MainFrame frame;
    private final JTextField from = new JTextField(10), to = new JTextField(10);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"All","Food","Travel","Rent","Bills","Shopping","Health","Entertainment","Education","Salary","Other"});
    private final JComboBox<String> type = new JComboBox<>(new String[]{"All","Income","Expense"});
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Date","Type","Category","Amount","Description"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model);

    /** Creates the management panel. */
    public ManagePanel(MainFrame frame) {
        this.frame = frame; setLayout(new BorderLayout(16, 16)); setBackground(Theme.CONTENT); setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32)); JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filters.setBackground(Theme.SURFACE); filters.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JLabel title = new JLabel("Manage Transactions"); title.setFont(Theme.TITLE); title.setForeground(Theme.TEXT); filters.add(title);
        filters.add(new JLabel("From")); filters.add(from); filters.add(new JLabel("To")); filters.add(to); filters.add(new JLabel("Category")); filters.add(category); filters.add(new JLabel("Type")); filters.add(type);
        JButton apply = new JButton("Apply Filters"), edit = new JButton("Edit"), delete = new JButton("Delete"); Theme.primary(apply); Theme.secondary(edit); delete.setBackground(Theme.EXPENSE); delete.setForeground(Color.WHITE); Theme.secondary(delete); filters.add(apply); filters.add(edit); filters.add(delete);
        apply.addActionListener(e -> refresh()); edit.addActionListener(e -> editSelected()); delete.addActionListener(e -> deleteSelected());
        Theme.table(table); table.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());
        add(filters, BorderLayout.NORTH); JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createEmptyBorder()); add(scroll, BorderLayout.CENTER);
    }

    /** Refreshes the table using the selected filters. */
    public void refresh() {
        try {
            LocalDate start = from.getText().isBlank() ? null : Validator.parseDate(from.getText());
            LocalDate end = to.getText().isBlank() ? null : Validator.parseDate(to.getText());
            if (start != null && end != null && start.isAfter(end)) throw new IllegalArgumentException("From date must not be after To date.");
            model.setRowCount(0); for (Transaction t : frame.database().query(start, end, (String) category.getSelectedItem(), (String) type.getSelectedItem()))
                model.addRow(new Object[]{t.getId(), t.getDate(), t.getType(), t.getCategory(), CurrencyUtil.formatINR(t.getAmount()), t.getDescription()});
        } catch (IllegalArgumentException ex) { frame.showValidation(ex.getMessage()); } catch (Exception ex) { frame.showError(ex); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow(); if (row < 0) { frame.showValidation("Select a transaction first."); return; }
        if (JOptionPane.showConfirmDialog(frame, "Delete the selected transaction?", "Confirm delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            try { frame.database().delete((Long) model.getValueAt(row, 0)); frame.refreshAll(); } catch (Exception ex) { frame.showError(ex); }
    }

    private void editSelected() {
        int row = table.getSelectedRow(); if (row < 0) { frame.showValidation("Select a transaction first."); return; }
        try {
            long id = (Long) model.getValueAt(row, 0); JTextField date = new JTextField(model.getValueAt(row, 1).toString());
            JComboBox<String> typeBox = new JComboBox<>(new String[]{"Income","Expense"}); typeBox.setSelectedItem(model.getValueAt(row, 2));
            JTextField cat = new JTextField(model.getValueAt(row, 3).toString()), amount = new JTextField(model.getValueAt(row, 4).toString().replace("₹","").replace(",",""));
            JTextField desc = new JTextField(model.getValueAt(row, 5).toString()); JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
            p.add(new JLabel("Date")); p.add(date); p.add(new JLabel("Type")); p.add(typeBox); p.add(new JLabel("Category")); p.add(cat); p.add(new JLabel("Amount")); p.add(amount); p.add(new JLabel("Description")); p.add(desc);
            if (JOptionPane.showConfirmDialog(frame, p, "Edit transaction", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                LocalDate parsed = Validator.parseDate(date.getText()); Validator.validateTransaction(parsed, (String) typeBox.getSelectedItem(), cat.getText());
                frame.database().update(new Transaction(id, parsed, (String) typeBox.getSelectedItem(), cat.getText().trim(), Validator.parseAmount(amount.getText()), desc.getText().trim())); frame.refreshAll();
            }
        } catch (IllegalArgumentException ex) { frame.showValidation(ex.getMessage()); } catch (Exception ex) { frame.showError(ex); }
    }

    /** Colors the formatted amount according to the transaction type. */
    private static class AmountRenderer extends DefaultTableCellRenderer {
        /** Renders one amount cell with income or expense color. */
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            component.setForeground(selected ? Color.WHITE : "Income".equals(table.getValueAt(row, 2)) ? Theme.INCOME : Theme.EXPENSE);
            return component;
        }
    }
}
