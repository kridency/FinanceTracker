package org.example.web.handler;

import org.example.web.servlet.AnalyticsServlet;
import org.example.web.servlet.FundServlet;

public class AnalyticsHandler extends AbstractHandler {
    private static AnalyticsHandler INSTANCE;

    private AnalyticsHandler() {
        servlet = AnalyticsServlet.getInstance();
    }

    public static AnalyticsHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AnalyticsHandler();
        }
        return INSTANCE;
    }
}
