package org.example.repository;

import org.example.client.PostgreSQLClient;
import org.example.entity.Invocation;
import org.example.entity.Limit;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.example.preset.FinancialTrackerInit.*;

public class InvocationRepository implements CrudRepository<Invocation> {
    private static InvocationRepository INSTANCE;
    private final CrudRepository<User> userRepository;

    private static final String INSERT_QUERY = "INSERT INTO \"invocation\" (date, endpoint, user_id) VALUES (?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"invocation\" SET endpoint=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"invocation\" WHERE id=?";
    private static final String GET_UNIQUE_QUERY = "SELECT * FROM \"invocation\" WHERE date=? AND user_id=?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"invocation\" WHERE id=?";

    private InvocationRepository() {
        userRepository = UserRepository.getInstance();
    }

    public static InvocationRepository getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new InvocationRepository();
        }
        return INSTANCE;
    }

    public Invocation add(Invocation invocation) {
        return Optional.ofNullable(invocation).map(newValue -> {
            try (var connection = datasource.getConnection()) {
                try (var statement = connection.prepareStatement(INSERT_QUERY)) {
                    statement.setTimestamp(1, Timestamp.from(newValue.getDate()), UTC_CALENDAR);
                    statement.setString(2, newValue.getEndpoint());
                    statement.setLong(3, Optional.ofNullable(newValue.getUser()).map(User::getId)
                            .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND)));
                    return statement.executeUpdate () == 1 ? newValue : null;
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(INVOCATION_NOT_FOUND));
    }

    public Invocation update(Invocation invocation) {
        return Optional.ofNullable(invocation).map(newValue -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(UPDATE_QUERY)) {
                try {
                    statement.setString(1, newValue.getEndpoint());
                    statement.setLong(2, newValue.getId());
                    return statement.executeUpdate() == 1 ? newValue : null;
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(INVOCATION_NOT_FOUND));
    }

    public Invocation delete(Invocation invocation) {
        return Optional.ofNullable(invocation).map(value -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(DELETE_QUERY)) {
                try {
                    statement.setLong(1, value.getId());
                    return statement.executeUpdate() == 1 ? value : null;
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(INVOCATION_NOT_FOUND));
    }

    public Optional<Invocation> getByDateAndUser(Instant instant, User user) {
        return Optional.ofNullable(instant).map(date ->
                Optional.ofNullable(user).map(principal -> {
                    try (var connection = datasource.getConnection();
                         var statement = connection.prepareStatement(GET_UNIQUE_QUERY)) {
                        statement.setTimestamp(1, Timestamp.from(date), UTC_CALENDAR);
                        statement.setLong(2, principal.getId());
                        try (var resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                var entity = new Invocation(resultSet.getString("endpoint"), principal);
                                entity.setId(resultSet.getLong("id"));
                                entity.setDate(resultSet.getTimestamp("date").toInstant());
                                return entity;
                            }
                            return null;
                        } catch (SQLException e) {
                            throw new ApplicationException(e.getMessage());
                        }
                    } catch (Exception e) {
                        throw new ApplicationException(e.getMessage());
                    }
                })
        ).orElseThrow(() -> new ApplicationException(DATE_ERROR));
    }

    public Optional<Invocation> getById(Long id) {
        return Optional.ofNullable(id).map(value -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
                statement.setLong(1, value);
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                        var entity = new Invocation(resultSet.getString("endpoint"), user);
                        entity.setId(resultSet.getLong("id"));
                        return entity;
                    }
                    return null;
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        });
    }
}
