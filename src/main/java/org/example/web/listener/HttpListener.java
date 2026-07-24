package org.example.web.listener;

import com.sun.net.httpserver.HttpServer;
import org.example.exception.ApplicationException;
import org.example.property.ApplicationProperties;
import org.example.web.handler.*;
import org.example.web.servlet.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpListener {
    private static HttpListener INSTANCE;
    private HttpServer httpServer;
    private final WebHandler<UserServlet> userHandler;
    private final WebHandler<TransactionServlet> transactionHandler;
    private final WebHandler<FundServlet> fundHandler;
    private final WebHandler<LimitServlet> limitHandler;
    private final WebHandler<NotificationServlet> notificationHandler;
    private final WebHandler<AnalyticsServlet> analyticsHandler;

    protected static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private HttpListener() {
        userHandler = new WebHandler<>();
        transactionHandler = new WebHandler<>();
        fundHandler = new WebHandler<>();
        limitHandler = new WebHandler<>();
        notificationHandler = new WebHandler<>();
        analyticsHandler = new WebHandler<>();
    }

    public static HttpListener getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new HttpListener();
        }
        return INSTANCE;
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
    }

    public void startHTTPServer() {
        try {
            var address = new InetSocketAddress(Integer.parseInt(applicationProperties.getProperty("app.port")));
            httpServer = HttpServer.create(address, 100);
            httpServer.createContext("/api/v1/auth/", userHandler);
            httpServer.createContext("/api/v1/identity/", userHandler);
            httpServer.createContext("/api/v1/administration/", userHandler);
            httpServer.createContext("/api/v1/transaction/", transactionHandler);
            httpServer.createContext("/api/v1/fund/", fundHandler);
            httpServer.createContext("/api/v1/limit/", limitHandler);
            httpServer.createContext("/api/v1/notification/", notificationHandler);
            httpServer.createContext("/api/v1/analytics/", analyticsHandler);
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public int getServerPort() {
        return httpServer.getAddress().getPort();
    }
}
