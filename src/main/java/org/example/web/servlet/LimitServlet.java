package org.example.web.servlet;

import org.example.dto.LimitDto;
import org.example.service.LimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LimitServlet extends AbstractServlet<LimitDto> {

    public LimitServlet() {
        PATH = "/api/v1/limit";
        service = new LimitService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/list", LimitDto.class);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/create", LimitDto.class);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/update", LimitDto.class);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        process(request, response, "/delete", LimitDto.class);
    }
}
