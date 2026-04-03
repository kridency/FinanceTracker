package org.example.web.handler;

import org.example.web.servlet.TransactionServlet;

public class TransactionHandler extends AbstractHandler {
        private static TransactionHandler INSTANCE;

        private TransactionHandler() {
            servlet = TransactionServlet.getInstance();
        }

        public static TransactionHandler getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new TransactionHandler();
            }
            return INSTANCE;
        }
    }
