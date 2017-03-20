package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.ProbabilisticExplorer;
import de.crispda.sola.multitester.RunState;
import de.crispda.sola.multitester.SeleniumRunner;
import de.crispda.sola.multitester.web.Drivers;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.xml.transform.TransformerConfigurationException;
import java.time.Duration;
import java.util.logging.Logger;

public class ProbabilisticExperimentTask extends ExperimentTask {
    private final ProbabilisticExplorer explorer;
    private final SetExperimentSpec experimentSpec;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ProbabilisticExperimentTask(SetExperimentSpec experimentSpec, String path)
            throws TransformerConfigurationException {
        this.experimentSpec = experimentSpec;
        explorer = new ProbabilisticExplorer(experimentSpec.interactionSet,
                new SeleniumRunner(path, Drivers.remoteDriver(), experimentSpec.init,
                        experimentSpec.exclusionRectangles, experimentSpec.neutralEvents, true),
                experimentSpec.combinator);
    }

    @Override
    protected Void call() throws Exception {
        try {
            logger.info("Running " + experimentSpec.getName());
            explorer.explore(10, Duration.ofDays(7));
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
