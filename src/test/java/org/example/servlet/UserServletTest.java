package org.example.servlet;

import org.example.AbstractTest;
import org.example.dto.TransactionDto;
import org.example.dto.UserDto;
import org.example.entity.StatusType;
import org.example.service.TransactionService;
import org.example.web.listener.RequestStream;
import org.example.web.listener.RequestWrapper;
import org.example.web.listener.ResponseWrapper;
import org.example.web.servlet.UserServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

import static org.example.preset.FinancialTrackerInit.*;

public class UserServletTest extends AbstractTest {
    private static final UserServlet userServlet = Mockito.spy(new UserServlet());
    private static final TransactionService transactionService = Mockito.spy(new TransactionService());

    @Test
    @DisplayName("Создание учетной записи пользователя.")
    void givenNewUser_whenTryToCreate_thenReturnCorrectResult() throws IOException {
        var userDto = new UserDto("newName", "newName@hostname", "12345");

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/auth/create");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(userDto).getBytes())
        )));
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + userDto.getEmail() + " успешно создана.");
    }

    @Test
    @DisplayName("Изменение учетной записи пользователя.")
    void givenExistingUser_whenTryToUpdate_thenReturnCorrectResult() throws IOException {
        var userDto = new UserDto("newName", "name@hostname", "12345");

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/identity/update");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "name@hostname");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(userDto).getBytes())
        )));
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Запись " + userDto.getEmail() + " успешно изменена.");
    }

    @Test
    @DisplayName("Удаление тестовой учетной записи.")
    void givenTestUser_whenTryToDelete_thenReturnsCorrectResult() throws IOException {
        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/identity/delete");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "test@hostname");
        Mockito.when(request.getReader()).thenReturn(Mockito.mock(BufferedReader.class));
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Запись test@hostname успешно удалена.");
    }

    @Test
    @DisplayName("Расшифровка действующих учётных записей.")
    void givenAdminUser_whenTryToPrintUser_thenReturnsCorrectResult() throws IOException {
        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        var userDto = new UserDto();
        userDto.setStatus(StatusType.ACTIVE);
        String userList = objectMapper.writeValueAsString(userService.findAllByDto(userDto));
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/administration/list");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "admin@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(userDto).getBytes())
        )));

        userServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(userList);
    }

    @Test
    @DisplayName("Расшифровка транзакций пользователя.")
    void givenAdminUserAndUserId_whenTryToPrintTransactions_thenReturnsCorrectResult() throws IOException {
        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        var transactionDto = new TransactionDto();
        transactionDto.setUserId(2L);
        String transactionList = objectMapper.writeValueAsString(transactionService.findAllByDto(transactionDto));
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/administration/transactions");
        Mockito.when(request.getUserPrincipal()).thenReturn(() -> "admin@hostname");
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream("{}".getBytes()))));
        Mockito.when(request.getParameter("id")).thenReturn("2");

        userServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(transactionList);
    }
}
