package org.example.web.servlet;

import jakarta.servlet.annotation.WebServlet;
import org.example.dto.TransactionDto;
import org.example.handler.NotificationInvocationHandler;
import org.example.service.CrudService;
import org.example.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Proxy;

@WebServlet(urlPatterns = {"/api/v1/transaction/create",
        "/api/v1/transaction/update",
        "/api/v1/transaction/delete",
        "/api/v1/transaction/list"})
public class TransactionServlet extends AbstractServlet<TransactionDto> {

    @SuppressWarnings("unchecked")
    public TransactionServlet() {
        PATH = "/api/v1/transaction";
        service = (CrudService<TransactionDto>) Proxy.newProxyInstance (
                CrudService.class.getClassLoader(),
                new Class<?>[] { CrudService.class },
                new NotificationInvocationHandler<>(new TransactionService()));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        process("/list", TransactionDto.class);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        process("/create", TransactionDto.class);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        process("/update", TransactionDto.class);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        process("/delete", TransactionDto.class);
    }
}

