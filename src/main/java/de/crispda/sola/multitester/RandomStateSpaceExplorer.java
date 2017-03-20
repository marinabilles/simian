package de.crispda.sola.multitester;

import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.util.DatabaseConnector;
import de.crispda.sola.multitester.web.RemoteDriverSupplier;

import javax.xml.transform.TransformerConfigurationException;
import java.time.Duration;
import java.time.Instant;

public class RandomStateSpaceExplorer extends StateSpaceExplorer {
    public RandomStateSpaceExplorer(SetExperimentSpec experimentSpec, RemoteDriverSupplier driverSupplier,
                                    String path) throws TransformerConfigurationException {
        super(experimentSpec, driverSupplier, path);
    }

    public void explore(int depth, Duration timeout) throws InterruptedException {
        if (depth <= 0)
            throw new IllegalArgumentException("depth cannot be 0");

        Instant before = Instant.now();
        this.runState = RunState.RUNNING;
        try {
            do {
                if (isStopped())
                    return;

                ExplorationState state = new ExplorationState();
                double singleProb = 1.0d / (depth);
                int prefixLength = 0;
                double randomValue = Math.random();
                while (randomValue >= (prefixLength + 1) * singleProb) {
                    prefixLength++;
                }

                while (prefixLength > 0) {
                    Interaction act = randomInteraction();
                    state.add(new GuidedStateSpaceExplorer.SequentialStep(act));
                    prefixLength--;
                }

                Interaction first = randomInteraction();
                Interaction second = randomInteraction();
                state.add(new GuidedStateSpaceExplorer.ParallelStep(first, second));

                parallelRun(state);
            } while (!timeout.minus(Duration.between(before, Instant.now())).isNegative());
            logger.info("Done exploring after " + timeout.minus(Duration.between(before, Instant.now())).toString());
        } finally {
            DatabaseConnector.close();
        }
    }

    private Interaction randomInteraction() {
        int interactionIndex = 0;
        double singleProb = 1.0 / interactions.size();
        double randomValue = Math.random();
        while (randomValue >= (interactionIndex + 1) * singleProb) {
            interactionIndex++;
        }

        return interactions.get(interactionIndex);
    }
}
