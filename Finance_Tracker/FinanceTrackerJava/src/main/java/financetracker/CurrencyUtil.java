package financetracker;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formats whole Indian Rupee amounts consistently. */
public final class CurrencyUtil {
    private CurrencyUtil() { }

    /** Formats a signed amount using Indian digit grouping. */
    public static String formatINR(long amount) {
        boolean negative = amount < 0;
        String digits = Long.toString(Math.abs(amount));
        if (digits.length() <= 3) return (negative ? "-₹" : "₹") + digits;
        String last = digits.substring(digits.length() - 3);
        String prefix = digits.substring(0, digits.length() - 3);
        StringBuilder grouped = new StringBuilder();
        while (prefix.length() > 2) {
            grouped.insert(0, "," + prefix.substring(prefix.length() - 2));
            prefix = prefix.substring(0, prefix.length() - 2);
        }
        grouped.insert(0, prefix);
        return (negative ? "-₹" : "₹") + grouped + "," + last;
    }

    /** Returns an amount formatted without a currency symbol for chart values. */
    public static String formatNumber(long amount) {
        DecimalFormat format = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
        return format.format(amount);
    }
}
