package org.example.service;

import java.util.Collection;

public interface CrudService<T> {
    T create(T data);
    T update(T data);
    T remove(T data);
    T findById(Long id);
    Collection<T> findAllByDto(T data);
}
