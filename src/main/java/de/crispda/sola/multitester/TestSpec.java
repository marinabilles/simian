package de.crispda.sola.multitester;

import java.util.List;
import java.util.Optional;

public interface TestSpec {
    Optional<TestInit> getInit();
    ScheduleVariant<?> createVariant(TransitionSequence sequence);
    TransitionSequence createInitial(List<Transition> transitionsFirst, List<Transition> transitionsSecond);
    int getFirstTransitionCount();
    int getSecondTransitionCount();
    String getName();
}
