package de.crispda.sola.multitester;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Transition {
    public final int threadId;
    public final int sequenceId;

    public Transition(int threadId, int sequenceId) {
        this.threadId = threadId;
        this.sequenceId = sequenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return threadId == that.threadId &&
                sequenceId == that.sequenceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadId, sequenceId);
    }

    @Override
    public String toString() {
        return String.format("%d.%d", threadId, sequenceId);
    }

    public static List<Transition> createTransitions(int threadId, int count) {
        return IntStream.rangeClosed(0, count - 1).boxed()
                .map(c -> new Transition(threadId, c)).collect(Collectors.toList());
    }
}
