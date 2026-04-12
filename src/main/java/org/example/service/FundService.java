package org.example.service;

import org.example.dto.FundDto;
import org.example.entity.*;
import org.example.exception.ApplicationException;
import org.example.mapper.FundMapper;
import org.example.repository.FundRepository;
import org.example.repository.UserRepository;
import org.example.util.Specification;

import java.util.*;

import static org.example.preset.FinancialTrackerInit.*;

public class FundService implements CrudService<FundDto> {
    private final FundRepository fundRepository;
    private final FundMapper fundMapper;
    private final UserRepository userRepository;

    public FundService() {
        userRepository = new UserRepository();
        fundRepository = new FundRepository();
        fundMapper = FundMapper.getInstance();
    }

    public FundDto create(FundDto data) {
        return Optional.ofNullable(data).map(fundMapper::fundDtoToFund).map(entity -> {
                    entity.setUser(userRepository.getById(data.getUserId()).orElse(null));
                    return fundRepository.add(entity);
                }).map(fundMapper::fundToFundDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public FundDto update(FundDto data) {
        return Optional.ofNullable(data).map(fundMapper::fundDtoToFund).map(entity -> {
                    Fund oldFund = fundRepository.getByTitleAndUserId(entity.getTitle(), data.getUserId())
                            .orElseThrow(() -> new ApplicationException(FUND_NOT_FOUND));
                    entity.setId(oldFund.getId());
                    return fundRepository.update(entity);
                }).map(fundMapper::fundToFundDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public FundDto remove(FundDto data) {
        return Optional.ofNullable(data).map(fundMapper::fundDtoToFund).map(entity -> fundRepository.delete(
                        fundRepository.getByTitleAndUserId(entity.getTitle(), data.getUserId())
                                .orElseThrow(() -> new ApplicationException(FUND_NOT_FOUND))
                )).map(fundMapper::fundToFundDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public FundDto findByTitleAndId(String title, Long id) {
        return Optional.ofNullable(id).flatMap(userRepository::getById).map(User::getId)
                .flatMap(x -> fundRepository.getByTitleAndUserId(title, x))
                .map(fundMapper::fundToFundDto)
                .orElseThrow(() -> new ApplicationException(FUND_NOT_FOUND));
    }

    private Collection<FundDto> findAll() {
        return fundRepository.getAll().stream().map(fundMapper::fundToFundDto).toList(); }

    public Collection<FundDto> findAllByDto (FundDto data) {
        return new Specification<>(data).apply(findAll()).stream().toList();
    }

    public FundDto findById(Long id) {
        return Optional.ofNullable(id).map(fundRepository::getById)
                .filter(Optional::isPresent).map(Optional::get)
                .map(fundMapper::fundToFundDto)
                .orElse(null);
    }
}
