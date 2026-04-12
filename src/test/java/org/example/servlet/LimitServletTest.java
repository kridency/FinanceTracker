package org.example.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.AbstractTest;
import org.example.dto.LimitDto;
import org.example.service.LimitService;
import org.example.web.listener.RequestStream;
import org.example.web.listener.RequestWrapper;
import org.example.web.listener.ResponseWrapper;
import org.example.web.servlet.LimitServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.YearMonth;

import static org.example.preset.FinancialTrackerInit.objectMapper;

public class LimitServletTest extends AbstractTest {
    private static final LimitServlet limitServlet = Mockito.spy(new LimitServlet());
    private static final LimitService limitService = Mockito.spy(new LimitService());

    @Test
    @DisplayName("Печать лимитов отфильтрованных по шаблону")
    public void givenCurrentUserAndLimit_whenTryToList_thenReturnBadRequest() throws IOException {
        var limit = new LimitDto();
        String limitList = objectMapper.writeValueAsString(limitService.findAllByDto(limit));
        String limitString = objectMapper.writeValueAsString(limit);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/limit/list");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(limitString.getBytes()))));

        limitServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(limitList);
    }

    @Test
    @DisplayName("Попытка создать лимит")
    void givenUserAndLimitDto_whenTryToCreate_thenReturnCorrectResult() throws IOException {
        var newLimit = new LimitDto();
        newLimit.setMonth(YearMonth.now());
        newLimit.setAmount(BigDecimal.valueOf(455.00));
        newLimit.setUserId(userService.loadUserByUsername("name@hostname").getId());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/limit/create");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(newLimit).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        limitServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + newLimit.getMonth() + " успешно создана.");
    }

    @Test
    @DisplayName("Попытка изменить лимит")
    void givenUserAndLimit_whenTryToUpdate_thenReturnCorrectResult() throws IOException {
        LimitDto newLimit = new LimitDto();
        newLimit.setMonth(YearMonth.parse("2024-12"));
        newLimit.setUserId(userService.loadUserByUsername("name@hostname").getId());
        newLimit = limitService.findAllByDto(newLimit).stream().findFirst().orElse(newLimit);

        newLimit.setAmount(new BigDecimal("800"));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/limit/update");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(newLimit).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        limitServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + newLimit.getMonth() + " успешно изменена.");
    }

    @Test
    @DisplayName("Попытка удалить лимит")
    void givenUserAndLimit_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        LimitDto limit = new LimitDto();
        limit.setMonth(YearMonth.parse("2024-11"));
        limit.setUserId(userService.loadUserByUsername("name@hostname").getId());
        limit = limitService.findAllByDto(limit).stream().findFirst().orElse(limit);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/limit/delete");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(limit).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        limitServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Запись " + limit.getMonth() + " успешно удалена.");
    }
}
