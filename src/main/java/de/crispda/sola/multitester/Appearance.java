package de.crispda.sola.multitester;

import java.util.List;

public class Appearance {
    public final TransitionSequence sequence;
    public final int firstIndex;
    public final int secondIndex;
    public final boolean conflict;

    public Appearance(TransitionSequence sequence, int index1, int index2, boolean conflict) {
        this.sequence = sequence;
        this.firstIndex = index1;
        this.secondIndex = index2;
        this.conflict = conflict;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<Transition> transitionList = sequence.getTransitions();
        boolean first = true;
        for (int i = 0; i < transitionList.size(); i++) {
            if (first) {
                first = false;
            } else {
                builder.append(" ");
            }

            if (i == firstIndex || i == secondIndex) {
                builder.append("<").append(transitionList.get(i)).append(">");
            } else {
                builder.append(transitionList.get(i));
            }
        }

        return builder.toString();
    }
}
