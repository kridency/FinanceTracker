package org.example.mapper;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.example.dto.FundDto;
import org.example.entity.Fund;
import org.example.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T19:03:05+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Red Hat, Inc.)"
)
@Singleton
@Named
public class FundMapperImpl implements FundMapper {

    @Override
    public FundDto fundToFundDto(Fund data) {
        if ( data == null ) {
            return null;
        }

        FundDto fundDto = new FundDto();

        fundDto.setTitle( data.getTitle() );
        fundDto.setTarget( data.getTarget() );
        fundDto.setSavings( data.getSavings() );
        fundDto.setUserId( UserMapper.getUserId( data.getUser() ) );

        return fundDto;
    }

    @Override
    public Fund fundDtoToFund(FundDto data) {
        if ( data == null ) {
            return null;
        }

        String title = null;
        BigDecimal target = null;
        BigDecimal savings = null;

        title = data.getTitle();
        target = data.getTarget();
        savings = data.getSavings();

        User user = null;

        Fund fund = new Fund( title, target, savings, user );

        return fund;
    }
}
