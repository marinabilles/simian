package de.crispda.sola.multitester.util;

import java.util.logging.Logger;

public class LogDebugAdapter implements DebugAdapter {
    private int threadId;
    private final boolean hasThreadId;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public LogDebugAdapter() {
        hasThreadId = false;
    }

    public LogDebugAdapter(int threadId) {
        hasThreadId = true;
        this.threadId = threadId;
    }

    @Override
    public void write(String text) {
        if (hasThreadId)
            text = String.format("[%d] %s", threadId, text);
        logger.info(text);
    }
}
