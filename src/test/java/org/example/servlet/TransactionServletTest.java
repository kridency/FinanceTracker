package org.example.servlet;

import org.example.AbstractTest;
import org.example.dto.TransactionDto;
import org.example.entity.TransactionType;
import org.example.service.TransactionService;
import org.example.web.listener.RequestStream;
import org.example.web.listener.RequestWrapper;
import org.example.web.listener.ResponseWrapper;
import org.example.web.servlet.TransactionServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.Instant;

import static org.example.preset.FinancialTrackerInit.objectMapper;

public class TransactionServletTest extends AbstractTest {
    private static final TransactionServlet transactionServlet = Mockito.spy(new TransactionServlet());
    private static final TransactionService transactionService = Mockito.spy(new TransactionService());

    @Test
    @DisplayName("Печать транзакций отфильтрованных по шаблону")
    public void givenCurrentUserAndTransaction_whenTryToListWithTemplate_thenReturnCorrectResult() throws IOException {
        var transaction = new TransactionDto();
        String transactionList = objectMapper.writeValueAsString(transactionService.findAllByDto(transaction));
        String transactionString = objectMapper.writeValueAsString(transaction);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/list");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(transactionString.getBytes()))));

        transactionServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(transactionList);
    }

    @Test
    @DisplayName("Попытка создать транзакцию")
    void givenUserAndTransactionDto_whenTryToCreate_thenReturnCorrectResult() throws IOException {
        TransactionDto newTransaction = new TransactionDto();
        newTransaction.setDate(Instant.parse("2024-12-25T12:00:00.00Z"));
        newTransaction.setType(TransactionType.WITHDRAW);
        newTransaction.setCategory("Payment");
        newTransaction.setAmount(new BigDecimal("80"));
        newTransaction.setDescription("Purchasing goods");
        newTransaction.setUserId(userService.loadUserByUsername("name@hostname").getId());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/create");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(newTransaction).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        transactionServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + newTransaction.getDescription() + " успешно создана.");
    }

    @Test
    @DisplayName("Попытка изменить транзакцию")
    void givenUserAndTransaction_whenTryToUpdate_thenReturnCorrectResult() throws IOException {
        TransactionDto newTransaction = new TransactionDto();
        newTransaction.setDate(Instant.parse("2024-12-12T12:00:00.00Z"));
        newTransaction.setUserId(userService.loadUserByUsername("name@hostname").getId());
        newTransaction = transactionService.findAllByDto(newTransaction).stream().findFirst().orElse(newTransaction);

        newTransaction.setCategory("Interest");
        newTransaction.setAmount(new BigDecimal("50"));
        newTransaction.setDescription("Receive income from investment");

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/update");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(newTransaction).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        transactionServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + newTransaction.getDescription() + " успешно изменена.");
    }

    @Test
    @DisplayName("Попытка удалить транзакцию")
    void givenUserAndTransaction_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        TransactionDto transaction = new TransactionDto();
        transaction.setDate(Instant.parse("2024-12-18T12:00:00.00Z"));
        transaction.setUserId(userService.loadUserByUsername("name@hostname").getId());
        transaction = transactionService.findAllByDto(transaction).stream().findFirst().orElse(transaction);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/delete");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(transaction).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        transactionServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Запись " + transaction.getDescription() + " успешно удалена.");
    }
}
