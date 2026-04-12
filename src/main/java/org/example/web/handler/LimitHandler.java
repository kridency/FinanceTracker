package org.example.web.handler;

import org.example.web.servlet.LimitServlet;

public class LimitHandler extends AbstractHandler {
    private static LimitHandler INSTANCE;

    private LimitHandler() {
        servlet = new LimitServlet();
    }

    public static LimitHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new LimitHandler();
        }
        return INSTANCE;
    }
}
