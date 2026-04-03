package org.example.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    private long id;
    private Instant date;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private String description;
    private User user;

    public Transaction(Instant date, TransactionType type, String category, BigDecimal amount, String description, User user) {
        setDate(date);
        setType(type);
        setCategory(category);
        setAmount(amount);
        setDescription(description);
        setUser(user);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}
