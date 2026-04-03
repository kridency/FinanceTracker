package org.example.mapper;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.YearMonth;
import javax.annotation.processing.Generated;
import org.example.dto.LimitDto;
import org.example.entity.Limit;
import org.example.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T19:03:05+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Red Hat, Inc.)"
)
@Singleton
@Named
public class LimitMapperImpl implements LimitMapper {

    @Override
    public LimitDto limitToLimitDto(Limit data) {
        if ( data == null ) {
            return null;
        }

        LimitDto limitDto = new LimitDto();

        limitDto.setMonth( data.getMonth() );
        limitDto.setAmount( data.getAmount() );
        limitDto.setUserId( UserMapper.getUserId( data.getUser() ) );

        return limitDto;
    }

    @Override
    public Limit limitDtoToLimit(LimitDto data) {
        if ( data == null ) {
            return null;
        }

        YearMonth month = null;
        BigDecimal amount = null;

        month = data.getMonth();
        amount = data.getAmount();

        User user = null;

        Limit limit = new Limit( month, amount, user );

        return limit;
    }
}
