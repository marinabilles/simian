package de.crispda.sola.multitester;

public class FailureReason {
    public final String neutralEventName;
    public final int index;
    public final boolean executingThread;

    public FailureReason(String neutralEventName, int index, boolean executingThread) {
        this.neutralEventName = neutralEventName;
        this.index = index;
        this.executingThread = executingThread;
    }
}
