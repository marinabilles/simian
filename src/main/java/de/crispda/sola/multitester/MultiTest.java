package de.crispda.sola.multitester;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MultiTest<T extends Test> implements TestSpec {
    private final T first;
    private final T second;
    private String name;
    private TestInit init;

    public MultiTest(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public MultiTest(T first, T second, String name) {
        this(first, second);
        this.name = name;
    }

    public MultiTest(T first, T second, String name, TestInit init) {
        this(first, second, name);
        this.init = init;
    }

    public int getFirstMaybeWaitCount() {
        return first.getMaybeWaitCount();
    }

    public int getSecondMaybeWaitCount() {
        return second.getMaybeWaitCount();
    }

    public T getFirstTest() {
        return first;
    }

    public T getSecondTest() {
        return second;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<TestInit> getInit() {
        return Optional.ofNullable(init);
    }

    @Override
    public ScheduleVariant<?> createVariant(TransitionSequence sequence) {
        ScheduleVariant<?> variant = new ScheduleVariant<>(this);
        variant.queueFirst(new MaybeWait(1));
        variant.queueSecond(new MaybeWait(1));
        List<Transition> transitions = sequence.getTransitions();
        for (Transition transition : transitions) {
            if (transition.threadId == 1) {
                variant.queueFirst(new MaybeWait(1));
                variant.incrementSecond(1);
            } else {
                variant.incrementFirst(1);
                variant.queueSecond(new MaybeWait(1));
            }
        }
        return variant;
    }

    @Override
    public TransitionSequence createInitial(List<Transition> transitionsFirst, List<Transition> transitionsSecond) {
        List<Transition> seq = new ArrayList<>(transitionsFirst);
        seq.addAll(transitionsSecond);
        return new TransitionSequence(seq);
    }

    @Override
    public int getFirstTransitionCount() {
        return getFirstMaybeWaitCount() - 1;
    }

    @Override
    public int getSecondTransitionCount() {
        return getSecondMaybeWaitCount() - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiTest multiTest = (MultiTest) o;
        return Objects.equals(first, multiTest.first) &&
                Objects.equals(second, multiTest.second) ||
                Objects.equals(first, multiTest.second) &&
                Objects.equals(second, multiTest.first);
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }
}
