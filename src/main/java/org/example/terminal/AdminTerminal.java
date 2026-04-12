package org.example.terminal;

import org.example.dto.TransactionDto;
import org.example.dto.UserDto;
import org.example.entity.StatusType;
import org.example.exception.ApplicationException;
import org.example.service.TransactionService;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.preset.FinancialTrackerInit.RETURN;

public class AdminTerminal extends AbstractTerminal<UserDto> {
    private final TransactionService transactionService;

    public static final Function<Collection<UserDto>, String> VIEW = collection ->
            "\n\tID\t|\t\tИмя\t\t|\t\tАдрес\t\t|\t\tПароль\t\t|\t\tРоль\t\t|\t\tСтатус\n"
                    + "-".repeat(110) + "\n" + collection.stream().map(value ->
                            "\t" + value.getUserId() + "\t|\t" + value.getName()
                                    + "\t\t|\t" + value.getEmail() + "\t|\t\t" + value.getPassword() + "\t\t|\t"
                                    + value.getRole() + "\t\t\t|\t" + value.getStatus()).collect(Collectors.joining("\n"));

    public AdminTerminal() {
        commandMenu = System.lineSeparator() + "\tusers (Список пользователей)"
                + System.lineSeparator() + "\ttransactions (Список транзакций пользователя)"
                + System.lineSeparator() + "\tblock (Блокировка пользователя)"
                + System.lineSeparator() + "\tdelete (Удаление пользователя)"
                + System.lineSeparator() + "\treturn (Возврат в главное меню)";
        transactionService = new TransactionService();
        commands = new ConcurrentHashMap<>() {{
            put("users", user -> print(user));
            put("transactions", user -> {
                var dto = new TransactionDto(null, null, null, null, null, user.getUserId());
                System.out.println(TransactionTerminal.VIEW.apply (transactionService.findAllByDto (dto)));
            });
            put("block", user -> {
                user.setStatus(StatusType.BLOCKED);
                userService.update(user);
            });
            put("remove", userService::remove);
            put("return", user -> {});
        }};
    }

    @Override
    protected UserDto processCommand(String command) {
        String email;

        if (command.equals("return"))
            throw new ApplicationException(RETURN);

        if (command.equals("users")) return new UserDto();

        System.out.println("===   Введите данные пользователя   === ");
        System.out.print("\t\tАдрес электронной почты :> ");
        email = scanner.nextLine();

        return userService.findByEmail(email);
    }

    @Override
    public void print(UserDto data) { System.out.println(VIEW.apply (userService.findAllByDto (data))); }
}
