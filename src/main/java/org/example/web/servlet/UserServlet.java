package org.example.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.dto.TransactionDto;
import org.example.dto.UserDto;
import org.example.entity.RoleType;
import org.example.entity.StatusType;
import org.example.exception.ApplicationException;
import org.example.service.CrudService;
import org.example.service.TransactionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.preset.FinancialTrackerInit.*;

public class UserServlet extends AbstractServlet<UserDto> {
    private static UserServlet INSTANCE;
    private final CrudService<TransactionDto> transactionService;

    private UserServlet() {
        service = userService;
        transactionService = TransactionService.getInstance();
    }

    public static UserServlet getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserServlet();
        }
        return INSTANCE;
    }

    private void logout (HttpServletResponse response, UserDto dto) {
        try (PrintWriter writer = response.getWriter()) {
            Cookie cookie = new Cookie("JSESSIONID", dto.getEmail());
            cookie.setPath("/api/v1");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            writer.println("Сессия пользователя " + dto.getEmail() + " завершена.");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    private int transactions(PrintWriter writer, TransactionDto dto) {
        try {
            writer.println(objectMapper.writeValueAsString (transactionService.findAllByDto(dto)));
            return HttpServletResponse.SC_OK;
        } catch (Exception e) {
            writer.println (e.getMessage());
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    protected void process(HttpServletRequest request, HttpServletResponse response, String endpoint) {}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {

            Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).map(userService::findByEmail)
                    .ifPresentOrElse(principal -> {
                        try {
                            var dto = objectMapper.readValue(reader.lines().collect(Collectors.joining()), UserDto.class);
                            if (path.contains("/api/v1/administration")) {
                                if (principal.getRole().equals(RoleType.ADMIN)) {
                                    String id = request.getParameter("id");
                                    Long userId = id == null ? 0L : Long.parseLong(id);
                                    var transaction = new TransactionDto();
                                    transaction.setUserId(userId);
                                    switch (path.substring(path.lastIndexOf('/'))) {
                                        case "/list" -> response.setStatus(list(writer, dto));
                                        case "/transactions" -> response.setStatus(transactions(writer, transaction));
                                        default -> {
                                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                            writer.println(BAD_ENDPOINT);
                                        }
                                    }
                                } else {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    writer.println("Недостаточно прав доступа.");
                                }
                            } else {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        } catch (JsonProcessingException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            writer.println(BAD_REQUEST);
                        }
                    }, () -> unauthorized.apply(response, writer));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId -> {
                if (path.contains("/api/v1/auth")) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    writer.println("Для использования сервиса аутентификации следует завершить текущую сессию.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writer.println(BAD_ENDPOINT);
                }
            }, () -> {
                try {
                    var dto = objectMapper.readValue(reader.lines().collect(Collectors.joining()), UserDto.class);
                    Optional.ofNullable(dto.getRole()).ifPresentOrElse(x->{}, () -> dto.setRole(RoleType.USER));
                    Optional.ofNullable(dto.getStatus()).ifPresentOrElse(x->{}, () -> dto.setStatus(StatusType.ACTIVE));
                    if (path.contains("/api/v1/auth")) {
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/create" -> response.setStatus(create(writer, dto));
                            default -> {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writer.println(BAD_ENDPOINT);
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writer.println(e.getMessage());
                }
            });
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {

            Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).map(userService::findByEmail)
                    .ifPresentOrElse(principal -> {
                        try {
                            var dto = objectMapper.readValue(reader.lines().collect(Collectors.joining()), UserDto.class);
                            if (path.contains("/api/v1/identity")) {
                                switch (path.substring(path.lastIndexOf('/'))) {
                                    case "/update" -> response.setStatus(update(writer, dto));
                                    default -> {
                                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                        writer.println(BAD_ENDPOINT);
                                    }
                                }
                            } else {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        } catch (JsonProcessingException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            writer.println(BAD_REQUEST);
                        }
                    }, () -> unauthorized.apply(response, writer));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter()) {

            Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).map(userService::findByEmail)
                    .ifPresentOrElse(principal -> {
                        if (path.contains("/api/v1/administration")) {
                            if (principal.getRole().equals(RoleType.ADMIN)) {
                                String id = request.getParameter("id");
                                var dto = userService.findById(id == null ? 0L : Long.parseLong(id));
                                switch (path.substring(path.lastIndexOf('/'))) {
                                    case "/block" -> {
                                        dto.setStatus(StatusType.BLOCKED);
                                        response.setStatus(update(writer, dto));
                                    }
                                    case "/delete" -> response.setStatus(delete(writer, dto));
                                    default -> {
                                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                        writer.println(BAD_ENDPOINT);
                                    }
                                }
                            } else {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                writer.println("Недостаточно прав доступа.");
                            }
                        } else if (path.contains("/api/v1/identity")) {
                            switch (path.substring(path.lastIndexOf('/'))) {
                                case "/delete" -> response.setStatus(delete(writer, principal));
                                case "/logout" -> logout(response, principal);
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
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }
}
