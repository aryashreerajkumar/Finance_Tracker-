package financetracker;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/** Calculates dashboard and report summaries from transactions. */
public class ReportService {
    /** Sums income for a list. */
    public long totalIncome(List<Transaction> transactions) { return totalByType(transactions, "Income"); }
    /** Sums expenses for a list. */
    public long totalExpense(List<Transaction> transactions) { return totalByType(transactions, "Expense"); }
    /** Returns income minus expense. */
    public long balance(List<Transaction> transactions) { return totalIncome(transactions) - totalExpense(transactions); }
    /** Returns totals by category for expenses. */
    public Map<String, Long> categoryTotals(List<Transaction> transactions) {
        return transactions.stream().filter(t -> "Expense".equals(t.getType()))
                .collect(Collectors.groupingBy(Transaction::getCategory, TreeMap::new, Collectors.summingLong(Transaction::getAmount)));
    }
    /** Returns the three largest spending categories. */
    public List<Map.Entry<String, Long>> topThreeCategories(List<Transaction> transactions) {
        return categoryTotals(transactions).entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(3).toList();
    }
    /** Returns totals grouped by calendar month. */
    public Map<YearMonth, long[]> monthlySummary(List<Transaction> transactions) {
        Map<YearMonth, long[]> result = new TreeMap<>();
        for (Transaction t : transactions) {
            long[] totals = result.computeIfAbsent(YearMonth.from(t.getDate()), ignored -> new long[2]);
            totals["Income".equals(t.getType()) ? 0 : 1] += t.getAmount();
        }
        return result;
    }
    /** Returns totals by type. */
    private long totalByType(List<Transaction> transactions, String type) {
        return transactions.stream().filter(t -> type.equals(t.getType())).mapToLong(Transaction::getAmount).sum();
    }
}
