package org.example.web.servlet;

import org.example.dto.FundDto;
import org.example.exception.ApplicationException;
import org.example.service.FundService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import static org.example.preset.FinancialTrackerInit.*;

public class FundServlet extends AbstractServlet<FundDto> {

    public FundServlet() {
        PATH = "/api/v1/fund";
        service = new FundService();
    }

    protected void process(HttpServletRequest request, HttpServletResponse response, String endpoint) {
        String[] tokens = request.getPathInfo().split(PATH);
        if (tokens.length > 1 && tokens[1].equals(endpoint)) {
            try (BufferedReader reader = request.getReader()) {
                var dto = objectMapper.readValue(reader.lines().collect(Collectors.joining()), FundDto.class);
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
