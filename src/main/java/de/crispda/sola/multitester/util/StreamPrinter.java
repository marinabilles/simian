package de.crispda.sola.multitester.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamPrinter extends Thread {
    private final InputStream inputStream;

    public StreamPrinter(InputStream is) {
        inputStream = is;
    }

    private boolean isEOF() {
        try {
            int read = inputStream.read();
            return read == -1;
        } catch (IOException ex) {
            return true;
        }
    }

    @Override
    public void run() {
        // print the stream, then exit
        int size;
        byte[] buffer = new byte[1024];
        while (!isEOF()) {
            try {
                while ((size = inputStream.read(buffer)) != -1) {
                    System.out.write(buffer, 0, size);
                }
            } catch (IOException ex) {
                break;
            }
        }
    }
}
