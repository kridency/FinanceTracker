package org.example.repository;

import org.example.entity.Limit;
import org.example.entity.User;
import org.example.exception.ApplicationException;

import java.sql.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.example.preset.FinancialTrackerInit.*;

public class LimitRepository implements CrudRepository<Limit> {
    private static LimitRepository INSTANCE;
    private final CrudRepository<User> userRepository;

    private static final String INSERT_QUERY = "INSERT INTO \"limit\" (month, amount, user_id) VALUES (?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"limit\" SET amount=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"limit\" WHERE id=?";
    private static final String GET_UNIQUE_QUERY = "SELECT * FROM \"limit\" WHERE user_id=? AND month=?";
    private static final String GET_BY_USERID_QUERY = "SELECT * FROM \"limit\" WHERE user_id=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM \"limit\"";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"limit\" WHERE id=?";

    private LimitRepository() {
        userRepository = UserRepository.getInstance();
    }

    public static LimitRepository getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new LimitRepository();
        }
        return INSTANCE;
    }

    public Limit add(Limit limit) {
        return Optional.ofNullable(limit).map(newValue -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(INSERT_QUERY)) {
                var month = Optional.ofNullable(newValue.getMonth()).map(x -> x.format(MONTH_FORMAT))
                        .orElse(YearMonth.now().format(MONTH_FORMAT));
                var userId = Optional.ofNullable(newValue.getUser()).map(User::getId).orElse(0L);
                statement.setString(1, month);
                statement.setBigDecimal(2, newValue.getAmount());
                statement.setLong(3, userId);
                return statement.executeUpdate () == 1 ? newValue : null;
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(LIMIT_NOT_FOUND));
    }

    public Limit update(Limit limit) {
        return Optional.ofNullable(limit).map(newValue -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(UPDATE_QUERY)) {
                statement.setBigDecimal(1, newValue.getAmount());
                statement.setLong(2, newValue.getId());
                return statement.executeUpdate () == 1 ? newValue : null;
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(LIMIT_NOT_FOUND));
    }

    public Limit delete(Limit limit) {
        return Optional.ofNullable(limit).map(value -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(DELETE_QUERY)) {
                statement.setLong(1, value.getId());
                return statement.executeUpdate() == 1 ? value : null;
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(LIMIT_NOT_FOUND));
    }

    public Optional<Limit> getByMonthAndUserId(YearMonth month, Long userId) {
        return Optional.ofNullable(userId).map(id -> {
            var user = userRepository.getById(id).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
            return Optional.ofNullable(month).map(value -> {
                try (var connection = datasource.getConnection();
                     var statement = connection.prepareStatement(GET_UNIQUE_QUERY)) {
                    statement.setLong(1, id);
                    statement.setString(2, value.format(MONTH_FORMAT));
                    try (var resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            var entity = new Limit(value, resultSet.getBigDecimal("amount"), user);
                            entity.setId(resultSet.getLong("id"));
                            return entity;
                        } else {
                            return null;
                        }
                    } catch (SQLException e) {
                        throw new ApplicationException(e.getMessage());
                    }
                } catch (Exception e) {
                    throw new ApplicationException(e.getMessage());
                }
            });
        }).orElseGet(Optional::empty);
    }

    public Collection<Limit> getAllByUserId(Long userId) {
        return Optional.ofNullable(userId).map(id -> {
            var user = userRepository.getById(id).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(GET_BY_USERID_QUERY)) {
                statement.setLong(1, id);
                try (var resultSet = statement.executeQuery()) {
                    return Stream.generate(() -> {
                        try {
                            if (resultSet.next()) {
                                var entity = new Limit(
                                        YearMonth.parse(resultSet.getString("month")),
                                        resultSet.getBigDecimal("amount"),
                                        user);
                                entity.setId(resultSet.getLong("id"));
                                return entity;
                            } else {
                                return null;
                            }
                        } catch (SQLException e) {
                            throw new ApplicationException(e.getMessage());
                        }
                    }).takeWhile(Objects::nonNull).toList();
                } catch (Exception e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseGet(Collections::emptyList);
    }

    public Collection<Limit> getAll() {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_ALL_QUERY);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                        var entity = new Limit(
                                YearMonth.parse(resultSet.getString("month")),
                                resultSet.getBigDecimal("amount"),
                                user);
                        entity.setId(resultSet.getLong("id"));
                        return entity;
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).takeWhile(Objects::nonNull).toList();
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public Optional<Limit> getById(Long limitId) {
        return Optional.ofNullable(limitId).map(id -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
                statement.setLong(1, id);
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                        var entity = new Limit(
                                YearMonth.parse(resultSet.getString("month")),
                                resultSet.getBigDecimal("amount"),
                                user);
                        entity.setId(resultSet.getLong("id"));
                        return entity;
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }});
    }
}
