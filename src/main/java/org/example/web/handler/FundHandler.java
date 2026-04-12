package org.example.web.handler;

import org.example.web.servlet.FundServlet;

public class FundHandler extends AbstractHandler {

    public FundHandler() {
        servlet = new FundServlet();
    }
}
