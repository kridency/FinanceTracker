package org.example.web.handler;

import org.example.web.servlet.TransactionServlet;

public class TransactionHandler extends AbstractHandler {

        public TransactionHandler() {
            servlet = new TransactionServlet();
        }
    }
