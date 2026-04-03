package org.example.web.listener;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;

public class RequestStream extends ServletInputStream {
    private final byte[] inBytes;
    private int lastIndexRetrieved = -1;
    private ReadListener readListener = null;

    public RequestStream(byte[] inBytes) {
        this.inBytes = inBytes;
    }

    @Override
    public boolean isFinished() {
        return (lastIndexRetrieved == inBytes.length - 1);
    }

    @Override
    public boolean isReady() {
        return isFinished();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
        if (!isFinished()) {
            try {
                readListener.onDataAvailable();
            } catch (IOException e) {
                readListener.onError(e);
            }
        } else {
            try {
                readListener.onAllDataRead();
            } catch (IOException e) {
                readListener.onError(e);
            }
        }
    }

    @Override
    public int read() throws IOException {
        int i;
        if (!isFinished()) {
            i = inBytes[lastIndexRetrieved+1];
            lastIndexRetrieved++;
            if (isFinished() && (readListener != null)) {
                try {
                    readListener.onAllDataRead();
                } catch (IOException ex) {
                    readListener.onError(ex);
                    throw ex;
                }
            }
            return i;
        } else {
            return -1;
        }
    }
}
