package org.example.dto;

import org.example.entity.User;
import org.example.exception.ApplicationException;

import java.math.BigDecimal;

public class FundDto extends AbstractDto {
    private String title;
    private BigDecimal target;
    private BigDecimal savings;
    private Long userId;

    public FundDto() {}

    public FundDto(String title, BigDecimal target, Long userId) {
        setTitle(title);
        setTarget(target);
        setSavings(BigDecimal.ZERO);
        setUserId(userId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String name() {
        return title;
    }
}
