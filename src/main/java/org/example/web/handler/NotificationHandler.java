package org.example.web.handler;

import org.example.web.servlet.NotificationServlet;

public class NotificationHandler extends AbstractHandler {
    public NotificationHandler() {
        servlet = new NotificationServlet();
    }
}
