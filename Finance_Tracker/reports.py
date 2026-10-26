"""Pandas-based summaries used by the dashboard and report pages."""

from __future__ import annotations

import pandas as pd


def format_inr(amount: int) -> str:
    """Format a whole-number amount using Indian rupee grouping."""
    value = int(amount)
    sign = "-" if value < 0 else ""
    digits = str(abs(value))
    if len(digits) > 3:
        last_three = digits[-3:]
        leading = digits[:-3]
        groups: list[str] = []
        while leading:
            groups.insert(0, leading[-2:])
            leading = leading[:-2]
        digits = ",".join(groups + [last_three])
    return f"{sign}₹{digits}"


def transactions_frame(records: list[dict]) -> pd.DataFrame:
    """Convert database records to a consistently typed DataFrame."""
    columns = ["id", "date", "type", "category", "amount", "description"]
    frame = pd.DataFrame(records, columns=columns)
    if not frame.empty:
        frame["date"] = pd.to_datetime(frame["date"])
        frame["amount"] = frame["amount"].astype(int)
    return frame


def totals(frame: pd.DataFrame) -> dict[str, int]:
    """Calculate income, expense, and net balance."""
    if frame.empty:
        return {"income": 0, "expense": 0, "balance": 0}
    income = frame.loc[frame["type"] == "Income", "amount"].sum()
    expense = frame.loc[frame["type"] == "Expense", "amount"].sum()
    return {"income": int(income), "expense": int(expense), "balance": int(income - expense)}


def category_spending(frame: pd.DataFrame) -> pd.DataFrame:
    """Return expense totals grouped by category."""
    if frame.empty:
        return pd.DataFrame(columns=["category", "amount"])
    result = (
        frame[frame["type"] == "Expense"]
        .groupby("category", as_index=False)["amount"]
        .sum()
        .sort_values("amount", ascending=False)
    )
    result["amount"] = result["amount"].astype(int)
    return result


def monthly_summary(frame: pd.DataFrame) -> pd.DataFrame:
    """Return income, expense, and balance totals for each calendar month."""
    columns = ["month", "income", "expense", "balance"]
    if frame.empty:
        return pd.DataFrame(columns=columns)
    working = frame.copy()
    working["month"] = working["date"].dt.to_period("M").astype(str)
    income = working[working["type"] == "Income"].groupby("month")["amount"].sum()
    expense = working[working["type"] == "Expense"].groupby("month")["amount"].sum()
    result = pd.DataFrame({"income": income, "expense": expense}).fillna(0).reset_index()
    result["balance"] = result["income"] - result["expense"]
    for column in ["income", "expense", "balance"]:
        result[column] = result[column].astype(int)
    return result[columns].sort_values("month")


def month_category_spending(frame: pd.DataFrame, year: int, month: int) -> pd.DataFrame:
    """Return category spending for one selected month."""
    if frame.empty:
        return pd.DataFrame(columns=["category", "amount"])
    selected = frame[
        (frame["date"].dt.year == year)
        & (frame["date"].dt.month == month)
        & (frame["type"] == "Expense")
    ]
    return category_spending(selected)
