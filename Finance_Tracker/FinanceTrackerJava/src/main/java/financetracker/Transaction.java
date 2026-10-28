package financetracker;

import java.time.LocalDate;

/** Represents one income or expense transaction. */
public class Transaction {
    private long id;
    private LocalDate date;
    private String type;
    private String category;
    private long amount;
    private String description;

    /** Creates a transaction. */
    public Transaction(long id, LocalDate date, String type, String category, long amount, String description) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description == null ? "" : description;
    }

    /** Creates a new unsaved transaction. */
    public Transaction(LocalDate date, String type, String category, long amount, String description) {
        this(0, date, type, category, amount, description);
    }

    /** Returns the database id. */
    public long getId() { return id; }
    /** Returns the date. */
    public LocalDate getDate() { return date; }
    /** Returns the type. */
    public String getType() { return type; }
    /** Returns the category. */
    public String getCategory() { return category; }
    /** Returns the amount. */
    public long getAmount() { return amount; }
    /** Returns the description. */
    public String getDescription() { return description; }
    /** Sets the database id. */
    public void setId(long id) { this.id = id; }
    /** Sets the date. */
    public void setDate(LocalDate date) { this.date = date; }
    /** Sets the type. */
    public void setType(String type) { this.type = type; }
    /** Sets the category. */
    public void setCategory(String category) { this.category = category; }
    /** Sets the amount. */
    public void setAmount(long amount) { this.amount = amount; }
    /** Sets the description. */
    public void setDescription(String description) { this.description = description == null ? "" : description; }
}
