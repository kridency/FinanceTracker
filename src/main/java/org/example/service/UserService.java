package org.example.service;

import org.example.dto.UserDto;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.util.Specification;

import java.util.Collection;
import java.util.Optional;

import static org.example.preset.FinancialTrackerInit.*;

public class UserService implements CrudService<UserDto> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService() {
        userMapper = UserMapper.getInstance();
        userRepository = new UserRepository();
    }

    @Override
    public UserDto create(UserDto data) {
        return Optional.ofNullable(data).map(userMapper::userDtoToUser).map(userRepository::add)
                .map(userMapper::userToUserDto).orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    @Override
    public UserDto update(UserDto data) {
        return Optional.ofNullable(data).map(userMapper::userDtoToUser)
                .map(entity -> {
                    User oldUser = userRepository.getByEmail(data.getEmail())
                            .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
                    entity.setId(oldUser.getId());
                    return userRepository.update(entity);
                }).map(userMapper::userToUserDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    @Override
    public UserDto remove(UserDto data) {
        return Optional.ofNullable(data).map(userMapper::userDtoToUser)
                .map(entity -> {
                    User oldUser = userRepository.getByEmail(data.getEmail())
                            .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
                    entity.setId(oldUser.getId());
                    return userRepository.delete(entity);
                }).map(userMapper::userToUserDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    private Collection<UserDto> findAll() {
        return userRepository.getAll().stream().map(userMapper::userToUserDto).toList(); }

    public Collection<UserDto> findAllByDto (UserDto data) {
        return new Specification<>(data).apply(findAll()).stream().toList();
    }

    public UserDto findByEmail(String email) {
        return Optional.ofNullable(email).map(userRepository::getByEmail)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::userToUserDto)
                .orElse(null);
    }

    public UserDto findById(Long id) {
        return Optional.ofNullable(id).map(userRepository::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::userToUserDto)
                .orElse(null);
    }

    public User loadUserByUsername(String username) {
        return userRepository.getByEmail(username).orElse(null);
    }
}
