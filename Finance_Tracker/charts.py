"""Matplotlib chart builders for the Streamlit reports."""

from __future__ import annotations

import matplotlib.pyplot as plt
import pandas as pd
from matplotlib.ticker import FuncFormatter

from reports import category_spending, format_inr, monthly_summary


def spending_pie(frame: pd.DataFrame):
    """Create a pie chart of expenses by category, or return None for no data."""
    data = category_spending(frame)
    if data.empty:
        return None
    figure, axis = plt.subplots()
    axis.pie(data["amount"], labels=data["category"], autopct="%1.0f%%", startangle=90)
    axis.set_title("Spending by Category")
    return figure


def monthly_bar(frame: pd.DataFrame):
    """Create a monthly income versus expense bar chart."""
    data = monthly_summary(frame)
    if data.empty:
        return None
    figure, axis = plt.subplots()
    data.plot(x="month", y=["income", "expense"], kind="bar", ax=axis, color=["#2e8b57", "#d95f02"])
    axis.set_title("Monthly Income vs Expense")
    axis.set_xlabel("Month")
    axis.set_ylabel("Amount (₹)")
    axis.yaxis.set_major_formatter(FuncFormatter(lambda value, _: format_inr(int(value))))
    axis.tick_params(axis="x", rotation=45)
    figure.tight_layout()
    return figure


def balance_line(frame: pd.DataFrame):
    """Create a cumulative balance trend using transactions ordered by date."""
    if frame.empty:
        return None
    data = frame.sort_values(["date", "id"]).copy()
    data["signed_amount"] = data["amount"].where(data["type"] == "Income", -data["amount"])
    data["balance"] = data["signed_amount"].cumsum()
    figure, axis = plt.subplots()
    axis.plot(data["date"], data["balance"], marker="o", color="#3366cc")
    axis.axhline(0, color="gray", linewidth=0.8)
    axis.set_title("Balance Trend")
    axis.set_xlabel("Date")
    axis.set_ylabel("Amount (₹)")
    axis.yaxis.set_major_formatter(FuncFormatter(lambda value, _: format_inr(int(value))))
    figure.autofmt_xdate()
    figure.tight_layout()
    return figure
