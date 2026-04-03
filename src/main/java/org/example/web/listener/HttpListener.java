package org.example.web.listener;

import com.sun.net.httpserver.HttpServer;
import org.example.exception.ApplicationException;
import org.example.web.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpListener {
    private static HttpListener INSTANCE;
    private HttpServer httpServer;
    private final UserHandler userHandler;
    private final TransactionHandler transactionHandler;
    private final FundHandler fundHandler;
    private final LimitHandler limitHandler;
    private final NotificationHandler notificationHandler;
    private final AnalyticsHandler analyticsHandler;

    private HttpListener() {
        userHandler = UserHandler.getInstance();
        transactionHandler = TransactionHandler.getInstance();
        fundHandler = FundHandler.getInstance();
        limitHandler = LimitHandler.getInstance();
        notificationHandler = NotificationHandler.getInstance();
        analyticsHandler = AnalyticsHandler.getInstance();
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

    public void startHTTPServer(int port) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 100);
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
