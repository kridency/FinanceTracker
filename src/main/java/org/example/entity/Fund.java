package org.example.entity;

import java.math.BigDecimal;

public class Fund {
    private long id;
    private String title;
    private BigDecimal target;
    private BigDecimal savings;
    private User user;

    public Fund(String title, BigDecimal target, BigDecimal savings, User user) {
        setTitle(title);
        setTarget(target);
        setSavings(savings);
        setUser(user);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
