package org.example.dto;

import org.example.entity.TransactionType;
import org.example.entity.User;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionDto extends AbstractDto {
    private Instant date;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private String description;
    private Long userId;

    public TransactionDto() {}

    public TransactionDto(Instant date, TransactionType type, String category, BigDecimal amount, String description, Long id) {
        setDate(date);
        setType(type);
        setCategory(category);
        setAmount(amount);
        setDescription(description);
        setUserId(id);
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

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String name() {
        return description;
    }
}
