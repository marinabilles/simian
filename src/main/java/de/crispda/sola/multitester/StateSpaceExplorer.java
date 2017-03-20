package de.crispda.sola.multitester;

import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.util.XMLFile;
import de.crispda.sola.multitester.web.DriverSupplier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.ScreenshotException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static de.crispda.sola.multitester.util.XMLFile.createElement;

abstract class StateSpaceExplorer extends Stoppable {
    protected final SetExperimentSpec experimentSpec;
    protected final List<Interaction> interactions;
    protected String path;
    protected final ScheduleVariantExecutor scheduleVariantExecutor;
    protected final boolean saveFiles;
    protected static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected int differenceId = 0;
    protected int errorId = 0;
    protected int runId = 0;
    protected final Transformer transformer;
    protected boolean useZip = true;
    protected boolean lastFailed = false;

    protected StateSpaceExplorer(SetExperimentSpec experimentSpec, DriverSupplier driverSupplier,
                                 String path) throws TransformerConfigurationException {
        this.experimentSpec = experimentSpec;
        interactions = new ArrayList<>(experimentSpec.interactionSet);

        scheduleVariantExecutor = new ScheduleVariantExecutor(driverSupplier, experimentSpec.exclusionRectangles,
                new ArrayList<>());
        scheduleVariantExecutor.setInit(experimentSpec.init);

        if (path != null) {
            this.path = path;
            saveFiles = true;
        } else {
            saveFiles = false;
        }
        transformer = getTransformer();
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        return transformer;
    }

    public List<Optional<byte[]>> parallelRun(ExplorationState state) throws InterruptedException {
        logger.info("Exploring in parallel: " + state);
        List<Interaction> firstInteractions = new ArrayList<>();
        List<Interaction> secondInteractions = new ArrayList<>();

        int nextFirst = 1;
        int nextSecond = 1;
        ArrayDeque<MaybeWait> firstSchedule = new ArrayDeque<>();
        ArrayDeque<MaybeWait> secondSchedule = new ArrayDeque<>();
        for (ExplorationStep step : state) {
            if (step instanceof GuidedStateSpaceExplorer.SequentialStep) {
                firstInteractions.add(((GuidedStateSpaceExplorer.SequentialStep) step).interaction);
                firstSchedule.add(new MaybeWait(nextFirst));
                nextFirst = 0;
            } else if (step instanceof GuidedStateSpaceExplorer.ParallelStep) {
                if (experimentSpec.sender != null) {
                    firstInteractions.add(experimentSpec.sender);
                    secondInteractions.add(experimentSpec.receiver);
                    firstSchedule.add(new MaybeWait(nextFirst + 1));
                    secondSchedule.add(new MaybeWait(nextSecond + 1));
                    nextFirst = 0;
                    nextSecond = 0;
                }

                firstInteractions.add(((GuidedStateSpaceExplorer.ParallelStep) step).first);
                secondInteractions.add(((GuidedStateSpaceExplorer.ParallelStep) step).second);
                firstSchedule.add(new MaybeWait(nextFirst + 1));
                secondSchedule.add(new MaybeWait(nextSecond + 1));
                nextFirst = 1;
                nextSecond = 1;
            }
        }
        firstSchedule.add(new MaybeWait(nextFirst + 1));
        secondSchedule.add(new MaybeWait(nextSecond + 1));

//        if (experimentSpec.combinator instanceof Adaptable) {
//            ((Adaptable) experimentSpec.combinator).setDebugAdapter(new LogDebugAdapter(1));
//        }
        CombinedTest first = experimentSpec.combinator.combine(firstInteractions);
//        if (experimentSpec.combinator instanceof Adaptable) {
//            ((Adaptable) experimentSpec.combinator).setDebugAdapter(new LogDebugAdapter(2));
//        }
        CombinedTest second = experimentSpec.combinator.combine(secondInteractions);
        MultiTest<CombinedTest> multiTest = new MultiTest<>(first, second);
        ScheduleVariant<CombinedTest> variant = new ScheduleVariant<>(multiTest, firstSchedule, secondSchedule);

        int attemptCount = 0;
        boolean succeeded = false;
        while (attemptCount < 5 && !succeeded) {
            if (isStopped())
                break;

            try {
                attemptCount++;
                scheduleVariantExecutor.executeTest(variant);
                succeeded = true;
            } catch (Exception e) {
                if (saveFiles) {
                    recordError(e);
                }
                errorId++;

                if (!first.getTestFailures().isEmpty() || !second.getTestFailures().isEmpty()) {
                    succeeded = true;
                    recordFailure(state, first, second);
                    differenceId++;
                }
            }
        }

        if (attemptCount >= 5 && !succeeded)
            logger.warning("Giving up on parallel run " + state);


        List<byte[]> firstScrList = first.getScreenshotList();
        List<byte[]> secondScrList = second.getScreenshotList();
        List<Optional<byte[]>> result = new ArrayList<>();
        if (firstScrList != null && secondScrList != null) {
            if (!firstScrList.isEmpty() && firstScrList.size() == secondScrList.size()) {
                int firstIndex = firstScrList.size() - 1;
                int secondIndex = secondScrList.size() - 1;
                if (!experimentSpec.neutralEvents.isEmpty()) {
                    firstIndex--;
                    secondIndex--;
                }

                int loopIndex = 0;
                lastFailed = false;
                while (firstIndex < firstScrList.size() && secondIndex < secondScrList.size()) {
                    byte[] scrFirst = firstScrList.get(firstIndex);
                    byte[] scrSecond = secondScrList.get(secondIndex);
                    if (!saveFiles) {
                        result.add(Optional.of(scrFirst));
                        result.add(Optional.of(scrSecond));
                    }
                    try {
                        ImageDiff diff = new ImageDiff(scrFirst, scrSecond, experimentSpec.exclusionRectangles);
                        if (diff.hasDifference()) {
                            recordFailure(state);
                            lastFailed = true;
                            if (saveFiles) {
                                FileUtils.writeByteArrayToFile(
                                        new File(String.format("%s/%d_%d_f.png", path, runId, loopIndex)), scrFirst);
                                FileUtils.writeByteArrayToFile(
                                        new File(String.format("%s/%d_%d_s.png", path, runId, loopIndex)), scrSecond);
                            }
                            differenceId++;
                        }
                    } catch (IOException | ImageDimensionException ignored) {
                    }

                    firstIndex++;
                    secondIndex++;
                    loopIndex++;
                }
            }
        }

        return result;
    }

    private void recordError(Exception e) {
        if (!saveFiles) {
            e.printStackTrace();
            return;
        }
        try {
            if (!Paths.exists(path))
                throw new IOException("Path " + path + " not found");

            XMLFile xmlFile = new XMLFile(path + "/errors.xml", "errors");
            Document doc = xmlFile.document;
            Element errorEl = doc.createElement("error");
            xmlFile.root.appendChild(errorEl);

            errorEl.appendChild(createElement(doc, "runId", Integer.toString(runId)));
            errorEl.appendChild(createElement(doc, "errorId", Integer.toString(errorId)));
            errorEl.appendChild(createElement(doc, "stackTrace", ExceptionUtils.getStackTrace(e)));
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile.log));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            Throwable cause = e;
            while (cause != null) {
                Throwable prevCause = cause;
                cause = cause.getCause();
                if (cause == prevCause)
                    break;
                if (cause instanceof ScreenshotException && Paths.exists(path)) {
                    String errorScr = ((ScreenshotException) cause).getBase64EncodedScreenshot();
                    byte[] errorScrBytes = OutputType.BYTES.convertFromBase64Png(errorScr);
                    FileUtils.writeByteArrayToFile(
                            new File(String.format("%s/error%d_%d.png", path, runId, errorId)),
                            errorScrBytes);
                    break;
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void recordFailure(ExplorationState state) {
        if (!saveFiles)
            return;
        try {
            if (!Paths.exists(path))
                throw new IOException("Path " + path + " not found");

            XMLFile xmlFile = new XMLFile(path + "/failures.xml", "failures");
            Document doc = xmlFile.document;
            Element failureEl = doc.createElement("failure");
            xmlFile.root.appendChild(failureEl);

            failureEl.appendChild(createElement(doc, "runId", Integer.toString(runId)));
            failureEl.appendChild(createElement(doc, "failureId", Integer.toString(differenceId)));
            failureEl.appendChild(createElement(doc, "sequence", state.toString()));
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile.log));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recordFailure(ExplorationState state, Test first, Test second) {
        if (!saveFiles)
            return;

        try {
            String formatString = path + "/" + Integer.toString(runId) + "_%s%d.png";
            FailureRecorder.saveFiles(first, second, formatString);

            FailureReason fr = first.getFailureReason();
            int threadId = 0;
            int differenceThreadId;
            if (fr != null) {
                differenceThreadId = 1;
                threadId = fr.executingThread ? 1 : 2;
            } else {
                differenceThreadId = 2;
                fr = second.getFailureReason();
                if (fr != null) {
                    threadId = fr.executingThread ? 2 : 1;
                }
            }

            if (threadId > 0) {
                XMLFile xmlFile = new XMLFile(path + "/failures.xml", "failures");
                Document doc = xmlFile.document;
                Element failureEl = doc.createElement("failure");
                xmlFile.root.appendChild(failureEl);

                failureEl.appendChild(createElement(doc, "runId", Integer.toString(runId)));
                failureEl.appendChild(createElement(doc, "failureId", Integer.toString(differenceId)));
                failureEl.appendChild(createElement(doc, "sequence", state.toString()));

                failureEl.appendChild(createElement(doc, "executingThreadId", Integer.toString(threadId)));
                failureEl.appendChild(createElement(doc, "differenceThreadId", Integer.toString(differenceThreadId)));

                Boolean wasFirstNeutralEvent;
                if (threadId == 1) {
                    wasFirstNeutralEvent = first.getWasFirst();
                } else {
                    wasFirstNeutralEvent = second.getWasFirst();
                }

                failureEl.appendChild(createElement(doc, "wasFirstNeutralEvent",
                        Boolean.toString(wasFirstNeutralEvent)));
                failureEl.appendChild(createElement(doc, "neutralEventName", fr.neutralEventName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
