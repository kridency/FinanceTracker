package org.example.repository;

import org.example.client.PostgreSQLClient;
import org.example.entity.Fund;
import org.example.entity.Limit;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.example.preset.FinancialTrackerInit.*;

public class FundRepository implements CrudRepository<Fund> {
    private static FundRepository INSTANCE;
    private final CrudRepository<User> userRepository;

    private static final String INSERT_QUERY = "INSERT INTO \"fund\" (title, savings, target, user_id) VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"fund\" SET target=?, savings=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"fund\" WHERE id=?";
    private static final String GET_UNIQUE_QUERY = "SELECT * FROM \"fund\" WHERE title=? AND user_id=?";
    private static final String GET_BY_USERID_QUERY = "SELECT * FROM \"fund\" WHERE user_id=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM \"fund\"";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"fund\" WHERE id=?";

    private FundRepository() {
        userRepository = UserRepository.getInstance();
    }

    public static FundRepository getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FundRepository();
        }
        return INSTANCE;
    }

    public Fund add(Fund fund) {
        return Optional.ofNullable(fund).map(newValue -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(INSERT_QUERY)) {
                statement.setString(1, newValue.getTitle());
                statement.setBigDecimal(2, newValue.getSavings());
                statement.setBigDecimal(3, newValue.getTarget());
                statement.setLong(4, Optional.ofNullable(newValue.getUser()).map(User::getId)
                        .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND)));
                return statement.executeUpdate () == 1 ? newValue : null;
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(FUND_NOT_FOUND));
    }

    public Fund update(Fund fund) {
        return Optional.ofNullable(fund).map(newValue -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(UPDATE_QUERY)) {
                statement.setBigDecimal(1, newValue.getTarget());
                statement.setBigDecimal(2, newValue.getSavings());
                statement.setLong(3, newValue.getId());
                return statement.executeUpdate () == 1 ? newValue : null;
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(FUND_NOT_FOUND));
    }

    public Fund delete(Fund fund) {
        return Optional.ofNullable(fund).map(value -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(DELETE_QUERY)) {
                statement.setLong(1, value.getId());
                return statement.executeUpdate() == 1 ? value : null;
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(FUND_NOT_FOUND));
    }

    public Optional<Fund> getByTitleAndUserId(String title, Long userId) {
        return Optional.ofNullable(userId).map(id -> {
            var user = userRepository.getById(id).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
            return Optional.ofNullable(title).map(value -> {
                try (var connection = datasource.getConnection();
                     var statement = connection.prepareStatement(GET_UNIQUE_QUERY)) {
                    statement.setString(1, title);
                    statement.setLong(2, userId);
                    try (var resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            var entity = new Fund(
                                    resultSet.getString("title"),
                                    resultSet.getBigDecimal("target"),
                                    resultSet.getBigDecimal("savings"),
                                    user
                            );
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
        }).orElseGet(Optional::empty);
    }

    public Collection<Fund> getAllByUserId(Long userId) {
        return Optional.ofNullable(userId).map(id -> {
            var user = userRepository.getById(id).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(GET_BY_USERID_QUERY)) {
                statement.setLong(1, id);
                try (var resultSet = statement.executeQuery()) {
                    return Stream.generate(() -> {
                        try {
                            if (resultSet.next()) {
                                var entity = new Fund(
                                        resultSet.getString("title"),
                                        resultSet.getBigDecimal("target"),
                                        resultSet.getBigDecimal("savings"),
                                        user
                                );
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

    public Collection<Fund> getAll() {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_ALL_QUERY);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                        var entity = new Fund(
                                resultSet.getString("title"),
                                resultSet.getBigDecimal("target"),
                                resultSet.getBigDecimal("savings"),
                                user
                        );
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

    public Optional<Fund> getById(Long fundId) {
        return Optional.ofNullable(fundId).map(id -> {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                    var entity = new Fund(
                            resultSet.getString("title"),
                            resultSet.getBigDecimal("target"),
                            resultSet.getBigDecimal("savings"),
                            user
                    );
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
