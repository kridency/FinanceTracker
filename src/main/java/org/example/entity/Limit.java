package org.example.entity;

import java.math.BigDecimal;
import java.time.YearMonth;

public class Limit {
    private long id;
    private YearMonth month;
    private BigDecimal amount;
    private User user;

    public Limit(YearMonth month, BigDecimal amount, User user) {
        setMonth(month);
        setAmount(amount);
        setUser(user);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
