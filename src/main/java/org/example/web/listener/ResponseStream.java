package org.example.web.listener;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

public class ResponseStream extends ServletOutputStream {
    private final OutputStream absorbStream;

    public ResponseStream(OutputStream stream) {
        this.absorbStream = stream;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {}

    @Override
    public void write(int b) throws IOException {
        absorbStream.write(b);
    }
}
