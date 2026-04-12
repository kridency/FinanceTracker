package org.example.terminal;

import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.CrudService;
import org.example.service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.example.preset.FinancialTrackerInit.*;

public abstract class AbstractTerminal<T> {
    protected static final Scanner scanner = new Scanner(System.in);
    protected static User principal;
    protected String commandMenu;
    protected Map<String, Consumer<T>> commands;
    protected CrudService<T> service;
    protected final UserService userService = new UserService();

    protected abstract T processCommand(String command);

    protected abstract void print(T data);

    public void runCommands() {
        var goBack = false;
        while(!goBack) {
            System.out.println(commandMenu);
            System.out.print(COMMAND_PROMPT);
            var command = scanner.nextLine();
            try {
                Optional.ofNullable(commands.get(command))
                        .ifPresentOrElse(consumer -> consumer.accept(processCommand(command)),
                                () -> { throw new ApplicationException(INPUT_ERROR); });
                goBack = command.equals("login") && getPrincipal() != null;
            } catch (ApplicationException e) {
                goBack = e.getMessage().equals(RETURN) || e.getMessage().equals(UNAUTHORIZED);
                System.out.println(e.getMessage());
            }
        }
    }

    public static void setPrincipal(User user) {
        principal = user;
    }

    public static User getPrincipal() {
        return principal;
    }
}
