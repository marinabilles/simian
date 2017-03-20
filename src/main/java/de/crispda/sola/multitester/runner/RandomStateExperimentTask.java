package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.RandomStateSpaceExplorer;
import de.crispda.sola.multitester.RunState;
import de.crispda.sola.multitester.web.Drivers;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.xml.transform.TransformerConfigurationException;
import java.time.Duration;
import java.util.logging.Logger;

public class RandomStateExperimentTask extends ExperimentTask {
    private final SetExperimentSpec experimentSpec;
    private final int depth;
    private final RandomStateSpaceExplorer explorer;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Duration duration;

    public RandomStateExperimentTask(SetExperimentSpec experimentSpec, String path, int depth, Duration duration)
            throws TransformerConfigurationException {
        this.experimentSpec = experimentSpec;
        this.depth = depth;
        this.duration = duration;
        explorer = new RandomStateSpaceExplorer(experimentSpec, Drivers.remoteDriver(), path);
    }

    @Override
    protected Void call() throws Exception {
        try {
            logger.info("Running " + experimentSpec.getName());
            logger.info("Depth: " + Integer.toString(depth));
            explorer.explore(depth, duration);
        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public void setRunState(RunState runState) {
        explorer.setRunState(runState);
    }
}
