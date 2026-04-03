package org.example.handler;

import org.example.dto.AbstractDto;
import org.example.dto.TransactionDto;
import org.example.entity.TransactionType;
import org.example.exception.ApplicationException;
import org.example.service.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Optional;

public class NotificationInvocationHandler<T extends AbstractDto> implements InvocationHandler {
    private final CrudService<T> service;
    private final NotificationService notificationService;

    public NotificationInvocationHandler(final CrudService<T> service) {
        this.service = service;
        notificationService = NotificationService.getInstance();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args[0] instanceof TransactionDto dto) {
            if (Optional.ofNullable(dto.getType()).map(value ->
                    value.equals(TransactionType.WITHDRAW)).orElse(false)) {
                var id = dto.getUserId();
                var date = dto.getDate().atZone(ZoneOffset.UTC).toLocalDate();
                var residue = AnalyticsService.unspent (id, YearMonth.from(date));
                var withdrawal = Optional.ofNullable(dto.getAmount()).orElse(BigDecimal.ZERO);

                if (residue.subtract(withdrawal).compareTo(BigDecimal.ZERO) < 0) {
                    try {
                        notificationService.notify(dto.getUserId());
                    } catch (ApplicationException e) {
                        throw new ApplicationException(e.getMessage());
                    }
                }
            }
        }

        try {
            return method.invoke(service, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
