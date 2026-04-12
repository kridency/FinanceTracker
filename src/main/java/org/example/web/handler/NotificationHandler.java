package org.example.web.handler;

import org.example.web.servlet.NotificationServlet;

public class NotificationHandler extends AbstractHandler {
    private static NotificationHandler INSTANCE;

    private NotificationHandler() {
        servlet = new NotificationServlet();
    }

    public static NotificationHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new NotificationHandler();
        }
        return INSTANCE;
    }
}
