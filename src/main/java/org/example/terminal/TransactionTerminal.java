package org.example.terminal;

import org.example.dto.TransactionDto;
import org.example.entity.TransactionType;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.handler.NotificationInvocationHandler;
import org.example.service.CrudService;
import org.example.service.TransactionService;
import static org.example.preset.FinancialTrackerInit.RETURN;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionTerminal extends AbstractTerminal<TransactionDto> {
    public static final Function<Collection<TransactionDto>, String> VIEW = collection ->
            "\n\t\tДата\t\t\t|\t\tТип\t\t|\t\tКатегория\t\t|\t\tСумма\t\t|\t\tОписание\n" + "-".repeat(130) + "\n" +
                    collection.stream().map(value ->
                            "\t" + value.getDate() + "\t|\t" + value.getType()
                                    + "\t\t|\t\t\t" + value.getCategory() + "\t\t|\t\t" + value.getAmount() + "\t\t\t|\t"
                                    + value.getDescription()).collect(Collectors.joining("\n"));

    @SuppressWarnings("unchecked")
    public TransactionTerminal() {
        commandMenu = System.lineSeparator() + "\tcreate (Создать транзакцию)"
                + System.lineSeparator() + "\tupdate (Редактирование транзакция)"
                + System.lineSeparator() + "\tdelete (Удаление транзакции)"
                + System.lineSeparator() + "\tlist (Список транзакций)"
                + System.lineSeparator() + "\treturn (Возврат в главное меню)";
        service = (CrudService<TransactionDto>) Proxy.newProxyInstance (
                CrudService.class.getClassLoader(),
                new Class<?>[] { CrudService.class },
                new NotificationInvocationHandler<>(new TransactionService()));
        commands = new ConcurrentHashMap<>() {{
            put("create", service::create);
            put("update", service::update);
            put("remove", service::remove);
            put("list", transaction -> print(transaction));
            put("return", transaction -> {});
        }};
    }

    @Override
    protected TransactionDto processCommand(String command) {
        long id = 0L, userId = Optional.ofNullable(getPrincipal()).map(User::getId).orElse(0L);
        Instant date = null;
        TransactionType type = TransactionType.REVERSE;
        String category = "", description = "";
        BigDecimal amount = BigDecimal.ZERO;

        if(command.equals("return"))
            throw new ApplicationException(RETURN);

        System.out.println("===   Введите подробности транзакции   === ");

        try {
            if (command.equals("list")) {
                System.out.print("        Дата (формат - dd.MM.yyyy) [] :> ");
                date = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        .plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            }
        } catch (DateTimeParseException ignore) {}

        if(command.equals("create") || command.equals("list")) {
            System.out.print("        Тип (DEPOSIT | WITHDRAW) [] :> ");
            var value = scanner.nextLine();
            type = value.isEmpty() ? type : TransactionType.valueOf(value);
        }

        try {
            if(command.equals("update") || command.equals("remove")) {
                System.out.print("        Идентификатор :> ");
                id = Long.parseLong(scanner.nextLine());
            }

            if (!command.equals("remove")) {
                System.out.print("        Категория [] :> ");
                category = scanner.nextLine();
                if (!command.equals("list")) {
                    System.out.print("        Сумма :> ");
                    amount = new BigDecimal(scanner.nextLine());
                    System.out.print("        Описание :> ");
                    description = scanner.nextLine();
                }
            }
        } catch (NumberFormatException e) {
            throw new ApplicationException(e.getMessage());
        }

        return command.equals("remove") ? service.findById(id)
                : new TransactionDto(date, type, category, amount, description, userId);
    }

    @Override
    protected void print(TransactionDto data) {
        System.out.println(VIEW.apply (service.findAllByDto (data)));
    }
}
