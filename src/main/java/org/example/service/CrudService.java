package org.example.service;

import org.example.dto.UserDto;
import org.example.entity.User;

import java.util.Collection;
import java.util.Optional;

public interface CrudService<T> {
    T create(T data);
    T update(T data);
    T remove(T data);
    T findById(Long id);
    Collection<T> findAllByDto(T data);
}
