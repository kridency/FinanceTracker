package org.example.terminal;

import org.example.dto.TransactionDto;
import org.example.dto.UserDto;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.AnalyticsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.preset.FinancialTrackerInit.DATE_FORMAT;
import static org.example.preset.FinancialTrackerInit.RETURN;

public class AnalyticsTerminal extends AbstractTerminal<Map.Entry<Map.Entry<LocalDate, LocalDate>, Long>> {
    private static AnalyticsService analyticsService;

    public AnalyticsTerminal() {
        commandMenu = System.lineSeparator() + "\tbalance (Подсчёт текущего баланса)"
                + System.lineSeparator() + "\tsummary (Расчёт суммарного дохода и расхода за определённый период)"
                + System.lineSeparator() + "\texpenses (Анализ расходов по категориям)"
                + System.lineSeparator() + "\tcondition (Формирование отчёта для пользователя по финансовому состоянию)"
                + System.lineSeparator() + "\treturn (Возврат в главное меню)";
        analyticsService = new AnalyticsService();
        commands = new ConcurrentHashMap<>() {{
            put("balance", data -> System.out.println(analyticsService.balance(data)));
            put("summary", data -> System.out.println(analyticsService.summary(data)));
            put("expenses", data -> System.out.println(analyticsService.expenses(data)));
            put("condition", data -> System.out.println(analyticsService.condition(data)));
            put("return", user -> {});
        }};
    }

    @Override
    protected Map.Entry<Map.Entry<LocalDate, LocalDate>, Long> processCommand(String command) {
        var start = LocalDateTime.now().toLocalDate();
        var end = LocalDateTime.now().toLocalDate();

        if (command.equals("return"))
            throw new ApplicationException(RETURN);

        try {
            if (command.equals ("summary")) {
                System.out.println("===   Введите подробности периода   === ");
                System.out.print("        Начальная дата периода (формат - dd.MM.yyyy) :> ");
                start = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
                System.out.print("        Конечная дата периода (формат - dd.MM.yyyy) :> ");
                end = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            } else if (command.equals ("condition")) {
                start = YearMonth.now().atDay(1);
                end = YearMonth.now().atEndOfMonth();
            }
        } catch (DateTimeParseException e) {
            throw new ApplicationException(e.getMessage());
        }

        return Map.entry(Map.entry(start, end), Objects.requireNonNull(getPrincipal()).getId());
    }

    @Override
    protected void print(Map.Entry<Map.Entry<LocalDate, LocalDate>, Long> data) {}
}
