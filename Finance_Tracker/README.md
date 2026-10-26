# Personal Financial Tracking Application

A beginner-friendly personal finance tracker built with Python 3.13, Streamlit, SQLite, pandas, and Matplotlib. All amounts are whole-number Indian rupees (INR).

## Setup and run

From PowerShell:

```powershell
cd C:\Users\aryas\Downloads\Finance_Tracker
py -3.13 -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
streamlit run app.py
```

The SQLite database (`finance.db`) and its `transactions` table are created automatically on first run. To reset any older database, remove it and reseed approximately 30 realistic Indian transactions across three months:

```powershell
Remove-Item .\finance.db -ErrorAction SilentlyContinue
python seed_data.py
```

Then run the application:

```powershell
streamlit run app.py
```

## Features

- Add, edit, delete, and filter income and expense transactions.
- Type amounts directly into the Amount (₹) field; spaces and commas are accepted as typing aids.
- Validation for dates, future dates, positive whole-number amounts, categories, and types.
- Dashboard cards for overall and current-month totals plus top spending categories.
- Monthly income, expense, balance, and category summaries.
- Filter-aware pie, monthly bar, and balance-trend charts.
- CSV downloads for filtered transactions and monthly summaries.
- Friendly empty-database and no-results states.

## Sample screenshots

Run the app and capture screenshots of these pages:

1. Dashboard with overall totals and top categories.
2. Monthly Summary with category spending.
3. Visual Reports with the three charts.

## Suggested test cases

1. Type `10000` into Amount (₹); it should save as `₹10,000`.
2. Type `10,000` into Amount (₹); it should save as `₹10,000`.
3. Try amount `10.5`; it should show `Amount must be a whole number greater than 0.`
4. Try amount `abc`; it should show `Amount must be a whole number greater than 0.`
5. Try amount `-50`; it should show `Amount must be a whole number greater than 0.`
6. Try amount `0`; it should show `Amount must be a whole number greater than 0.`
7. Try an empty amount; it should show `Please enter the amount.`
8. Edit a transaction and confirm the existing whole-number amount is pre-filled and can be replaced by typing.
