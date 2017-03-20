package de.crispda.sola.multitester;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InteractionTest implements TestSpec {
    private final List<Interaction> interactionsFirst;
    private final List<Interaction> interactionsSecond;
    private final Combinator combinator;
    private TestInit init;
    private String name;

    public InteractionTest(Combinator combinator, List<Interaction> interactionsFirst,
                           List<Interaction> interactionsSecond) {
        this.interactionsFirst = interactionsFirst;
        this.interactionsSecond = interactionsSecond;
        this.combinator = combinator;
    }

    public InteractionTest(Combinator combinator, List<Interaction> interactionsFirst,
                           List<Interaction> interactionsSecond, String name) {
        this(combinator, interactionsFirst, interactionsSecond);
        this.name = name;
    }

    public InteractionTest(Combinator combinator, List<Interaction> interactionsFirst,
                           List<Interaction> interactionsSecond, TestInit init) {
        this(combinator, interactionsFirst, interactionsSecond);
        this.init = init;
    }

    public InteractionTest(Combinator combinator, List<Interaction> interactionsFirst,
                           List<Interaction> interactionsSecond, TestInit init, String name) {
        this(combinator, interactionsFirst, interactionsSecond, init);
        this.name = name;
    }

    @Override
    public Optional<TestInit> getInit() {
        return Optional.ofNullable(init);
    }

    @Override
    public ScheduleVariant<?> createVariant(TransitionSequence sequence) {
        List<Interaction> firstOrder = new ArrayList<>();
        List<Interaction> secondOrder = new ArrayList<>();
        for (int i = 0; i < sequence.size(); i++) {
            Transition t = sequence.get(i);
            if (t.threadId == 1) {
                firstOrder.add(interactionsFirst.get(t.sequenceId));
            } else {
                secondOrder.add(interactionsSecond.get(t.sequenceId));
            }
        }

        MultiTest<Test> multiTest = new MultiTest<>(combinator.combine(firstOrder),
                combinator.combine(secondOrder));

        return multiTest.createVariant(sequence);
    }

    @Override
    public TransitionSequence createInitial(List<Transition> transitionsFirst, List<Transition> transitionsSecond) {
        List<Transition> initialTransitions = new ArrayList<>();
        int i = 0, j = 0;
        while (i < transitionsFirst.size() || j < transitionsSecond.size()) {
            if (i < transitionsFirst.size()) {
                initialTransitions.add(transitionsFirst.get(i));
                i++;
            }
            if (j < transitionsSecond.size()) {
                initialTransitions.add(transitionsSecond.get(j));
                j++;
            }
        }
        return new TransitionSequence(initialTransitions);
    }

    @Override
    public int getFirstTransitionCount() {
        return interactionsFirst.size();
    }

    @Override
    public int getSecondTransitionCount() {
        return interactionsSecond.size();
    }

    @Override
    public String getName() {
        return name;
    }
}
