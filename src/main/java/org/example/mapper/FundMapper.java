package org.example.mapper;

import org.example.dto.FundDto;
import org.example.entity.Fund;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.FundService;
import org.example.service.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
@Named("FundMapper")
public interface FundMapper {
    FundMapper INSTANCE = Mappers.getMapper(FundMapper.class);

    @Named("getFundMapper")
    static FundMapper getInstance() {
        return INSTANCE;
    }

    @Mappings({
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "target", target = "target"),
            @Mapping(source = "savings", target = "savings"),
            @Mapping(source = "user", target = "userId", qualifiedByName = "getUserId")
    })
    FundDto fundToFundDto(Fund data);

    @Mappings({
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "target", target = "target"),
            @Mapping(source = "savings", target = "savings"),
    })
    Fund fundDtoToFund(FundDto data);
}
