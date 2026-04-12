package org.example.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.AnalyticsService;
import org.example.web.listener.RequestWrapper;
import org.example.web.listener.ResponseWrapper;
import org.example.web.servlet.AnalyticsServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;

import static org.example.preset.FinancialTrackerInit.DATE_FORMAT;

public class AnalyticsServletTest {
    private final AnalyticsServlet analyticsServlet = Mockito.spy(new AnalyticsServlet());
    private final AnalyticsService analyticsService = Mockito.spy(new AnalyticsService());
    private final Map.Entry<Map.Entry<LocalDate, LocalDate>, Long> dto =
            Map.entry(Map.entry(LocalDate.parse("01.01.2024", DATE_FORMAT), LocalDate.parse("31.12.2024", DATE_FORMAT)), 2L);

    @Test
    @DisplayName("Печать баланса текущего счета пользователя")
    public void givenCurrentUser_whenTryToGetBalance_thenReturnBadRequest() throws IOException {
        String balanceView = analyticsService.balance(dto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/analytics/balance");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);

        analyticsServlet.doGet(request, response);
        Mockito.verify(writer).println(balanceView);
    }

    @Test
    @DisplayName("Печать оборотной ведомости за период")
    public void givenCurrentUser_whenTryToGetSummary_thenReturnBadRequest() throws IOException {
        String summaryView = analyticsService.summary (dto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/analytics/summary");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getParameter("from")).thenReturn("01.01.2024");
        Mockito.when(request.getParameter("to")).thenReturn("31.12.2024");

        analyticsServlet.doGet(request, response);
        Mockito.verify(writer).println(summaryView);
    }

    @Test
    @DisplayName("Печать расходов сгруппированных по категориям")
    public void givenCurrentUser_whenTryToGetExpenses_thenReturnBadRequest() throws IOException {
        String expensesView = analyticsService.expenses (dto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/analytics/expenses");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);

        analyticsServlet.doGet(request, response);
        Mockito.verify(writer).println(expensesView);
    }

    @Test
    @DisplayName("Печать текущего финансового состояния")
    public void givenCurrentUser_whenTryToGetCondition_thenReturnBadRequest() throws IOException {
        String conditionView = analyticsService.condition (dto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/analytics/condition");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);

        analyticsServlet.doGet(request, response);
        Mockito.verify(writer).println(conditionView);
    }
}
