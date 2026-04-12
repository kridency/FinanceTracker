package org.example.web.handler;

import org.example.web.servlet.TransactionServlet;

public class TransactionHandler extends AbstractHandler {
        private static TransactionHandler INSTANCE;

        private TransactionHandler() {
            servlet = new TransactionServlet();
        }

        public static TransactionHandler getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new TransactionHandler();
            }
            return INSTANCE;
        }
    }
