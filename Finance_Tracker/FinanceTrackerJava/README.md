# Personal Finance Tracker

A beginner-friendly Java 26 Swing application for tracking Indian Rupee income and expenses. Data is stored in `finance.db` using SQLite, and reports use JFreeChart.

## Requirements

- Java 26
- Maven 3.10 or newer

## Run

From this directory:

```text
mvn compile exec:java -Dexec.mainClass="financetracker.Main"
```

Build the runnable shaded JAR:

```text
mvn clean package
java -jar target/finance-tracker-1.0.0.jar
```

On Java 26, the recommended command enables FlatLaf native access explicitly:

```text
java --enable-native-access=ALL-UNNAMED -jar target/finance-tracker-1.0.0.jar
```

Load approximately thirty sample transactions from the command line:

```text
mvn compile exec:java -Dexec.mainClass="financetracker.SeedData"
```

The database file is created automatically beside the project when the application first starts. Amounts are positive whole numbers, stored as `long`, and displayed with Indian grouping and the `₹` symbol.

## Features

- Dashboard cards for income, expense, balance, current-month totals, and top spending categories
- Add, edit, delete, and filter transactions
- Monthly category and month-by-month summaries
- Pie, bar, and balance-trend charts
- CSV export for filtered transactions and summaries
- Friendly validation and empty-database handling

## Eight test cases

1. Save `2026-10-27`, `Income`, `Salary`, `75,000`: transaction saves and displays as `₹75,000`.
2. Save an empty amount: shows `Please enter the amount.`
3. Save amount `0`: shows the whole-number validation error.
4. Save amount `100,000,001`: shows the whole-number validation error.
5. Enter amount `12.50`: rejects decimals.
6. Enter a future date: shows a future-date error.
7. Leave category empty in the edit dialog: shows the category validation error.
8. Select a row, choose Delete, and cancel confirmation: record remains unchanged.
