package financetracker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/** Modern transaction entry page while preserving existing validation behavior. */
public class TransactionFormPanel extends JPanel {
    private final MainFrame frame;
    private final JTextField date = new JTextField(LocalDate.now().toString());
    private final JComboBox<String> type = new JComboBox<>(new String[]{"Income", "Expense"});
    private final JComboBox<String> category = new JComboBox<>(new String[]{"Food","Travel","Rent","Bills","Shopping","Health","Entertainment","Education","Salary","Other"});
    private final JTextField amount = new JTextField();
    private final JTextField description = new JTextField();
    private final JLabel feedback = new JLabel(" ");

    /** Creates the add transaction page. */
    public TransactionFormPanel(MainFrame frame) {
        this.frame = frame; setLayout(new GridBagLayout()); setBackground(Theme.CONTENT);
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 22)); card.setPreferredSize(new Dimension(700, 500));
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Add Transaction"); title.setFont(Theme.TITLE); title.setForeground(Theme.TEXT);
        JLabel subtitle = new JLabel("Record your income or expenses with ease."); subtitle.setFont(Theme.BODY); subtitle.setForeground(Theme.MUTED);
        heading.add(title); heading.add(Box.createVerticalStrut(5)); heading.add(subtitle); card.add(heading, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(3, 2, 18, 14)); form.setOpaque(false);
        form.add(field("Date (yyyy-MM-dd)", date)); form.add(field("Amount (₹)", amount)); form.add(field("Type", type)); form.add(field("Category", category)); form.add(field("Description", description)); form.add(new JLabel());
        card.add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT)); actions.setOpaque(false); JButton clear = new JButton("Clear"), save = new JButton("Save Transaction"); Theme.secondary(clear); Theme.primary(save);
        actions.add(feedback); actions.add(clear); actions.add(save); card.add(actions, BorderLayout.SOUTH);
        clear.addActionListener(e -> clear()); save.addActionListener(e -> save()); add(card);
    }

    private JPanel field(String label, JComponent input) { JPanel panel = new JPanel(new BorderLayout(0, 5)); panel.setOpaque(false); JLabel caption = new JLabel(label); caption.setForeground(Theme.MUTED); caption.setFont(Theme.BOLD); panel.add(caption, BorderLayout.NORTH); panel.add(input); return panel; }
    private void save() {
        try {
            LocalDate parsedDate = Validator.parseDate(date.getText()); Validator.validateTransaction(parsedDate, (String) type.getSelectedItem(), (String) category.getSelectedItem());
            frame.database().add(new Transaction(parsedDate, (String) type.getSelectedItem(), (String) category.getSelectedItem(), Validator.parseAmount(amount.getText()), description.getText().trim()));
            clear(); frame.refreshAll(); feedback.setText("Saved successfully."); feedback.setForeground(Theme.INCOME); frame.showSuccess("Transaction saved successfully.");
        } catch (IllegalArgumentException ex) { frame.showValidation(ex.getMessage()); } catch (Exception ex) { frame.showError(ex); }
    }
    private void clear() { date.setText(LocalDate.now().toString()); type.setSelectedIndex(0); category.setSelectedIndex(0); amount.setText(""); description.setText(""); }
}
