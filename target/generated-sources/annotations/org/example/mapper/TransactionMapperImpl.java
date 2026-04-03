package org.example.mapper;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.example.dto.TransactionDto;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T19:03:04+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Red Hat, Inc.)"
)
@Singleton
@Named
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionDto transactionToTransactionDto(Transaction data) {
        if ( data == null ) {
            return null;
        }

        TransactionDto transactionDto = new TransactionDto();

        transactionDto.setDate( data.getDate() );
        transactionDto.setType( data.getType() );
        transactionDto.setCategory( data.getCategory() );
        transactionDto.setAmount( data.getAmount() );
        transactionDto.setDescription( data.getDescription() );
        transactionDto.setUserId( UserMapper.getUserId( data.getUser() ) );

        return transactionDto;
    }

    @Override
    public Transaction transactionDtoToTransaction(TransactionDto data) {
        if ( data == null ) {
            return null;
        }

        Instant date = null;
        TransactionType type = null;
        String category = null;
        BigDecimal amount = null;
        String description = null;

        date = getTransactionDate( data.getDate() );
        type = data.getType();
        category = data.getCategory();
        amount = data.getAmount();
        description = data.getDescription();

        User user = null;

        Transaction transaction = new Transaction( date, type, category, amount, description, user );

        return transaction;
    }
}
