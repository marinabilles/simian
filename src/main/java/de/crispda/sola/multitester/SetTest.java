package de.crispda.sola.multitester;

import java.util.List;
import java.util.Optional;

public class SetTest implements TestSpec {
    private String name;
    private final Combinator combinator;
    private final List<Interaction> interactionsFirst;
    private final List<Interaction> interactionsSecond;

    public SetTest(Combinator combinator, List<Interaction> interactionsFirst,
                   List<Interaction> interactionsSecond) {
        this.combinator = combinator;
        this.interactionsFirst = interactionsFirst;
        this.interactionsSecond = interactionsSecond;
    }

    public SetTest(Combinator combinator, List<Interaction> interactionsFirst,
                   List<Interaction> interactionsSecond, String name) {
        this(combinator, interactionsFirst, interactionsSecond);
        this.name = name;
    }

    @Override
    public Optional<TestInit> getInit() {
        return Optional.empty();
    }

    @Override
    public ScheduleVariant<?> createVariant(TransitionSequence sequence) {
        MultiTest<CombinedTest> multiTest = new MultiTest<>(combinator.combine(interactionsFirst),
                combinator.combine(interactionsSecond));

        return multiTest.createVariant(sequence);
    }

    @Override
    public TransitionSequence createInitial(List<Transition> transitionsFirst, List<Transition> transitionsSecond) {
        return null;
    }

    @Override
    public int getFirstTransitionCount() {
        return 0;
    }

    @Override
    public int getSecondTransitionCount() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }
}
