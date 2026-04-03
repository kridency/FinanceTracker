package org.example.dto;

import org.example.exception.ApplicationException;

import java.math.BigDecimal;
import java.time.YearMonth;

public class LimitDto extends AbstractDto {
    private YearMonth month;
    private BigDecimal amount;
    private Long userId;

    public LimitDto() {}

    public LimitDto(YearMonth month, BigDecimal amount, Long userId) {
        setMonth(month);
        setAmount(amount);
        setUserId(userId);
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String name() {
        return month.toString();
    }
}
