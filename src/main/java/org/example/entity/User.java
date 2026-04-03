package org.example.entity;

import org.example.exception.ApplicationException;
import org.mapstruct.Named;

import java.util.regex.Pattern;

import static org.example.preset.FinancialTrackerInit.EMAIL_ERROR;

public class User {
    private long id;
    private String name;
    private String email;
    private String password;
    private RoleType role;
    private StatusType status;

    public User(String name, String email, String password) {
        setName(name);
        setEmail(email);
        setPassword(password);
        setRole(RoleType.USER);
        setStatus(StatusType.ACTIVE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        if (!Pattern.compile("\\S+@(\\S+\\.){0,}\\w+").matcher(email).matches()) {
            throw new ApplicationException(EMAIL_ERROR);
        } else {
            this.email = email;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
