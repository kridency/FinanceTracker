package org.example.preset;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.TimeZone;

public class FinancialTrackerInit {
    public static String UNAUTHORIZED;
    public static String RETURN;
    public static String INPUT_ERROR;
    public static String EMAIL_ERROR;
    public static String DATE_ERROR;
    public static String TITLE_ERROR;
    public static String COMMAND_PROMPT;
    public static String FORCED_COMPLETION;
    public static String USER_NOT_FOUND;
    public static String TRANSACTION_NOT_FOUND;
    public static String LIMIT_NOT_FOUND;
    public static String FUND_NOT_FOUND;
    public static String INVOCATION_NOT_FOUND;
    public static DateTimeFormatter MONTH_FORMAT;
    public static DateTimeFormatter DATE_FORMAT;
    public static Calendar UTC_CALENDAR;
    public static String BAD_REQUEST;
    public static String BAD_ENDPOINT;
    public static String CREATED;
    public static String UPDATED;
    public static String DELETED;
    public static String OBJECT_NOT_CREATED;
    public static String OBJECT_NOT_UPDATED;
    public static ObjectMapper objectMapper;

    public static String NEW_LINE;

    static {
        UNAUTHORIZED = "Необходимо пройти аутентификацию";
        RETURN = "return";
        INPUT_ERROR = "Неопознанная команда";
        EMAIL_ERROR = "Неправильный формат email";
        DATE_ERROR = "Неправильный формат даты";
        TITLE_ERROR = "Не указана цель";
        COMMAND_PROMPT = System.lineSeparator() + "Введите команду :> ";
        FORCED_COMPLETION =  "Принудительное завершение" + System.lineSeparator();
        USER_NOT_FOUND = "Пользователь не найден";
        TRANSACTION_NOT_FOUND = "Транзакция не найдена";
        LIMIT_NOT_FOUND = "Лимит не найден";
        FUND_NOT_FOUND = "Фонд накоплений не найден";
        INVOCATION_NOT_FOUND = "Вызов не найден";
        DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
        UTC_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        BAD_ENDPOINT = "Недопустимая операция";
        BAD_REQUEST = "Входящие данные не соответствуют формату";
        CREATED = "Объект успешно создан";
        UPDATED = "Объект успешно изменен";
        DELETED = "Объект успешно удален";
        OBJECT_NOT_CREATED = "Объект не создан";
        OBJECT_NOT_UPDATED = "Объект не обновлен";
        NEW_LINE = System.lineSeparator();

        objectMapper = new ObjectMapper();
    }
}
