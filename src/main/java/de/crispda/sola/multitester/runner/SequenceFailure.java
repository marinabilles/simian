package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.Transition;
import de.crispda.sola.multitester.TransitionSequence;

public class SequenceFailure extends Failure {
    private final Transition state;
    private final int failedThreadId;
    private final TransitionSequence sequence;

    public SequenceFailure(int id, String neutralEventName, TransitionSequence sequence, Transition state,
                           int failedThreadId) {
        super(id, neutralEventName);
        this.state = state;
        this.failedThreadId = failedThreadId;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return String.format("%d - %s (Failed: %d)", id, sequence.toFailureString(state), failedThreadId);
    }
}
