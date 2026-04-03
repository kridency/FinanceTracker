package org.example.terminal;

import org.example.dto.UserDto;
import org.example.entity.StatusType;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.mapper.UserMapper;

import static org.example.preset.FinancialTrackerInit.FORCED_COMPLETION;
import static org.example.preset.FinancialTrackerInit.UNAUTHORIZED;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AuthTerminal extends AbstractTerminal<UserDto> {
    private static AuthTerminal INSTANCE;
    private final UserMapper userMapper;

    private AuthTerminal() {
        commandMenu = System.lineSeparator() + "\tregister (Регистрация пользователя)"
                + System.lineSeparator() + "\tlogin (Вход в систему)"
                + System.lineSeparator() + "\texit (Завершение сеанса)";
        userMapper = UserMapper.getInstance();
        commands = new ConcurrentHashMap<>() {{
            put("register", userService::create);
            put("login", user -> Optional.ofNullable(user).map(value -> userService.findByEmail(value.getEmail()))
                        .filter(value -> !value.getStatus().equals(StatusType.BLOCKED))
                        .filter(value -> value.getPassword().equals(user.getPassword()))
                        .orElseThrow(() -> new ApplicationException(UNAUTHORIZED)));
            put("exit", user -> { throw new RuntimeException(FORCED_COMPLETION); });
        }};
    }

    public static AuthTerminal getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AuthTerminal();
        }
        return INSTANCE;
    }

    @Override
    protected UserDto processCommand(String command) {
        UserDto userDto;
        String name, email, password;

        if (command.equals("exit"))
            return null;

        System.out.println("===   Введите данные пользователя   === ");

        if(command.equals("register")) {
            System.out.print("\t\tИмя :> ");
            name = scanner.nextLine();
        } else {
            name = "";
        }

        System.out.print("\t\tАдрес электронной почты :> ");
        email = scanner.nextLine();
        System.out.print("\t\tПароль :> ");
        password = scanner.nextLine();

        try {
            userDto = Optional.ofNullable(userService.findByEmail(email))
                    .filter(value -> !value.getStatus().equals(StatusType.BLOCKED))
                    .filter(value -> value.getPassword().equals(password))
                    .orElseThrow();
            setPrincipal(userService.loadUserByUsername(email));
        } catch (Exception e) {
            userDto = new UserDto(name, email, password);
        }

        return userDto;
    }

    @Override
    protected void print(UserDto data) {}
}
