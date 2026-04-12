package org.example.web.handler;

import org.example.web.servlet.UserServlet;

public class UserHandler extends AbstractHandler {
    public UserHandler() {
        servlet = new UserServlet();
    }
}
