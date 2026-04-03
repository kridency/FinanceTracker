package org.example.dto;

import org.example.entity.RoleType;
import org.example.entity.StatusType;
import org.example.exception.ApplicationException;

import java.util.regex.Pattern;

import static org.example.preset.FinancialTrackerInit.EMAIL_ERROR;

public class UserDto extends AbstractDto {
    private Long userId;
    private String name;
    private String email;
    private String password;
    private RoleType role;
    private StatusType status;

    public UserDto() {}

    public UserDto(String name, String email, String password) {
        setName(name);
        setEmail(email);
        setPassword(password);
        setRole(RoleType.USER);
        setStatus(StatusType.ACTIVE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !Pattern.compile("\\S+@(\\S+\\.){0,}\\w+").matcher(email).matches()) {
            throw new ApplicationException(EMAIL_ERROR);
        } else {
            this.email = email;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && password.isBlank()) {
            throw new ApplicationException("Не указано значение для поля password");
        } else {
            this.password = password;
        }
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String name() {
        return email;
    }
}
