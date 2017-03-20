package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.Transition;

public class OrderedTransition {
    public final int index;
    public final Transition transition;

    public OrderedTransition(int index, Transition transition) {
        this.index = index;
        this.transition = transition;
    }

    public OrderedTransition(int index) {
        this.index = index;
        this.transition = null;
    }

    @Override
    public String toString() {
        if (transition != null)
            return String.format("%d - %s", index, transition);
        else
            return Integer.toString(index);
    }
}
