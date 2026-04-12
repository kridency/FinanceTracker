package org.example.repository;

import org.example.client.PostgreSQLClient;
import javax.sql.DataSource;
import java.util.Optional;

public interface CrudRepository<T> {
    PostgreSQLClient CLIENT = new PostgreSQLClient();

    T add(T t);
    T update(T t);
    T delete(T t);
    Optional<T> getById(Long id);
}
