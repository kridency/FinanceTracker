package org.example.web.servlet;
import org.example.dto.UserDto;
import org.example.entity.EndPoint;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.NotificationService;
import org.example.service.UserService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.example.preset.FinancialTrackerInit.*;

public class NotificationServlet extends HttpServlet {
    private final String PATH;
    private static NotificationServlet INSTANCE;
    private final UserService userService;
    private final NotificationService notificationService;

    private NotificationServlet() {
        PATH = "/api/v1/notification";
        userService = new UserService();
        notificationService = new NotificationService();
    }

    public static NotificationServlet getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new NotificationServlet();
        }
        return INSTANCE;
    }

    private void toggle(HttpServletResponse response, User user) {
        try (PrintWriter writer = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            writer.println(notificationService.activate(user.getId()));
            writer.flush();
        } catch (Exception e) {
            if (!e.getMessage().equals(RETURN)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        try (PrintWriter writer = response.getWriter()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

                Optional.ofNullable(request.getUserPrincipal())
                        .map(Principal::getName)
                        .map(userService::loadUserByUsername).ifPresentOrElse(principal -> {
                            String[] tokens = request.getPathInfo().split(PATH);
                            if (tokens[1].equals("/toggle")) {
                                toggle(response, principal);
                            } else {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                }, unauthorized::get);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }
}
