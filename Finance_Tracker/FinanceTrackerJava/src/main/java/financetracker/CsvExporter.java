package financetracker;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Exports transactions and summaries as UTF-8 CSV files. */
public final class CsvExporter {
    private CsvExporter() { }

    /** Writes transaction records to a CSV file. */
    public static void exportTransactions(File file, List<Transaction> transactions) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("Date,Type,Category,Amount (INR),Description\n");
            for (Transaction t : transactions) writer.write(String.join(",", csv(t.getDate().toString()), csv(t.getType()),
                    csv(t.getCategory()), Long.toString(t.getAmount()), csv(t.getDescription())) + "\n");
        }
    }

    /** Writes a monthly summary to a CSV file. */
    public static void exportSummary(File file, String month, long income, long expense, long balance,
                                     List<String[]> categoryRows, List<String[]> monthlyRows) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("Month," + csv(month) + "\nIncome," + income + "\nExpense," + expense + "\nBalance," + balance + "\n\n");
            writer.write("Category,Amount (INR)\n");
            for (String[] row : categoryRows) writer.write(csv(row[0]) + "," + row[1] + "\n");
            writer.write("\nMonth,Income (INR),Expense (INR),Balance (INR)\n");
            for (String[] row : monthlyRows) writer.write(csv(row[0]) + "," + row[1] + "," + row[2] + "," + row[3] + "\n");
        }
    }

    private static String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}
