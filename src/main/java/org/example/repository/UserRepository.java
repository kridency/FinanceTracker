package org.example.repository;

import org.example.client.PostgreSQLClient;
import org.example.entity.RoleType;
import org.example.entity.StatusType;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

import static org.example.preset.FinancialTrackerInit.USER_NOT_FOUND;

public class UserRepository implements CrudRepository<User> {
    private static DataSource dataSource;

    private static final String INSERT_QUERY = "INSERT INTO \"user\" (name, email, password, role, status) VALUES (?,?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"user\" SET name=?, email=?, password=?, role=?, status=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"user\" WHERE id=?";
    private static final String GET_UNIQUE_QUERY = "SELECT * FROM \"user\" WHERE email=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM \"user\"";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"user\" WHERE id=?";

    public UserRepository() {
        dataSource = CLIENT.getDataSource();
    }

    public User add(User user) throws ApplicationException {
        return Optional.ofNullable(user).map(newValue -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(INSERT_QUERY)) {
                statement.setString(1, newValue.getName());
                statement.setString(2, newValue.getEmail());
                statement.setString(3, newValue.getPassword());
                statement.setString(4, newValue.getRole().toString());
                statement.setString(5, newValue.getStatus().toString());
                return statement.executeUpdate() == 1 ? newValue : null;
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

    public User update(User user) {
        return Optional.ofNullable(user).map(newValue -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(UPDATE_QUERY)) {
                statement.setString(1, newValue.getName());
                statement.setString(2, newValue.getEmail());
                statement.setString(3, newValue.getPassword());
                statement.setString(4, newValue.getRole().toString());
                statement.setString(5, newValue.getStatus().toString());
                statement.setLong(6, newValue.getId());
                return statement.executeUpdate() == 1 ? newValue : null;
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

    public User delete(User user) {
        return Optional.ofNullable(user).map(value -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(DELETE_QUERY)) {
                statement.setLong(1, value.getId());
                return statement.executeUpdate() == 1 ? value : null;
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

    public Collection<User> getAll() {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(GET_ALL_QUERY);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var entity = new User(
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                resultSet.getString("password"));
                        entity.setId(resultSet.getLong("id"));
                        entity.setRole(RoleType.valueOf(resultSet.getString("role")));
                        entity.setStatus(StatusType.valueOf(resultSet.getString("status")));
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

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(email).map(value -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(GET_UNIQUE_QUERY,
                         ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                statement.setString(1, email);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    if (resultSet.getRow() == 1) {
                        var entity = new User(
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                resultSet.getString("password"));
                        entity.setId(resultSet.getLong("id"));
                        entity.setRole(RoleType.valueOf(resultSet.getString("role")));
                        entity.setStatus(StatusType.valueOf(resultSet.getString("status")));
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

    public Optional<User> getById(Long userId) {
        return Optional.ofNullable(userId).map(id -> {
            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
                statement.setLong(1, id);
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        User entity = new User(
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                resultSet.getString("password"));
                        entity.setId(resultSet.getLong("id"));
                        entity.setRole(RoleType.valueOf(resultSet.getString("role")));
                        entity.setStatus(StatusType.valueOf(resultSet.getString("status")));
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
