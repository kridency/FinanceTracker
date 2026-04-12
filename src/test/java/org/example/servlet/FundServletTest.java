package org.example.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.AbstractTest;
import org.example.dto.FundDto;
import org.example.service.FundService;
import org.example.web.listener.RequestStream;
import org.example.web.listener.RequestWrapper;
import org.example.web.listener.ResponseWrapper;
import org.example.web.servlet.FundServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static org.example.preset.FinancialTrackerInit.objectMapper;

public class FundServletTest extends AbstractTest {
    private static final FundServlet fundServlet = Mockito.spy(new FundServlet());
    private static final FundService fundService = Mockito.spy(new FundService());

    @Test
    @DisplayName("Печать фондов накоплений отфильтрованных по шаблону")
    public void givenCurrentUserAndFund_whenTryToList_thenReturnBadRequest() throws IOException {
        var fund = new FundDto();
        String fundList = objectMapper.writeValueAsString(fundService.findAllByDto(fund));
        String fundString = objectMapper.writeValueAsString(fund);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/fund/list");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(fundString.getBytes()))));

        fundServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(fundList);
    }

    @Test
    @DisplayName("Попытка создать фонд накоплений")
    void givenUserAndFundDto_whenTryToCreate_thenReturnCorrectResult() throws IOException {
        var newFund = new FundDto();
        newFund.setTitle("Pension");
        newFund.setTarget(BigDecimal.valueOf(10_000.00));
        newFund.setSavings(BigDecimal.valueOf(1_000.00));
        newFund.setUserId(userService.loadUserByUsername("name@hostname").getId());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/fund/create");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(newFund).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        fundServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + newFund.getTitle() + " успешно создана.");
    }

    @Test
    @DisplayName("Попытка изменить фонд накоплений")
    void givenUserAndFundDto_whenTryToUpdate_thenReturnCorrectResult() throws IOException {
        FundDto newFund = new FundDto();
        newFund.setTitle("Pay out mortgage");
        newFund.setUserId(userService.loadUserByUsername("name@hostname").getId());

        newFund.setTarget(BigDecimal.valueOf (10_100.00));
        newFund.setSavings(BigDecimal.valueOf (1_000.00));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/fund/update");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(newFund).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        fundServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + newFund.getTitle() + " успешно изменена.");
    }

    @Test
    @DisplayName("Попытка удалить фонд накоплений")
    void givenUserAndFundDto_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        FundDto fund = new FundDto();
        fund.setTitle("New car purchase");
        fund.setUserId(userService.loadUserByUsername("name@hostname").getId());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/fund/delete");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(fund).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        fundServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Запись " + fund.getTitle() + " успешно удалена.");
    }
}
