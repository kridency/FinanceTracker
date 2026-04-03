package org.example.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.dto.FundDto;
import org.example.dto.LimitDto;
import org.example.dto.UserDto;
import org.example.entity.EndPoint;
import org.example.exception.ApplicationException;
import org.example.service.CrudService;
import org.example.service.LimitService;
import org.example.service.UserService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.example.preset.FinancialTrackerInit.*;
import static org.example.preset.FinancialTrackerInit.BAD_REQUEST;

public class LimitServlet extends AbstractServlet<LimitDto> {
    private static LimitServlet INSTANCE;

    private LimitServlet() {
        PATH = "/api/v1/limit";
        service = LimitService.getInstance();
    }

    public static LimitServlet getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new LimitServlet();
        }
        return INSTANCE;
    }

    protected void process(HttpServletRequest request, HttpServletResponse response, String endpoint) {
        String[] tokens = request.getPathInfo().split(PATH);
        if (tokens.length > 1 && tokens[1].equals(endpoint)) {
            try (BufferedReader reader = request.getReader()) {
                var dto = objectMapper.readValue(reader.lines().collect(Collectors.joining()), LimitDto.class);
                execute(request, response, dto);
            } catch (Exception e) {
                try {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                } catch (IOException ex) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    throw new ApplicationException(ex.getMessage());
                }
            }
        }
        else {
            try (PrintWriter writer = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_MISDIRECTED_REQUEST);
                writer.println(BAD_ENDPOINT);
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
    }
}
