package org.example.web.servlet;

import org.example.dto.FundDto;
import org.example.service.FundService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FundServlet extends AbstractServlet<FundDto> {

    public FundServlet() {
        PATH = "/api/v1/fund";
        service = new FundService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response =response;
        process("/list", FundDto.class);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response =response;
        process("/create", FundDto.class);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response =response;
        process("/update", FundDto.class);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response =response;
        process("/delete", FundDto.class);
    }
}
