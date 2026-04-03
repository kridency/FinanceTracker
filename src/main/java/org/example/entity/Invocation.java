package org.example.entity;
import java.time.Instant;

public class Invocation {
    private long id;
    private Instant date;
    private String endpoint;
    private User user;

    public Invocation(String endpoint, User user) {
        setDate(Instant.now());
        setEndpoint(endpoint);
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
