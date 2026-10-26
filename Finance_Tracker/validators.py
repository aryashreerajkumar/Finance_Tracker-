"""Validation functions shared by forms and the seed script."""

from __future__ import annotations

from datetime import date, datetime


def validate_date(value: str | date) -> str | None:
    """Return an error message, or None when the date is valid."""
    text = value.isoformat() if isinstance(value, date) else str(value).strip()
    try:
        parsed = datetime.strptime(text, "%Y-%m-%d").date()
    except ValueError:
        return "Date must use the YYYY-MM-DD format."
    if parsed > date.today():
        return "Date cannot be in the future."
    return None


def validate_amount(value: object) -> str | None:
    """Validate a typed amount after allowing spaces and commas as aids."""
    text = "" if value is None else str(value).strip()
    if not text:
        return "Please enter the amount."
    normalized = text.replace(" ", "").replace(",", "")
    if not normalized.isascii() or not normalized.isdigit():
        return "Amount must be a whole number greater than 0."
    amount = int(normalized)
    if amount <= 0 or amount > 100_000_000:
        return "Amount must be a whole number greater than 0."
    return None


def validate_category(category: str) -> str | None:
    """Ensure a category contains non-whitespace text."""
    if not category or not category.strip():
        return "Category must not be empty."
    return None


def validate_type(transaction_type: str) -> str | None:
    """Ensure the transaction type is Income or Expense."""
    if transaction_type not in {"Income", "Expense"}:
        return "Type must be Income or Expense."
    return None


def validate_transaction(
    transaction_date: str | date,
    transaction_type: str,
    category: str,
    amount: object,
) -> list[str]:
    """Return all validation errors for a transaction form."""
    errors = []
    for error in (
        validate_date(transaction_date),
        validate_type(transaction_type),
        validate_category(category),
        validate_amount(amount),
    ):
        if error:
            errors.append(error)
    return errors
