package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.web.DriverSupplier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;

import javax.xml.transform.TransformerConfigurationException;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SeleniumRunner {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final DriverSupplier driverSupplier;
    private final String path;
    private final boolean saveFiles;
    private boolean useNeutralEvents;
    private List<Rectangle> exclusionRectangles;
    private List<Interaction> neutralEvents;
    private TestInit init;
    private FailureRecorder failureRecorder;
    private int runCount = 0;

    public SeleniumRunner(String path, DriverSupplier driverSupplier) throws TransformerConfigurationException {
        this.path = path;
        saveFiles = true;
        this.driverSupplier = driverSupplier;
        exclusionRectangles = new ArrayList<>();
        neutralEvents = new ArrayList<>();
        useNeutralEvents = false;
        failureRecorder = new FailureRecorder(path);
    }

    public SeleniumRunner(String path, DriverSupplier driverSupplier, TestInit init,
                          List<Rectangle> exclusionRectangles, List<Interaction> neutralEvents,
                          boolean useNeutralEvents)
            throws TransformerConfigurationException {
        this(path, driverSupplier);
        this.init = init;
        this.exclusionRectangles = exclusionRectangles;
        this.neutralEvents = neutralEvents;
        this.useNeutralEvents = useNeutralEvents;
    }

    public SeleniumRunner(DriverSupplier driverSupplier, TestInit init,
                          List<Rectangle> exclusionRectangles, List<Interaction> neutralEvents,
                          boolean useNeutralEvents) {
        this.path = "";
        saveFiles = false;
        this.driverSupplier = driverSupplier;
        this.init = init;
        this.exclusionRectangles = exclusionRectangles;
        this.neutralEvents = neutralEvents;
        this.useNeutralEvents = useNeutralEvents;
    }


    public Optional<List<List<ImageDiff>>> runSequence(final TransitionSequence sequence, final TestSpec t,
                                                    boolean saveScreenshots)
            throws InterruptedException, IOException, ImageDimensionException {
        Optional<List<List<byte[]>>> screenshots = runSequence(sequence, t, saveScreenshots, useNeutralEvents);
        if (!screenshots.isPresent()) {
            logger.info("Returning full independence map after neutral event violation");
            return Optional.empty();
        }

        List<byte[]> firstScreens = screenshots.get().get(0);
        List<byte[]> secondScreens = screenshots.get().get(1);
        if (sequence.size() != firstScreens.size() - 1 || sequence.size() != secondScreens.size() - 1) {
            logger.warning("Error occurred during independence inferring. Returning full independence map.");
            return Optional.empty();
        }

        List<List<ImageDiff>> sequenceDiffs = new ArrayList<>();
        for (int index = 0; index < sequence.size(); index++) {
            byte[] firstBefore = firstScreens.get(index);
            byte[] firstAfter = firstScreens.get(index + 1);
            byte[] secondBefore = secondScreens.get(index);
            byte[] secondAfter = secondScreens.get(index + 1);

            sequenceDiffs.add(Lists.newArrayList(
                    Images.getDiff(firstBefore, firstAfter, exclusionRectangles),
                    Images.getDiff(secondBefore, secondAfter, exclusionRectangles)));
        }

        return Optional.of(sequenceDiffs);
    }

    public Optional<List<List<byte[]>>> runSequence(final TransitionSequence sequence, final TestSpec t,
                                                    boolean saveScreenshots, boolean useNeutralEvents)
            throws InterruptedException {
        ScheduleVariant<?> variant = t.createVariant(sequence);
        Optional<List<List<byte[]>>> screenshots = runVariant(sequence, variant, t, saveScreenshots, useNeutralEvents);
        if (saveScreenshots) {
            runCount++;
            failureRecorder.reset();
        }
        return screenshots;
    }

    private Optional<List<List<byte[]>>> runVariant(final TransitionSequence sequence, final ScheduleVariant<?> variant,
                                                    final TestSpec testSpec, boolean saveScreenshots,
                                                    boolean useNeutralEvents)
            throws InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final Exchanger<Boolean> exchanger = new Exchanger<>();
        final Test first = variant.getFirstTest();
        final Test second = variant.getSecondTest();

        final WebDriver firstDriver = driverSupplier.get(1);
        final WebDriver secondDriver = driverSupplier.get(2);
        firstDriver.get(first.getInitialURL());
        secondDriver.get(second.getInitialURL());

        Optional<TestInit> maybeInit = testSpec.getInit();
        if (maybeInit.isPresent()) {
            maybeInit.get().run(firstDriver);
        } else if (init != null) {
            init.run(firstDriver);
        }

        if (useNeutralEvents) {
            first.setup(barrier, firstDriver, variant.getFirstSchedule(), exchanger, exclusionRectangles,
                    neutralEvents);
            second.setup(barrier, secondDriver, variant.getSecondSchedule(), exchanger, exclusionRectangles,
                    neutralEvents);
        } else {
            first.setup(barrier, firstDriver, variant.getFirstSchedule(), exchanger);
            second.setup(barrier, secondDriver, variant.getSecondSchedule(), exchanger);
        }

        final List<Test> tests = Lists.newArrayList(first, second);
        List<List<byte[]>> screenshots;
        boolean failed = false;

        try {
            Tests.executeConcurrently(tests);
        } catch (InterruptedException | ExecutionException e) {
            if (saveFiles) {
                failureRecorder.recordError(firstDriver, secondDriver, runCount, e);
            }

            if (first.getTestFailures().size() == 0 && second.getTestFailures().size() == 0) {
                logger.severe(ExceptionUtils.getStackTrace(e));
            } else if (saveFiles) {
                failed = true;
                failureRecorder.recordFailures(runCount, sequence, first, second);
            }
        } finally {
            tests.forEach(Test::cleanUp);

            screenshots = tests.stream().map(Test::getScreenshotList).collect(Collectors.toList());
            if (saveFiles && saveScreenshots && Paths.exists(path)) {
                int reachedState = Math.min(sequence.size(), Math.min(screenshots.get(0).size() - 1,
                        screenshots.get(1).size() - 1));

                for (int index = 0; index < reachedState; index++) {
                    byte[] firstBefore = screenshots.get(0).get(index);
                    byte[] firstAfter = screenshots.get(0).get(index + 1);
                    byte[] secondBefore = screenshots.get(1).get(index);
                    byte[] secondAfter = screenshots.get(1).get(index + 1);

                    try {
                        if (index == 0) {
                            FileUtils.writeByteArrayToFile(
                                    new File(String.format("%s/%d_f%d.png", path, runCount, index)), firstBefore);
                            FileUtils.writeByteArrayToFile(
                                    new File(String.format("%s/%d_s%d.png", path, runCount, index)), secondBefore);
                        }
                        FileUtils.writeByteArrayToFile(
                                new File(String.format("%s/%d_f%d.png", path, runCount, index + 1)), firstAfter);
                        FileUtils.writeByteArrayToFile(
                                new File(String.format("%s/%d_s%d.png", path, runCount, index + 1)), secondAfter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (failed)
            return Optional.empty();
        else
            return Optional.of(screenshots);
    }
}
