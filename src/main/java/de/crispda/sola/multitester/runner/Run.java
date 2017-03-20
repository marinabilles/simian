package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.TransitionSequence;

public class Run {
    public final int id;
    public final TransitionSequence sequence;
    public final Execution execution;

    public Run(int id, TransitionSequence sequence, Execution ex) {
        this.id = id;
        this.sequence = sequence;
        this.execution = ex;
    }
}
