package org.example;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.entity.RoleType;
import org.example.exception.ApplicationException;
import org.example.terminal.*;
import org.example.web.listener.HttpListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.preset.FinancialTrackerInit.*;

public class FinanceTrackerApplication {
    private static final String COMMAND_MENU;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, ? extends AbstractTerminal<?>> terminals;

    static {
        terminals = new HashMap<>() {{
            put("authentication", new AuthTerminal());
            put("identity", new UserTerminal());
            put("finance", new TransactionTerminal());
            put("budget", new BudgetTerminal());
            put("goal", new GoalTerminal());
            put("analytics", new AnalyticsTerminal());
            put("administration", new AdminTerminal());
        }};

        COMMAND_MENU = System.lineSeparator() + "\tidentity (Управление пользователями)"
                + System.lineSeparator() + "\tfinance (Управление финансами)"
                + System.lineSeparator() + "\tbudget (Управление лимитом расходов на текущий месяц)"
                + System.lineSeparator() + "\tgoal (Управление финансовыми поступлениями)"
                + System.lineSeparator() + "\tanalytics (Статистика и аналитика)"
                + System.lineSeparator() + "\tlogout (Выход из системы)";
    }

    private static final Logger LOGGER = Logger.getLogger(FinanceTrackerApplication.class.getName());
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        HttpListener.getInstance().startHTTPServer(8080);
        objectMapper.registerModule(new JavaTimeModule());

        try {
            while(true) {
                Optional.ofNullable(AbstractTerminal.getPrincipal()).ifPresentOrElse(user -> {
                    var adminMenu = user.getRole().equals(RoleType.ADMIN) ? System.lineSeparator()
                            + "\tadministration (Администрирование)" : "";

                    System.out.println(adminMenu + COMMAND_MENU);
                    System.out.print(COMMAND_PROMPT);
                    var command = SCANNER.nextLine();

                    if(adminMenu.isEmpty() && command.equals("administration")) {
                        throw new ApplicationException(INPUT_ERROR);
                    }

                    try {
                        Optional.ofNullable(terminals.get(command))
                                .ifPresentOrElse(AbstractTerminal::runCommands,
                                        () -> {
                                            if (command.equals("logout")) {
                                                AbstractTerminal.setPrincipal(null);
                                            } else {
                                                throw new ApplicationException(INPUT_ERROR);
                                            }
                                        });
                    } catch(ApplicationException e) {
                        System.out.println(e.getMessage());
                    }
                }, () -> terminals.get("authentication").runCommands());
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.INFO, e.getMessage());
            HttpListener.getInstance().stop();
            System.exit(0);
        }
    }
}