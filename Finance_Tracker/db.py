"""SQLite persistence helpers for the personal finance tracker."""

from __future__ import annotations

import sqlite3
from contextlib import contextmanager
from pathlib import Path
from typing import Any

DATABASE_PATH = Path(__file__).with_name("finance.db")


def _connect() -> sqlite3.Connection:
    """Open a database connection with rows accessible by column name."""
    connection = sqlite3.connect(DATABASE_PATH)
    connection.row_factory = sqlite3.Row
    return connection


@contextmanager
def _connection():
    """Yield a connection and always close it after the operation."""
    connection = _connect()
    try:
        with connection:
            yield connection
    finally:
        connection.close()


def init_db() -> None:
    """Create the integer-amount table, replacing an older decimal schema."""
    with _connection() as connection:
        existing = connection.execute("PRAGMA table_info(transactions)").fetchall()
        amount_column = next((row for row in existing if row["name"] == "amount"), None)
        if amount_column is not None and amount_column["type"].upper() != "INTEGER":
            connection.execute("DROP TABLE transactions")
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                type TEXT NOT NULL CHECK (type IN ('Income', 'Expense')),
                category TEXT NOT NULL,
                amount INTEGER NOT NULL CHECK (amount > 0),
                description TEXT NOT NULL DEFAULT ''
            )
            """
        )


def add_transaction(
    date: str,
    transaction_type: str,
    category: str,
    amount: int,
    description: str = "",
) -> int:
    """Insert a transaction and return its generated ID."""
    with _connection() as connection:
        cursor = connection.execute(
            """
            INSERT INTO transactions (date, type, category, amount, description)
            VALUES (?, ?, ?, ?, ?)
            """,
            (date, transaction_type, category, amount, description),
        )
        return int(cursor.lastrowid)


def get_transactions(
    start_date: str | None = None,
    end_date: str | None = None,
    category: str | None = None,
    transaction_type: str | None = None,
) -> list[dict[str, Any]]:
    """Return transactions matching the optional filters, newest first."""
    clauses: list[str] = []
    parameters: list[Any] = []
    if start_date:
        clauses.append("date >= ?")
        parameters.append(start_date)
    if end_date:
        clauses.append("date <= ?")
        parameters.append(end_date)
    if category:
        clauses.append("category = ?")
        parameters.append(category)
    if transaction_type:
        clauses.append("type = ?")
        parameters.append(transaction_type)

    query = "SELECT id, date, type, category, amount, description FROM transactions"
    if clauses:
        query += " WHERE " + " AND ".join(clauses)
    query += " ORDER BY date DESC, id DESC"
    with _connection() as connection:
        rows = connection.execute(query, parameters).fetchall()
    return [dict(row) for row in rows]


def update_transaction(
    transaction_id: int,
    date: str,
    transaction_type: str,
    category: str,
    amount: int,
    description: str = "",
) -> bool:
    """Update a transaction and report whether a row was changed."""
    with _connection() as connection:
        cursor = connection.execute(
            """
            UPDATE transactions
            SET date = ?, type = ?, category = ?, amount = ?, description = ?
            WHERE id = ?
            """,
            (date, transaction_type, category, amount, description, transaction_id),
        )
        return cursor.rowcount > 0


def delete_transaction(transaction_id: int) -> bool:
    """Delete one transaction and report whether a row was removed."""
    with _connection() as connection:
        cursor = connection.execute(
            "DELETE FROM transactions WHERE id = ?",
            (transaction_id,),
        )
        return cursor.rowcount > 0
