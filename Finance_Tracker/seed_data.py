"""Insert a small demo dataset. Run with: python seed_data.py"""

from datetime import date, timedelta
import random

from db import add_transaction, init_db


def seed() -> None:
    """Insert approximately 30 varied transactions across three months."""
    init_db()
    random.seed(13)
    today = date.today()
    expense_ranges = {
        "Food": (150, 600),
        "Travel": (50, 1500),
        "Bills": (300, 2000),
        "Shopping": (500, 3000),
        "Health": (300, 2000),
        "Entertainment": (200, 1000),
        "Rent": (8000, 8000),
    }
    descriptions = ["Demo purchase", "Monthly payment", "Weekend activity", "Sample entry"]
    for index in range(30):
        transaction_date = today - timedelta(days=random.randint(0, 89))
        if index % 6 == 0:
            add_transaction(
                transaction_date.isoformat(), "Income", "Salary",
                random.choice([25000, 30000, 35000]), "Demo salary",
            )
        else:
            category = random.choice(list(expense_ranges))
            add_transaction(
                transaction_date.isoformat(), "Expense", category,
                random.randint(*expense_ranges[category]), random.choice(descriptions),
            )
    print("Inserted 30 demo transactions into finance.db.")


if __name__ == "__main__":
    seed()
