package org.example.mapper;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javax.annotation.processing.Generated;
import org.example.dto.UserDto;
import org.example.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T19:03:05+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Red Hat, Inc.)"
)
@Singleton
@Named
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto userToUserDto(User data) {
        if ( data == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setUserId( data.getId() );
        userDto.setName( data.getName() );
        userDto.setEmail( data.getEmail() );
        userDto.setPassword( data.getPassword() );
        userDto.setRole( data.getRole() );
        userDto.setStatus( data.getStatus() );

        return userDto;
    }

    @Override
    public User userDtoToUser(UserDto data) {
        if ( data == null ) {
            return null;
        }

        String name = null;
        String email = null;
        String password = null;

        name = data.getName();
        email = data.getEmail();
        password = data.getPassword();

        User user = new User( name, email, password );

        user.setRole( data.getRole() );
        user.setStatus( data.getStatus() );

        return user;
    }
}
