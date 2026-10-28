package financetracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Validates user-entered transactions and filter values. */
public final class Validator {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private Validator() { }

    /** Parses a valid, non-future ISO date or throws a user-friendly error. */
    public static LocalDate parseDate(String value) {
        try {
            LocalDate date = LocalDate.parse(value.trim(), DATE_FORMAT);
            if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Date cannot be in the future.");
            return date;
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("Please enter a valid date (yyyy-MM-dd).");
        }
    }

    /** Parses a positive whole-number amount after removing separators. */
    public static long parseAmount(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Please enter the amount.");
        String normalized = value.replace(",", "").replace(" ", "").trim();
        if (!normalized.matches("\\d+")) throw new IllegalArgumentException("Amount must be a whole number greater than 0.");
        try {
            long amount = Long.parseLong(normalized);
            if (amount <= 0 || amount > 100_000_000L) throw new NumberFormatException();
            return amount;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount must be a whole number greater than 0.");
        }
    }

    /** Validates the remaining transaction fields. */
    public static void validateTransaction(LocalDate date, String type, String category) {
        if (date == null || date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Date cannot be in the future.");
        if (!"Income".equals(type) && !"Expense".equals(type)) throw new IllegalArgumentException("Type must be Income or Expense.");
        if (category == null || category.trim().isEmpty()) throw new IllegalArgumentException("Category must not be empty.");
    }
}
