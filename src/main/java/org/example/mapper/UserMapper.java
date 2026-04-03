package org.example.mapper;

import org.example.dto.UserDto;
import org.example.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Named("UserMapper")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Named("getUserMapper")
    static UserMapper getInstance() {
        return INSTANCE;
    }

    @Named("getUserId")
    static Long getUserId(User user) {
        return Optional.ofNullable(user).map(User::getId).orElse(null);
    }

    @Mappings({
            @Mapping(source = "id", target = "userId"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role"),
            @Mapping(source = "status", target = "status")
    })
    UserDto userToUserDto(User data);

    @Mappings({
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role"),
            @Mapping(source = "status", target = "status")
    })
    User userDtoToUser(UserDto data);
}
