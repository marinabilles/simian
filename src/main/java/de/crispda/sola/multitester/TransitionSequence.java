package de.crispda.sola.multitester;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public class TransitionSequence {
    public static final TransitionSequence empty = new TransitionSequence(new ArrayList<>());
    private final List<Transition> transitions;

    public TransitionSequence(List<Transition> transitions) {
        this.transitions = ImmutableList.copyOf(transitions);
    }

    public Transition get(int index) {
        return transitions.get(index);
    }

    public Transition getFirst() {
        return transitions.get(0);
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public int size() {
        return transitions.size();
    }

    public TransitionSequence[] split(int index) {
        return new TransitionSequence[] {
                new TransitionSequence(transitions.subList(0, index)),
                new TransitionSequence(transitions.subList(index, transitions.size()))
        };
    }

    public OptionalInt getMax(final int threadId) {
        return transitions.stream().filter(t -> t.threadId == threadId).mapToInt(t -> t.sequenceId).max();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransitionSequence transitionSequence = (TransitionSequence) o;
        return Objects.equals(transitions, transitionSequence.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transitions);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        for (Transition t: transitions) {
            sb.append(" ").append(t);
        }
        sb.append(" }");
        return sb.toString();
    }

    public String toFailureString(Transition state) {
        final StringBuilder sb = new StringBuilder("{");
        if (state.sequenceId == -1)
            sb.append(" <>");
        for (Transition t: transitions) {
            sb.append(" ").append(t);
            if (t.equals(state))
                sb.append(" <>");
        }
        sb.append(" }");
        return sb.toString();
    }

    public TransitionSequence concat(Transition current) {
        List<Transition> newTransitions = new ArrayList<>(transitions);
        newTransitions.add(current);
        return new TransitionSequence(newTransitions);
    }

    public Transition getLatest(int firstThreadId, int firstIndex, int secondThreadId, int secondIndex) {
        int seenFirst = 0, seenSecond = 0;
        Transition t1 = null, t2 = null;
        for (Transition t : transitions) {
            if (t.threadId == firstThreadId) {
                if (seenFirst == firstIndex) {
                    t1 = t;
                }
                seenFirst++;
            }
            if (t.threadId == secondThreadId) {
                if (seenSecond == secondIndex) {
                    t2 = t;
                }
                seenSecond++;
            }
            if (t1 != null && t2 != null)
                break;
        }

        Transition latest = null;
        boolean seen_t1 = false;
        boolean seen_t2 = false;
        for (Transition t : transitions) {
            if (t.equals(t1)) {
                latest = t1;
                seen_t1 = true;
            }
            if (t.equals(t2)) {
                latest = t2;
                seen_t2 = true;
            }
            if (seen_t1 && seen_t2)
                break;
        }
        return latest;
    }
}
