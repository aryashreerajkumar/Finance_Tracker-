"""Streamlit entry point for the Personal Financial Tracking Application."""

from __future__ import annotations

from datetime import date

import pandas as pd
import streamlit as st

import db
from charts import balance_line, monthly_bar, spending_pie
from reports import category_spending, format_inr, month_category_spending, monthly_summary, totals, transactions_frame
from validators import validate_transaction

st.set_page_config(page_title="Personal Finance Tracker", page_icon="💰", layout="wide")
db.init_db()

CATEGORIES = ["Food", "Travel", "Rent", "Bills", "Shopping", "Health", "Entertainment", "Education", "Salary", "Other"]


def load_frame(start: str | None = None, end: str | None = None, category: str | None = None, transaction_type: str | None = None) -> pd.DataFrame:
    """Load filtered records and convert them to a DataFrame."""
    return transactions_frame(db.get_transactions(start, end, category, transaction_type))


def show_transaction_form(form_key: str, record: dict | None = None) -> None:
    """Render a reusable add/edit transaction form."""
    values = record or {}
    amount_key = f"{form_key}_amount"
    with st.form(form_key, clear_on_submit=True):
        transaction_date = st.date_input("Date", value=pd.to_datetime(values.get("date", date.today())).date())
        transaction_type = st.selectbox("Type", ["Income", "Expense"], index=0 if values.get("type", "Expense") == "Income" else 1)
        category = st.selectbox("Category", CATEGORIES, index=CATEGORIES.index(values["category"]) if values.get("category") in CATEGORIES else len(CATEGORIES) - 1)
        amount = st.text_input(
            "Amount (₹)",
            value="" if not values else str(values.get("amount", "")),
            placeholder="Enter amount, e.g. 10000",
            key=amount_key,
        )
        description = st.text_input("Description", value=values.get("description", ""))
        submitted = st.form_submit_button("Save Transaction")
    if submitted:
        errors = validate_transaction(transaction_date, transaction_type, category, amount)
        if errors:
            for error in errors:
                st.error(error)
        else:
            date_text = transaction_date.isoformat()
            normalized_amount = int(str(amount).strip().replace(" ", "").replace(",", ""))
            if record:
                db.update_transaction(record["id"], date_text, transaction_type, category, normalized_amount, description.strip())
                st.success("Transaction updated.")
            else:
                db.add_transaction(date_text, transaction_type, category, normalized_amount, description.strip())
                st.success("Transaction added.")
            st.rerun()


def filters(key: str = "filters") -> tuple[date, date, str | None, str | None]:
    """Render shared report filters and return their values."""
    records = db.get_transactions()
    dates = [pd.to_datetime(row["date"]).date() for row in records]
    minimum = min(dates) if dates else date(date.today().year, 1, 1)
    with st.sidebar:
        st.subheader("Filters")
        selected_dates = st.date_input("Date range", value=(minimum, date.today()), key=f"{key}_dates")
        category = st.selectbox("Category", ["All categories"] + CATEGORIES, key=f"{key}_category")
        transaction_type = st.selectbox("Type", ["All types", "Income", "Expense"], key=f"{key}_type")
    start, end = (selected_dates if isinstance(selected_dates, tuple) and len(selected_dates) == 2 else (minimum, date.today()))
    return start, end, None if category == "All categories" else category, None if transaction_type == "All types" else transaction_type


def dashboard() -> None:
    """Render the home dashboard."""
    st.title("💰 Personal Finance Dashboard")
    frame = load_frame()
    if frame.empty:
        st.info("No transactions yet. Use **Add Transaction** to get started, or run `python seed_data.py` for demo data.")
        return
    overall = totals(frame)
    current_month = frame[(frame["date"].dt.year == date.today().year) & (frame["date"].dt.month == date.today().month)]
    month_totals = totals(current_month)
    cards = st.columns(3)
    cards[0].metric("Total Income", format_inr(overall["income"]))
    cards[1].metric("Total Expenses", format_inr(overall["expense"]))
    cards[2].metric("Current Balance", format_inr(overall["balance"]))
    st.subheader("This Month")
    month_cards = st.columns(3)
    month_cards[0].metric("Income", format_inr(month_totals["income"]))
    month_cards[1].metric("Expenses", format_inr(month_totals["expense"]))
    month_cards[2].metric("Balance", format_inr(month_totals["balance"]))
    st.subheader("Top 3 Spending Categories")
    top = category_spending(frame).head(3)
    st.dataframe(top.rename(columns={"amount": "Amount (INR)"}).assign(**{"Amount (INR)": top["amount"].map(format_inr)}), use_container_width=True, hide_index=True)


def manage_transactions() -> None:
    """Render filtered transaction records and edit/delete controls."""
    st.title("View / Manage Transactions")
    start, end, category, transaction_type = filters("manage")
    frame = load_frame(start.isoformat(), end.isoformat(), category, transaction_type)
    if frame.empty:
        st.info("No transactions match the selected filters.")
        return
    display_frame = frame.assign(date=frame["date"].dt.strftime("%Y-%m-%d"), amount=frame["amount"].map(format_inr))
    st.dataframe(display_frame.rename(columns={"amount": "Amount (INR)"}), use_container_width=True, hide_index=True)
    records = db.get_transactions(start.isoformat(), end.isoformat(), category, transaction_type)
    selected_id = st.selectbox("Select a transaction to edit or delete", [record["id"] for record in records], format_func=lambda value: f"#{value} - {next(r['category'] for r in records if r['id'] == value)}")
    selected = next(record for record in records if record["id"] == selected_id)
    left, right = st.columns(2)
    with left:
        if st.button("Delete selected transaction", type="secondary"):
            db.delete_transaction(selected_id)
            st.success("Transaction deleted.")
            st.rerun()
    with right:
        with st.expander("Edit selected transaction"):
            show_transaction_form("edit_transaction", selected)


def monthly_page() -> None:
    """Render selected-month and all-month summaries."""
    st.title("Monthly Summary")
    year = st.number_input("Year", min_value=2000, max_value=date.today().year, value=date.today().year, step=1)
    month = st.selectbox("Month", range(1, 13), index=date.today().month - 1, format_func=lambda value: date(2000, value, 1).strftime("%B"))
    frame = load_frame()
    selected = frame[(frame["date"].dt.year == year) & (frame["date"].dt.month == month)] if not frame.empty else frame
    summary = totals(selected)
    cards = st.columns(3)
    cards[0].metric("Income", format_inr(summary["income"]))
    cards[1].metric("Expense", format_inr(summary["expense"]))
    cards[2].metric("Net Balance", format_inr(summary["balance"]))
    st.subheader("Category-wise Spending")
    category_data = month_category_spending(frame, int(year), month)
    category_display = category_data.assign(amount=category_data["amount"].map(format_inr))
    st.dataframe(category_display.rename(columns={"amount": "Amount (INR)"}), use_container_width=True, hide_index=True)
    st.subheader("All Months")
    all_months = monthly_summary(frame)
    summary_display = all_months.assign(
        income=all_months["income"].map(format_inr),
        expense=all_months["expense"].map(format_inr),
        balance=all_months["balance"].map(format_inr),
    )
    st.dataframe(summary_display.rename(columns={"income": "Income (INR)", "expense": "Expense (INR)", "balance": "Balance (INR)"}), use_container_width=True, hide_index=True)


def visual_reports() -> None:
    """Render all charts using the shared date/category/type filters."""
    st.title("Visual Reports")
    start, end, category, transaction_type = filters("charts")
    frame = load_frame(start.isoformat(), end.isoformat(), category, transaction_type)
    if frame.empty:
        st.info("No data available for the selected filters.")
        return
    first, second = st.columns(2)
    pie = spending_pie(frame)
    bar = monthly_bar(frame)
    if pie:
        first.pyplot(pie)
    else:
        first.info("No expense data for the pie chart.")
    if bar:
        second.pyplot(bar)
    st.pyplot(balance_line(frame))


def export_page() -> None:
    """Render CSV download buttons for filtered transactions and summaries."""
    st.title("Export Data")
    start, end, category, transaction_type = filters("export")
    frame = load_frame(start.isoformat(), end.isoformat(), category, transaction_type)
    if frame.empty:
        st.info("No data available to export for the selected filters.")
        return
    export_frame = frame.copy()
    export_frame["date"] = export_frame["date"].dt.strftime("%Y-%m-%d")
    export_frame = export_frame.rename(columns={"amount": "Amount (INR)"})
    st.download_button("Download Transactions CSV", export_frame.to_csv(index=False), "transactions.csv", "text/csv")
    summary_export = monthly_summary(frame).rename(
        columns={"income": "Income (INR)", "expense": "Expense (INR)", "balance": "Balance (INR)"}
    )
    st.download_button("Download Monthly Summary CSV", summary_export.to_csv(index=False), "monthly_summary.csv", "text/csv")


def main() -> None:
    """Choose and render the requested application page."""
    page = st.sidebar.radio("Menu", ["Dashboard", "Add Transaction", "View / Manage Transactions", "Monthly Summary", "Visual Reports", "Export"])
    if page == "Dashboard":
        dashboard()
    elif page == "Add Transaction":
        st.title("Add Transaction")
        show_transaction_form("add_transaction")
    elif page == "View / Manage Transactions":
        manage_transactions()
    elif page == "Monthly Summary":
        monthly_page()
    elif page == "Visual Reports":
        visual_reports()
    else:
        export_page()


if __name__ == "__main__":
    main()
