package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.GuidedStateSpaceExplorer;
import de.crispda.sola.multitester.RunState;
import de.crispda.sola.multitester.runner.ExperimentSpec;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.web.Drivers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

public class ExploreDiffMap {
    public static void main(String[] args) throws IOException, TransformerConfigurationException,
            DataFormatException, InterruptedException, ExecutionException {
        if (args.length < 3)
            return;

        ExperimentSpec spec = SerializationUtils.deserialize(
                FileUtils.readFileToByteArray(new File(args[0])));

        if (!(spec instanceof SetExperimentSpec)) {
            System.err.println("Not a SetExperimentSpec! Exiting.");
            return;
        }

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        FileHandler handler = new FileHandler("./redone.xml", true);
        logger.addHandler(handler);

        GuidedStateSpaceExplorer explorer = new GuidedStateSpaceExplorer(
                (SetExperimentSpec) spec, Drivers.remoteDriver(), ".");
        explorer.loadDiffMap(args[1]);
        explorer.setRunState(RunState.RUNNING);
        explorer.parallelExplorationStep(Integer.parseInt(args[2]));
    }
}
