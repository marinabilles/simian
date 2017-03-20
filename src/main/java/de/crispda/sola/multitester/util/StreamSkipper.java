package de.crispda.sola.multitester.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamSkipper extends Thread {
    private final InputStream inputStream;

    public StreamSkipper(InputStream is) {
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
        // skip the stream, then exit
        while (!isEOF()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                inputStream.skip(inputStream.available());
            } catch (IOException ex) {
                break;
            }
        }
    }
}
