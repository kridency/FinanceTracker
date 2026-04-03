package org.example.repository;

import org.example.client.PostgreSQLClient;
import org.postgresql.ds.PGConnectionPoolDataSource;
import java.util.Optional;

public interface CrudRepository<T> {
    PGConnectionPoolDataSource datasource = PostgreSQLClient.getInstance().getDataSource();

    T add(T t);
    T update(T t);
    T delete(T t);
    Optional<T> getById(Long id);
}
