package de.crispda.sola.multitester;

import java.util.ArrayList;

/**
 * Represents a multi-client interaction with sequentially executed
 * and parallel parts. Only allows two actions to be parallel with each other
 * at any one time, with {@link GuidedStateSpaceExplorer.ParallelStep}, i.e. more restricted than
 * {@link TransitionSequence} with {@link ScheduleVariant}, used to represent
 * generic interleavings between two threads.
 */
public class ExplorationState extends ArrayList<ExplorationStep> {
    public static final long serialVersionUID = 4548736904515292418L;

    public static ExplorationState create(GuidedStateSpaceExplorer.ParallelStep par) {
        ExplorationState state = new ExplorationState();
        state.add(par);
        return state;
    }

    public static ExplorationState create(GuidedStateSpaceExplorer.SequentialStep seq,
                                          GuidedStateSpaceExplorer.ParallelStep par) {
        ExplorationState state = new ExplorationState();
        state.add(seq);
        state.add(par);
        return state;
    }

    public static ExplorationState create(GuidedStateSpaceExplorer.SequentialStep seq1,
                                          GuidedStateSpaceExplorer.SequentialStep seq2,
                                          GuidedStateSpaceExplorer.ParallelStep par) {
        ExplorationState state = new ExplorationState();
        state.add(seq1);
        state.add(seq2);
        state.add(par);
        return state;
    }

    public static ExplorationState create(GuidedStateSpaceExplorer.SequentialStep seq) {
        ExplorationState state = new ExplorationState();
        state.add(seq);
        return state;
    }

    public static ExplorationState create(GuidedStateSpaceExplorer.SequentialStep seq1,
                                          GuidedStateSpaceExplorer.SequentialStep seq2) {
        ExplorationState state = new ExplorationState();
        state.add(seq1);
        state.add(seq2);
        return state;
    }

}
