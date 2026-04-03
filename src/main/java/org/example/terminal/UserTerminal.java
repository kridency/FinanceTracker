package org.example.terminal;

import org.example.dto.UserDto;
import org.example.exception.ApplicationException;
import org.example.mapper.UserMapper;

import static org.example.preset.FinancialTrackerInit.RETURN;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class UserTerminal extends AbstractTerminal<UserDto> {
    private static UserTerminal INSTANCE;
    private final UserMapper userMapper;

    private UserTerminal() {
        commandMenu = System.lineSeparator() + "\tupdate (Редактирование профиля пользователя)"
                + System.lineSeparator() + "\tdelete (Удаление пользователя)"
                + System.lineSeparator() + "\treturn (Возврат в главное меню)";
        userMapper = UserMapper.getInstance();
        commands = new ConcurrentHashMap<>() {{
            put("update", userService::update);
            put("delete", userService::remove);
            put("return", user -> {});
        }};
    }

    public static UserTerminal getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserTerminal();
        }
        return INSTANCE;
    }

    @Override
    protected UserDto processCommand(String command) {
        String name, email, password;

        if (command.equals("return")) {
            throw new ApplicationException(RETURN);
        }

        if (command.equals("delete")) {
            return userMapper.userToUserDto(getPrincipal());
        }

        System.out.println("===   Введите данные пользователя   === ");

        System.out.print("\t\tИмя :> ");
        name = scanner.nextLine();
        System.out.print("\t\tАдрес электронной почты :> ");
        email = scanner.nextLine();
        System.out.print("\t\tПароль :> ");
        password = scanner.nextLine();

        return new UserDto (name, email, password);
    }

    @Override
    protected void print(UserDto user) {}
}
