package org.example.web.handler;

import org.example.web.servlet.UserServlet;

public class UserHandler extends AbstractHandler {
    private static UserHandler INSTANCE;

    private UserHandler() {
        servlet = UserServlet.getInstance();
    }

    public static UserHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserHandler();
        }
        return INSTANCE;
    }
}
