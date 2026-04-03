package org.example.mapper;

import org.example.dto.LimitDto;
import org.example.entity.Limit;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
@Named("LimitMapper")
public interface LimitMapper {
    LimitMapper INSTANCE = Mappers.getMapper(LimitMapper.class);

    @Named("getLimitMapper")
    static LimitMapper getInstance() {
        return INSTANCE;
    }

    @Mappings({
            @Mapping(source = "month", target = "month"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "user", target = "userId", qualifiedByName = "getUserId")
    })
    LimitDto limitToLimitDto(Limit data);

    @Mappings({
            @Mapping(source = "month", target = "month"),
            @Mapping(source = "amount", target = "amount")
    })
    Limit limitDtoToLimit(LimitDto data);
}
