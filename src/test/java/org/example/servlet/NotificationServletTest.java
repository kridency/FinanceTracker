package org.example.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.AbstractTest;
import org.example.dto.TransactionDto;
import org.example.entity.TransactionType;
import org.example.service.TransactionService;
import org.example.web.listener.RequestStream;
import org.example.web.listener.RequestWrapper;
import org.example.web.listener.ResponseWrapper;
import org.example.web.servlet.NotificationServlet;
import org.example.web.servlet.TransactionServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.Instant;

import static org.example.preset.FinancialTrackerInit.objectMapper;

public class NotificationServletTest extends AbstractTest  {
    @Test
    @DisplayName("Попытка активации уведомлений пользователя и списания средств с овердрафтом")
    public void givenCurrentUserAndNewTransaction_whenTryToActivateNotification_thenReturnBadRequest() throws IOException {
        PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/notification/toggle");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(objectMapper
                        .writeValueAsString("{}").getBytes()))));

        NotificationServlet.getInstance().doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Уведомления успешно активированы");

        TransactionDto newTransaction = new TransactionDto();
        newTransaction.setDate(Instant.parse("2024-12-15T12:00:00.00Z"));
        newTransaction.setType(TransactionType.WITHDRAW);
        newTransaction.setCategory("Payment");
        newTransaction.setAmount(BigDecimal.valueOf(1_000.00));
        newTransaction.setDescription("Purchasing goods");
        newTransaction.setUserId(userService.loadUserByUsername("name@hostname").getId());

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/create");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(newTransaction).getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        TransactionServlet.getInstance().doPost(request, response);
        Mockito.verify(writer)  .println("Уведомление: превышение установленного лимита расходования");
    }
}
