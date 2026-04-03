package org.example.service;

import org.example.dto.LimitDto;
import org.example.entity.*;
import org.example.exception.ApplicationException;
import org.example.mapper.LimitMapper;
import org.example.repository.LimitRepository;
import org.example.repository.UserRepository;
import org.example.util.Specification;

import java.time.YearMonth;
import java.util.Collection;
import java.util.Optional;

import static org.example.preset.FinancialTrackerInit.*;

public class LimitService implements CrudService<LimitDto> {
    private static LimitService INSTANCE;
    private final LimitRepository limitRepository;
    private final LimitMapper limitMapper;
    private final UserRepository userRepository;

    private LimitService() {
        limitMapper = LimitMapper.getInstance();
        limitRepository = LimitRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    public static LimitService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new LimitService();
        }
        return INSTANCE;
    }

    public LimitDto create(LimitDto data) {
        return Optional.ofNullable(data).map(limitMapper::limitDtoToLimit).map(entity -> {
                    entity.setUser(userRepository.getById(data.getUserId()).orElse(null));
                    return limitRepository.add(entity);
                }).map(limitMapper::limitToLimitDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public LimitDto update(LimitDto data) {
        return Optional.ofNullable(data).map(limitMapper::limitDtoToLimit).map(entity -> {
                    Limit oldLimit = limitRepository.getByMonthAndUserId(entity.getMonth(), data.getUserId())
                            .orElseThrow(() -> new ApplicationException(LIMIT_NOT_FOUND));
                    entity.setId(oldLimit.getId());
                    return limitRepository.update(entity);
                }).map(limitMapper::limitToLimitDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public LimitDto remove(LimitDto data) {
        return Optional.ofNullable(data).map(limitMapper::limitDtoToLimit).map(entity -> limitRepository.delete(
                        limitRepository.getByMonthAndUserId(entity.getMonth(), data.getUserId())
                                .orElseThrow(() -> new ApplicationException(LIMIT_NOT_FOUND))
                )).map(limitMapper::limitToLimitDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public LimitDto findByMonthAndId (YearMonth month, Long id) {
        return limitRepository.getByMonthAndUserId(month, id).map(limitMapper::limitToLimitDto).orElse(null);
    }

    private Collection<LimitDto> findAll() {
        return limitRepository.getAll().stream().map(limitMapper::limitToLimitDto).toList(); }

    public Collection<LimitDto> findAllByDto(LimitDto data) {
        return new Specification<>(data).apply(findAll()).stream().toList();
    }

    public LimitDto findById(Long id) {
        return Optional.ofNullable(id).map(limitRepository::getById)
                .filter(Optional::isPresent).map(Optional::get)
                .map(limitMapper::limitToLimitDto)
                .orElse(null);
    }
}
