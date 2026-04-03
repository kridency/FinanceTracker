package org.example.mapper;

import org.example.dto.TransactionDto;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.TransactionService;
import org.example.service.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Optional;

import static org.example.preset.FinancialTrackerInit.INPUT_ERROR;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class}
)
@Named("TransactionMapper")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Named("getTransactionMapper")
    static TransactionMapper getInstance() {
        return INSTANCE;
    }

    @Named("getTransactionDate")
    default Instant getTransactionDate(Instant date) {
        return Optional.ofNullable(date).orElse(Instant.now());
    }

    @Mappings({
            @Mapping(source = "date", target = "date"),
            @Mapping(source = "type", target = "type"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "user", target = "userId", qualifiedByName = "getUserId")
    })
    TransactionDto transactionToTransactionDto(Transaction data);

    @Mappings({
            @Mapping(source = "date", target = "date", qualifiedByName = "getTransactionDate"),
            @Mapping(source = "type", target = "type"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "description", target = "description")
    })
    Transaction transactionDtoToTransaction(TransactionDto data);
}
