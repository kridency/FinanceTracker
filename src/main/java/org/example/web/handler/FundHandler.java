package org.example.web.handler;

import org.example.web.servlet.FundServlet;

public class FundHandler extends AbstractHandler {
    private static FundHandler INSTANCE;

    private FundHandler() {
        servlet = new FundServlet();
    }

    public static FundHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FundHandler();
        }
        return INSTANCE;
    }
}
