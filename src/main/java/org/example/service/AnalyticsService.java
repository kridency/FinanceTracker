package org.example.service;

import org.example.entity.Fund;
import org.example.entity.Limit;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.repository.FundRepository;
import org.example.repository.LimitRepository;
import org.example.repository.TransactionRepository;

import static org.example.preset.FinancialTrackerInit.DATE_FORMAT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.preset.FinancialTrackerInit.NEW_LINE;

public class AnalyticsService {
    private static final TransactionRepository transactionRepository = new TransactionRepository();
    private static final LimitRepository limitRepository = new LimitRepository();
    private static final FundRepository fundRepository = new FundRepository();

    private final String TOTAL_OUTCOME = "\t\tВсего расходов на сумму :";
    private final String TOTAL_INCOME = "\t\tВсего доходов на сумму :";

    private final String[] BALANCE_VIEW = new String[] { "Подсчёт текущего баланса :",
            TOTAL_OUTCOME, TOTAL_INCOME,
            "\t\tБаланс доходов и расходов (\"+\" - прибыль, \"-\" - убыток) :"
    };

    private final String[] CONDITION_VIEW = new String[] { "Текущее финансовое состояние :",
            "\t\tСредств для финансирования расходов (\"+\" - избыток, \"-\" - недостаток) :",
            "\t\tНакоплено средств во внебюджетных фондах :"
    };

    private static final BiPredicate<Transaction, Map.Entry<LocalDate, LocalDate>> withinPeriod = (value, borders) ->
            value.getDate().isAfter(borders.getKey().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) &&
                    value.getDate().isBefore(borders.getValue().plusDays(1).atStartOfDay()
                            .atZone(ZoneId.systemDefault()).toInstant());

    private static BigDecimal total (Long userId,
                             Map.Entry<LocalDate, LocalDate> period,
                             Predicate<Transaction> condition) {
        return transactionRepository.getAllByUserId (userId).stream().filter(condition)
                .filter(value -> Optional.ofNullable(period)
                        .map(x -> withinPeriod.test(value, x)).orElse(true))
                .map(Transaction::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public static BigDecimal outcome (Long id, Map.Entry<LocalDate, LocalDate> period) {
        return total (id, period, x -> x.getType().equals(TransactionType.WITHDRAW));
    }

    public static BigDecimal income (Long id, Map.Entry<LocalDate, LocalDate> period) {
        return total (id, period, x -> x.getType().equals(TransactionType.DEPOSIT));
    }

    public static BigDecimal unspent (Long id, YearMonth month) {
        return Optional.ofNullable(id)
                .flatMap(value -> limitRepository.getByMonthAndUserId (month, value))
                .map(Limit::getAmount).orElse(BigDecimal.ZERO)
                .subtract(outcome(id, Map.entry(month.atDay(1), month.atEndOfMonth())));
    }

    public static BigDecimal savings (Long id) {
        return Optional.ofNullable(id).map(fundRepository::getAllByUserId).stream().flatMap(Collection::stream)
                .map(Fund::getSavings).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private static final BiFunction<String[], String[], String> VIEW = (decoding, values) ->
        decoding[0] + NEW_LINE + IntStream.range (1, decoding.length)
                .mapToObj (i -> Map.entry (i, decoding[i]))
                .map (entry -> decoding[entry.getKey()]
                        + (entry.getKey () - 1 < values.length ? values[entry.getKey () - 1] : ""))
                .collect (Collectors.joining (NEW_LINE));

    public String balance (Map.Entry<Map.Entry<LocalDate, LocalDate>, Long> data) {
        var id = Optional.ofNullable(data).map(Map.Entry::getValue).orElse(null);

        var expenses = outcome (id, null);
        var revenue = income (id, null);
        var profit = revenue.subtract (expenses);

        return VIEW.apply (BALANCE_VIEW, new String[] { " " + expenses, " " + revenue, " " + profit });
    }

    public String summary (Map.Entry<Map.Entry<LocalDate, LocalDate>, Long> data) {
        var id = Optional.ofNullable(data).map(Map.Entry::getValue).orElse(null);
        var period = Optional.ofNullable(data).map(Map.Entry::getKey).orElse(null);

        var expenses = outcome (id, period);
        var revenue = income (id, period);

        var SUMMARY_VIEW = new String[] { "За период " + Optional.ofNullable(period).map(entry ->
                "с " + entry.getKey().format(DATE_FORMAT) + " по " + entry.getValue().format(DATE_FORMAT)).orElse("")
                + " суммарно :", TOTAL_OUTCOME, TOTAL_INCOME
        };

        return VIEW.apply (SUMMARY_VIEW, new String[] { " " + expenses, " " + revenue });
    }

    public String expenses (Map.Entry<Map.Entry<LocalDate, LocalDate>, Long> data) {
        var categories = Optional.ofNullable(data).map(Map.Entry::getValue).map(id ->
                transactionRepository.getAllByUserId (id).stream()
                .filter(value -> value.getType().equals(TransactionType.WITHDRAW))
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)))
                .entrySet().stream()
                .map(entry ->  "\t\t" + entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining(NEW_LINE))).orElse("");

        var EXPENSES_VIEW = new String[] { "Перечень расходов в разрезе категорий :", categories };

        return VIEW.apply (EXPENSES_VIEW, new String[]{});
    }

    public String condition (Map.Entry<Map.Entry<LocalDate, LocalDate>, Long> data) {
        var id = Optional.ofNullable(data).map(Map.Entry::getValue).orElse(null);

        var residue = unspent (id, YearMonth.now());
        var funds = savings (id);

        return VIEW.apply (CONDITION_VIEW, new String[] { " " + residue, " " + funds });
    }
}
