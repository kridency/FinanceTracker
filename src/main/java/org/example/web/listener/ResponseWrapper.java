package org.example.web.listener;

import com.sun.net.httpserver.HttpExchange;
import org.example.exception.ApplicationException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;

import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final ServletOutputStream servletOutputStream = new ResponseStream(outputStream);

    private final HttpExchange ex;
    private final PrintWriter printWriter;
    private int status = HttpServletResponse.SC_OK;

    public ResponseWrapper(HttpServletResponse response, HttpExchange ex) {
        super(response);
        this.ex = ex;
        printWriter = new PrintWriter(servletOutputStream);
    }

    @Override
    public void setContentType(String type) {
        ex.getResponseHeaders().add("Content-Type", type);
    }

    @Override
    public void setHeader(String name, String value) {
        ex.getResponseHeaders().add(name, value);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return servletOutputStream;
    }

    @Override
    public void setContentLength(int len) {
        ex.getResponseHeaders().add("Content-Length", len + "");
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void sendError(int sc, String msg) {
        this.status = sc;
        if (msg != null) {
            printWriter.write(msg);
        }
    }

    @Override
    public void sendError(int sc) {
        sendError(sc, null);
    }

    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    public void complete() throws ApplicationException {
        try {
            printWriter.flush();
            ex.sendResponseHeaders(status, outputStream.size());
            if (outputStream.size() > 0) {
                ex.getResponseBody().write(outputStream.toByteArray());
            }
            ex.getResponseBody().flush();
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        } finally {
            ex.close();
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        this.setHeader("Set-Cookie",
                cookie.getName() + "=" + cookie.getValue()
                        + "; Path=" + cookie.getPath()
                        + "; Max-Age=" + cookie.getMaxAge() + ";");
    }
}
