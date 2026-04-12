package org.example.web.handler;

import org.example.web.servlet.LimitServlet;

public class LimitHandler extends AbstractHandler {

    public LimitHandler() {
        servlet = new LimitServlet();
    }
}
