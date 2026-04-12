package org.example.service;

import org.example.dto.TransactionDto;
import org.example.entity.Transaction;
import org.example.exception.ApplicationException;
import org.example.mapper.TransactionMapper;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.util.Specification;

import java.util.Collection;
import java.util.Optional;

import static org.example.preset.FinancialTrackerInit.BAD_REQUEST;
import static org.example.preset.FinancialTrackerInit.TRANSACTION_NOT_FOUND;

public class TransactionService implements CrudService<TransactionDto> {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

    public TransactionService() {
        transactionMapper = TransactionMapper.getInstance();
        transactionRepository = new TransactionRepository();
        userRepository = new UserRepository();
    }

    public TransactionDto create(TransactionDto data) {
        return Optional.ofNullable(data).map(transactionMapper::transactionDtoToTransaction).map(entity -> {
                    entity.setUser(userRepository.getById(data.getUserId()).orElse(null));
                    return transactionRepository.add(entity);
                }).map(transactionMapper::transactionToTransactionDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public TransactionDto update(TransactionDto data) {
        return Optional.ofNullable(data).map(transactionMapper::transactionDtoToTransaction)
                .map(entity -> {
                    Transaction oldTransaction = transactionRepository.getByDateAndUserId(entity.getDate(), data.getUserId())
                            .orElseThrow(() -> new ApplicationException(TRANSACTION_NOT_FOUND));
                    entity.setId(oldTransaction.getId());
                    return transactionRepository.update(entity);
                }).map(transactionMapper::transactionToTransactionDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    public TransactionDto remove(TransactionDto data) {
        return Optional.ofNullable(data).map(transactionMapper::transactionDtoToTransaction)
                .map(entity -> transactionRepository.delete(
                        transactionRepository.getByDateAndUserId(entity.getDate(), data.getUserId())
                                .orElseThrow(() -> new ApplicationException(TRANSACTION_NOT_FOUND))
                )).map(transactionMapper::transactionToTransactionDto)
                .orElseThrow(() -> new ApplicationException(BAD_REQUEST));
    }

    private Collection<TransactionDto> findAll() {
        return transactionRepository.getAll().stream().map(transactionMapper::transactionToTransactionDto).toList(); }

    public Collection<TransactionDto> findAllByDto(TransactionDto data) {
        return new Specification<>(data).apply(findAll()).stream().toList();
    }

    public TransactionDto findById(Long id) {
        return Optional.ofNullable(id).map(transactionRepository::getById)
                .filter(Optional::isPresent).map(Optional::get)
                .map(transactionMapper::transactionToTransactionDto)
                .orElse(null);
    }
}
