package org.example.terminal;

import org.example.dto.LimitDto;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.AnalyticsService;
import org.example.service.LimitService;

import static org.example.preset.FinancialTrackerInit.RETURN;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BudgetTerminal extends AbstractTerminal<LimitDto> {

    public BudgetTerminal() {
        commandMenu = System.lineSeparator() + "\tcreate (Установка лимита)"
                + System.lineSeparator() + "\tupdate (Корректировка лимита)"
                + System.lineSeparator() + "\tremove (Удаление лимита)"
                + System.lineSeparator() + "\ttrack (Отслеживание лимита)"
                + System.lineSeparator() + "\treturn (Возврат в главное меню)";
        service = new LimitService();
        commands = new ConcurrentHashMap<>() {{
            put("create", service::create);
            put("update", service::update);
            put("remove", service::remove);
            put("track", limit -> print(limit));
            put("return", limit -> {});
        }};
    }

    @Override
    protected LimitDto processCommand(String command) {
        if (command.equals("return"))
            throw new ApplicationException(RETURN);

        System.out.println("===   Введите подробности лимита финансирования   === ");
        YearMonth month;
        try {
            System.out.print("\t\tМесяц (формат - MM.yyyy) [] :> ");
            month = YearMonth.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("MM.yyyy"));
        } catch (DateTimeParseException e) {
            month = YearMonth.now();
            System.out.println(System.lineSeparator()
                    + "===  Предупреждение  === В качестве месяца установлен текущий");
        }

        BigDecimal amount = BigDecimal.ZERO;
        try {
            if(!command.equals("track") && !command.equals("remove")) {
                System.out.print("\t\tСумма :> ");
                amount = new BigDecimal(scanner.nextLine());
            }
        } catch (NumberFormatException e) {
            throw new ApplicationException(e.getMessage());
        }

        return new LimitDto(month, amount,
                Optional.ofNullable(getPrincipal()).map(User::getId).orElse(null));
    }

    @Override
    protected void print(LimitDto data) {
        var id = Optional.ofNullable(getPrincipal()).map(User::getId).orElse(null);

        var period = Optional.ofNullable(data)
                .map(x -> Map.entry(x.getMonth().atDay(1), x.getMonth().atEndOfMonth()))
                .orElse(null);
        var expenses = AnalyticsService.outcome (id, period);

        var dto = new LimitDto(Optional.ofNullable(data)
                .map(LimitDto::getMonth).orElse(null), null, id);
        var limit = service.findAllByDto (dto).stream()
                .map(LimitDto::getAmount).reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        Optional.ofNullable(data).ifPresent(value ->
                System.out.println(System.lineSeparator() + "Текущее отклонение относительно действующего лимита "
                        + "(\"+\" - не израсходовано, \"-\" - перерасходовано): " + limit.subtract(expenses)));
    }
}
