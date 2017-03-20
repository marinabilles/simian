package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.RandomExplorer;
import de.crispda.sola.multitester.RunState;
import de.crispda.sola.multitester.web.Drivers;

public class RandomExperimentTask extends ExperimentTask {
    private final RandomExplorer randomExplorer;

    public RandomExperimentTask(ExperimentSpec experimentSpec, String path) {
        if (!(experimentSpec instanceof SetExperimentSpec))
            throw new IllegalArgumentException("experimentSpec");

        randomExplorer = new RandomExplorer((SetExperimentSpec) experimentSpec, Drivers.remoteDriver(), path);
    }

    @Override
    public void setRunState(RunState runState) {
        randomExplorer.setRunState(runState);
    }

    @Override
    protected Void call() throws Exception {
        randomExplorer.explore();

        return null;
    }
}
