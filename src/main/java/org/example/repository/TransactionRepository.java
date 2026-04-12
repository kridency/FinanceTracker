package org.example.repository;

import org.example.client.PostgreSQLClient;
import org.example.entity.*;
import org.example.exception.ApplicationException;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static org.example.preset.FinancialTrackerInit.*;

public class TransactionRepository implements CrudRepository<Transaction> {
    private final UserRepository userRepository;
    private final DataSource dataSource;

    private static final String INSERT_QUERY = "INSERT INTO \"transaction\" (date, type, category, amount, description, user_id) "
            + "VALUES (?,?,?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"transaction\" SET category=?, amount=?, description=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"transaction\" WHERE id=?";
    private static final String GET_UNIQUE_QUERY = "SELECT * FROM \"transaction\" WHERE date=? AND user_id=?";
    private static final String GET_BY_USERID_QUERY = "SELECT * FROM \"transaction\" WHERE user_id=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM \"transaction\"";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"transaction\" WHERE id=?";

    public TransactionRepository() {
        dataSource = CLIENT.getDataSource();
        userRepository = new UserRepository();
    }

    public Transaction add(Transaction transaction) {
        return Optional.ofNullable(transaction).map(newValue -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(INSERT_QUERY)) {
                var date = Optional.ofNullable(newValue.getDate()).orElse(Instant.now());
                var userId = Optional.ofNullable(newValue.getUser()).map(User::getId).orElse(0L);
                statement.setTimestamp(1, Timestamp.from(date), UTC_CALENDAR);
                statement.setString(2, newValue.getType().toString());
                statement.setString(3, newValue.getCategory());
                statement.setBigDecimal(4, newValue.getAmount());
                statement.setString(5, newValue.getDescription());
                statement.setLong(6, userId);
                return statement.executeUpdate() == 1 ? newValue : null;
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(TRANSACTION_NOT_FOUND));
    }

    public Transaction update(Transaction transaction) {
        return Optional.ofNullable(transaction).map(newValue -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(UPDATE_QUERY)) {
                statement.setString(1, newValue.getCategory());
                statement.setBigDecimal(2, newValue.getAmount());
                statement.setString(3, newValue.getDescription());
                statement.setLong(4, newValue.getId());
                return statement.executeUpdate() == 1 ? newValue : null;
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(TRANSACTION_NOT_FOUND));
    }

    public Transaction delete(Transaction transaction) {
        return Optional.ofNullable(transaction).map(value -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(DELETE_QUERY)) {
                statement.setLong(1, value.getId());
                return statement.executeUpdate() == 1 ? value : null;
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(TRANSACTION_NOT_FOUND));
    }

    public Optional<Transaction> getByDateAndUserId(Instant instant, Long userId) {
        return Optional.ofNullable(userId).map(id -> {
            var user = userRepository.getById(id).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
            return Optional.ofNullable(instant).map(date -> {
                try (var connection = dataSource.getConnection();
                     var statement = connection.prepareStatement(GET_UNIQUE_QUERY)) {
                    statement.setTimestamp(1, Timestamp.from(date), UTC_CALENDAR);
                    statement.setLong(2, id);
                    try (var resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            var entity = new Transaction(
                                    resultSet.getTimestamp("date").toLocalDateTime().toInstant(ZoneOffset.UTC),
                                    TransactionType.valueOf(resultSet.getString("type")),
                                    resultSet.getString("category"),
                                    resultSet.getBigDecimal("amount"),
                                    resultSet.getString("description"),
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
                }
            });
        }).orElseGet(Optional::empty);
    }

    public Collection<Transaction> getAllByUserId(Long userId) {
        return Optional.ofNullable(userId).map(id -> {
            var user = userRepository.getById(id).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(GET_BY_USERID_QUERY)) {
                statement.setLong(1, id);
                try (var resultSet = statement.executeQuery()) {
                    return Stream.generate(() -> {
                        try {
                            if(resultSet.next()) {
                                var entity = new Transaction(
                                        resultSet.getTimestamp("date").toLocalDateTime().toInstant(ZoneOffset.UTC),
                                        TransactionType.valueOf(resultSet.getString("type")),
                                        resultSet.getString("category"),
                                        resultSet.getBigDecimal("amount"),
                                        resultSet.getString("description"),
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

    public Collection<Transaction> getAll() {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(GET_ALL_QUERY);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                        var entity = new Transaction(
                                resultSet.getTimestamp("date").toLocalDateTime().toInstant(ZoneOffset.UTC),
                                TransactionType.valueOf(resultSet.getString("type")),
                                resultSet.getString("category"),
                                resultSet.getBigDecimal("amount"),
                                resultSet.getString("description"),
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
        } catch (SQLException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public Optional<Transaction> getById(Long transactionId) {
        return Optional.ofNullable(transactionId).map(id -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
                statement.setLong(1, id);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    if (resultSet.next()) {
                        var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                        var entity = new Transaction(
                                resultSet.getTimestamp("date").toLocalDateTime().toInstant(ZoneOffset.UTC),
                                TransactionType.valueOf(resultSet.getString("type")),
                                resultSet.getString("category"),
                                resultSet.getBigDecimal("amount"),
                                resultSet.getString("description"),
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
            }
        });
    }
}
