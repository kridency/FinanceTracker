package org.example.web.handler;

import org.example.web.servlet.AnalyticsServlet;

public class AnalyticsHandler extends AbstractHandler {
    private static AnalyticsHandler INSTANCE;

    private AnalyticsHandler() {
        servlet = new AnalyticsServlet();
    }

    public static AnalyticsHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AnalyticsHandler();
        }
        return INSTANCE;
    }
}
