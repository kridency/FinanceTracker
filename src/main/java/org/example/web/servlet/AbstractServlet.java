package org.example.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.AbstractDto;
import org.example.entity.EndPoint;
import org.example.exception.ApplicationException;
import org.example.service.CrudService;
import org.example.service.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.example.preset.FinancialTrackerInit.*;
import static org.example.preset.FinancialTrackerInit.BAD_ENDPOINT;

public abstract class AbstractServlet<T extends AbstractDto> extends HttpServlet {
    protected String PATH;
    protected CrudService<T> service;
    protected final UserService userService = UserService.getInstance();

    protected static final BiFunction<HttpServletResponse, PrintWriter, Void> unauthorized = (response, writer) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        writer.println(UNAUTHORIZED);
        return null;
    };

    protected int create(PrintWriter writer, T dto) {
        if (service.create(dto) != null) {
            writer.println("Запись " + dto.name() + " успешно создана.");
            return HttpServletResponse.SC_CREATED;
        } else {
            writer.println("Не удалось создать запись.");
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    protected int list(PrintWriter writer, T dto) {
        try {
            writer.println(objectMapper.writeValueAsString(service.findAllByDto(dto)));
            return HttpServletResponse.SC_OK;
        } catch (JsonProcessingException e) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }
    }

    protected int update(PrintWriter writer, T dto) {
        if (service.update(dto) != null) {
            writer.println("Запись " + dto.name() + " успешно изменена.");
            return HttpServletResponse.SC_CREATED;
        } else {
            writer.println("Не удалось изменить запись.");
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    protected int delete(PrintWriter writer, T dto) {
        if (service.remove(dto) != null) {
            writer.println("Запись " + dto.name() + " успешно удалена.");
            return HttpServletResponse.SC_OK;
        } else {
            writer.println("Не удалось удалить запись.");
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    protected abstract void process(HttpServletRequest request, HttpServletResponse response, String endpoint);

    protected void execute(HttpServletRequest request, HttpServletResponse response, T dto) {
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            Optional.ofNullable(request.getUserPrincipal())
                    .map(Principal::getName)
                    .map(userService::findByEmail)
                    .ifPresentOrElse(principal -> {
                        String[] tokens = request.getPathInfo().split(PATH);
                        if (tokens.length > 0) {
                            Optional.ofNullable(userService.loadUserByUsername(principal.getEmail()))
                                    .ifPresent(value -> dto.setUserId(value.getId()));
                            switch (Objects.requireNonNull(EndPoint.fromString(tokens[1]))) {
                                case CREATE -> response.setStatus(create(writer, dto));
                                case UPDATE -> response.setStatus(update(writer, dto));
                                case DELETE -> response.setStatus(delete(writer, dto));
                                case LIST -> response.setStatus(list(writer, dto));
                                default -> {
                                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                    writer.println(BAD_ENDPOINT);
                                }
                            }
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            writer.println(BAD_ENDPOINT);
                        }
                    }, () -> unauthorized.apply(response, writer));
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/list");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/create");
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/update");
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/delete");
    }
}
