package org.example.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.annotation.WebServlet;
import org.example.dto.FundDto;
import org.example.dto.LimitDto;
import org.example.dto.TransactionDto;
import org.example.entity.EndPoint;
import org.example.exception.ApplicationException;
import org.example.handler.NotificationInvocationHandler;
import org.example.service.CrudService;
import org.example.service.TransactionService;
import org.example.service.UserService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.preset.FinancialTrackerInit.*;
import static org.example.preset.FinancialTrackerInit.BAD_ENDPOINT;

@WebServlet(urlPatterns = {"/api/v1/transaction/create",
        "/api/v1/transaction/update",
        "/api/v1/transaction/delete",
        "/api/v1/transaction/list"})
public class TransactionServlet extends AbstractServlet<TransactionDto> {
    private static TransactionServlet INSTANCE;

    @SuppressWarnings("unchecked")
    private TransactionServlet() {
        PATH = "/api/v1/transaction";
        service = (CrudService<TransactionDto>) Proxy.newProxyInstance (
                CrudService.class.getClassLoader(),
                new Class<?>[] { CrudService.class },
                new NotificationInvocationHandler<>(new TransactionService()));
    }

    public static TransactionServlet getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TransactionServlet();
        }
        return INSTANCE;
    }

    protected int create(PrintWriter writer, TransactionDto transaction) {
            try {
                if (service.create(transaction) != null) {
                    writer.println("Запись " + transaction.getDescription() + " успешно создана.");
                    return HttpServletResponse.SC_CREATED;
                } else {
                    writer.println("Не удалось создать транзакцию.");
                    return HttpServletResponse.SC_BAD_REQUEST;
                }
            } catch (ApplicationException e) {
                writer.println(e.getMessage());
                return HttpServletResponse.SC_BAD_REQUEST;
            }
    }

    protected void process(HttpServletRequest request, HttpServletResponse response, String endpoint) {
        String[] tokens = request.getPathInfo().split(PATH);
        try {
            if (tokens.length > 1 && tokens[1].equals(endpoint)) {
                try (BufferedReader reader = request.getReader()) {
                    var dto = objectMapper.readValue(reader.lines().collect(Collectors.joining()), TransactionDto.class);
                    execute(request, response, dto);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                }
            } else {
                try (PrintWriter writer = response.getWriter()) {
                    response.setStatus(HttpServletResponse.SC_MISDIRECTED_REQUEST);
                    writer.println(BAD_ENDPOINT);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                }
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }
}

