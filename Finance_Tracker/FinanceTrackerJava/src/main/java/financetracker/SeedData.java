package financetracker;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Loads a repeatable set of approximately thirty sample transactions. */
public final class SeedData {
    private SeedData() { }

    /** Adds sample data to the database. */
    public static void load(Database database) throws SQLException {
        LocalDate today = LocalDate.now();
        String[][] rows = {
                {"Income","Salary","75000","Monthly salary"}, {"Expense","Rent","22000","Apartment rent"},
                {"Expense","Food","1800","Groceries"}, {"Expense","Travel","1200","Metro and taxi"},
                {"Expense","Bills","2500","Electricity and internet"}, {"Expense","Shopping","3200","Clothes"},
                {"Expense","Health","900","Pharmacy"}, {"Expense","Entertainment","700","Movie"},
                {"Expense","Education","1500","Online course"}, {"Income","Other","5000","Freelance work"},
                {"Expense","Food","2100","Groceries"}, {"Expense","Travel","850","Bus pass"},
                {"Expense","Bills","1800","Phone bill"}, {"Expense","Shopping","1400","Household items"},
                {"Expense","Health","600","Doctor visit"}, {"Income","Salary","75000","Monthly salary"},
                {"Expense","Rent","22000","Apartment rent"}, {"Expense","Food","2300","Groceries"},
                {"Expense","Travel","1100","Fuel"}, {"Expense","Bills","2700","Utilities"},
                {"Expense","Entertainment","1200","Concert"}, {"Expense","Education","900","Books"},
                {"Expense","Shopping","2600","Shoes"}, {"Expense","Health","450","Medicine"},
                {"Income","Other","3500","Consulting"}, {"Income","Salary","75000","Monthly salary"},
                {"Expense","Rent","22000","Apartment rent"}, {"Expense","Food","1900","Groceries"},
                {"Expense","Travel","1300","Train tickets"}, {"Expense","Bills","2300","Utilities"}
        };
        for (int i = 0; i < rows.length; i++) {
            LocalDate month = today.minusMonths(2 - (i / 10));
            LocalDate date = month.withDayOfMonth(Math.min(5 + (i % 20), month.lengthOfMonth()));
            database.add(new Transaction(date, rows[i][0], rows[i][1], Long.parseLong(rows[i][2]), rows[i][3]));
        }
    }

    /** Runs the seed loader from the command line. */
    public static void main(String[] args) throws SQLException {
        SeedData.load(new Database());
        System.out.println("Sample transactions loaded.");
    }
}
