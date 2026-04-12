package org.example.terminal;

import org.example.dto.FundDto;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.FundService;

import static org.example.preset.FinancialTrackerInit.RETURN;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GoalTerminal extends AbstractTerminal<FundDto> {

    public GoalTerminal() {
        commandMenu = System.lineSeparator() + "\tproclaim (Установить фонд накоплений)"
                + System.lineSeparator() + "\ttransfer (Перечисление в фонд накоплений)"
                + System.lineSeparator() + "\ttrack (Отслеживание состояния фонда накоплений)"
                + System.lineSeparator() + "\treturn (Возврат в главное меню)";
        service = new FundService();
        commands = new ConcurrentHashMap<>() {{
            put("proclaim", service::create);
            put("transfer", service::update);
            put("remove", service::remove);
            put("track", fund -> print(fund));
            put("return", fund -> {});
        }};
    }

    @Override
    protected FundDto processCommand(String command) {
        FundDto fund;
        BigDecimal target = BigDecimal.ZERO;

        if (command.equals("return"))
            throw new ApplicationException(RETURN);

        System.out.println("===   Введите подробности фонда накоплений   === ");
        try {
            System.out.print("        Наименование фонда :> ");
            fund = new FundDto(scanner.nextLine(), target, getPrincipal().getId());
            if (fund.getTitle().isBlank()) {
                throw new ApplicationException("Недопустимое наименование фонда");
            }
            if (command.equals("proclaim")) {
                System.out.print("        Величина фонда :> ");
                fund.setTarget(new BigDecimal(scanner.nextLine()));
            } else if (command.equals("transfer")) {
                System.out.print("        Размер перечисления в фонд :> ");
                fund.setSavings(new BigDecimal(scanner.nextLine()));
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }

        return fund;
    }

    @Override
    protected void print(FundDto data) {
        var id = Optional.ofNullable(getPrincipal()).map(User::getId).orElse(null);
        var title = Optional.ofNullable(data).map(FundDto::getTitle).orElse(null);
        var dto = new FundDto(title,null, id);
        var fund = service.findAllByDto (dto).stream().findAny().orElse(dto);

        System.out.println(System.lineSeparator() + "Текущее состояние накоплений для цели "
                + "\"" + fund.getTitle() + "\"" + " : " + fund.getSavings() + System.lineSeparator()
                + "Для достижения цели \"" + fund.getTitle() + "\" требуется перечислить : "
                + (fund.getTarget().subtract(fund.getSavings())));
    }
}
