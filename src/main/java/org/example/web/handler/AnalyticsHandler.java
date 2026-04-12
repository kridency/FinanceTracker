package org.example.web.handler;

import org.example.web.servlet.AnalyticsServlet;

public class AnalyticsHandler extends AbstractHandler {

    public AnalyticsHandler() {
        servlet = new AnalyticsServlet();
    }
}
