package org.example.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.ApplicationException;
import org.example.service.AnalyticsService;
import org.example.service.UserService;

import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

import static org.example.preset.FinancialTrackerInit.*;

public class AnalyticsServlet extends HttpServlet {
    private final String PATH;
    private final AnalyticsService service;
    private final UserService userService;

    public AnalyticsServlet() {
        PATH = "/api/v1/analytics";
        service = new AnalyticsService();
        userService = new UserService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter()) {

            Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).map(userService::findByEmail)
                    .ifPresentOrElse(principal -> {
                        try {
                            if (path.contains(PATH)) {
                                String begin = request.getParameter("from");
                                String end = request.getParameter("to");
                                var beginDate = begin == null ? LocalDate.now() : LocalDate.parse(begin, DATE_FORMAT);
                                var endDate = end == null ? LocalDate.now() : LocalDate.parse(end, DATE_FORMAT);
                                var dto = Map.entry(Map.entry(beginDate, endDate), principal.getUserId());
                                switch (path.substring(path.lastIndexOf('/'))) {
                                    case "/balance" -> writer.println(service.balance(dto));
                                    case "/summary" -> writer.println(service.summary(dto));
                                    case "/expenses" -> writer.println(service.expenses(dto));
                                    case "/condition" -> writer.println(service.condition(dto));
                                    default -> {
                                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                        writer.println(BAD_ENDPOINT);
                                    }
                                }
                            } else {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        } catch (DateTimeParseException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            writer.println(BAD_REQUEST);
                        }
                    }, () -> AbstractServlet.unauthorized.apply(response, writer));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }
}
