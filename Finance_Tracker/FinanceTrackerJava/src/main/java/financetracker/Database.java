package financetracker;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Provides SQLite persistence with prepared statements for every query. */
public class Database {
    private static final String URL = "jdbc:sqlite:finance.db";

    /** Opens the database and creates its table when needed. */
    public Database() throws SQLException {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        date TEXT NOT NULL,
                        type TEXT NOT NULL CHECK(type IN ('Income','Expense')),
                        category TEXT NOT NULL,
                        amount INTEGER NOT NULL CHECK(amount > 0),
                        description TEXT
                    )
                    """);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Inserts a transaction and returns its generated id. */
    public long add(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions(date,type,category,amount,description) VALUES(?,?,?,?,?)";
        try (Connection connection = connect(); PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, transaction);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("The database did not return a transaction id.");
    }

    /** Updates an existing transaction. */
    public void update(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET date=?,type=?,category=?,amount=?,description=? WHERE id=?";
        try (Connection connection = connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, transaction);
            ps.setLong(6, transaction.getId());
            ps.executeUpdate();
        }
    }

    /** Deletes a transaction by id. */
    public void delete(long id) throws SQLException {
        try (Connection connection = connect(); PreparedStatement ps = connection.prepareStatement("DELETE FROM transactions WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /** Queries transactions using optional inclusive date, category, and type filters. */
    public List<Transaction> query(LocalDate from, LocalDate to, String category, String type) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id,date,type,category,amount,description FROM transactions WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        if (from != null) { sql.append(" AND date >= ?"); parameters.add(from.toString()); }
        if (to != null) { sql.append(" AND date <= ?"); parameters.add(to.toString()); }
        if (category != null && !category.isBlank() && !"All".equals(category)) { sql.append(" AND category = ?"); parameters.add(category); }
        if (type != null && !type.isBlank() && !"All".equals(type)) { sql.append(" AND type = ?"); parameters.add(type); }
        sql.append(" ORDER BY date DESC, id DESC");
        try (Connection connection = connect(); PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) ps.setObject(i + 1, parameters.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Transaction> result = new ArrayList<>();
                while (rs.next()) result.add(read(rs));
                return result;
            }
        }
    }

    /** Queries all transactions. */
    public List<Transaction> all() throws SQLException { return query(null, null, "All", "All"); }

    private void bind(PreparedStatement ps, Transaction t) throws SQLException {
        ps.setString(1, t.getDate().toString());
        ps.setString(2, t.getType());
        ps.setString(3, t.getCategory());
        ps.setLong(4, t.getAmount());
        ps.setString(5, t.getDescription());
    }

    private Transaction read(ResultSet rs) throws SQLException {
        return new Transaction(rs.getLong("id"), LocalDate.parse(rs.getString("date")), rs.getString("type"),
                rs.getString("category"), rs.getLong("amount"), rs.getString("description"));
    }
}
