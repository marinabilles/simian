package de.crispda.sola.multitester;

import java.util.logging.Logger;

public abstract class Stoppable {
    protected volatile RunState runState;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void setRunState(RunState runState) {
        this.runState = runState;
    }

    protected boolean isStopped() throws InterruptedException {
        RunState runState;
        runState = this.runState;
        boolean paused = false;
        if (runState == RunState.PAUSED) {
            logger.info("Paused execution");
            paused = true;
        }
        while (runState != RunState.RUNNING) {
            if (runState == RunState.STOPPED)
                return true;
            Thread.sleep(500);
            runState = this.runState;
        }
        if (paused)
            logger.info("Resumed execution");
        return false;
    }
}
