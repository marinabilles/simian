package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.GuidedStateSpaceExplorer;
import de.crispda.sola.multitester.RunState;
import de.crispda.sola.multitester.web.Drivers;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.xml.transform.TransformerConfigurationException;
import java.util.logging.Logger;

public class GuidedExperimentTask extends ExperimentTask {
    private final SetExperimentSpec experimentSpec;
    private final int depth;
    private final GuidedStateSpaceExplorer explorer;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public GuidedExperimentTask(SetExperimentSpec experimentSpec, String path, int depth)
            throws TransformerConfigurationException {
        this.experimentSpec = experimentSpec;
        this.depth = depth;
        explorer = new GuidedStateSpaceExplorer(experimentSpec, Drivers.remoteDriver(), path);
    }

    @Override
    protected Void call() throws Exception {
        try {
            logger.info("Running " + experimentSpec.getName());
            logger.info("Depth: " + Integer.toString(depth));
            explorer.explore(depth);
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
