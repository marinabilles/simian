package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.web.DriverSupplier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;

import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This explorer generates random interactions based on two threads each executing a
 * different random number of interactions and a random schedule between the two threads.
 * This is different from {@link ProbabilisticExplorer} which executes a deterministic
 * schedule of randomly generated actions, and {@link RandomStateSpaceExplorer}, which
 * generates multi-client interactions of a sequential prefix and concurrent suffix.
 */
public class RandomExplorer extends Stoppable {
    private final SetExperimentSpec experimentSpec;
    private final DriverSupplier driverSupplier;
    private final String path;
    private static final int MAX_INTERACTION_LENGTH = 10;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public RandomExplorer(SetExperimentSpec experimentSpec, DriverSupplier supplier, String path) {
        this.experimentSpec = experimentSpec;
        driverSupplier = supplier;
        this.path = path;
        runState = RunState.STOPPED;
    }

    public void explore() throws InterruptedException, TransformerConfigurationException {
        runState = RunState.RUNNING;
        logger.info("Random Exploration");

        FailureRecorder failureRecorder = new FailureRecorder(path);
        int runCount = 0;

        while (!isStopped()) {
            logger.info("Run " + runCount);
            CombinedTest first = createTest(1);
            CombinedTest second = createTest(2);
            WebDriver firstDriver = driverSupplier.get(1);
            WebDriver secondDriver = driverSupplier.get(2);

            CyclicBarrier barrier = new CyclicBarrier(2);
            Exchanger<Boolean> exchanger = new Exchanger<>();

            double singleProp = 1.0d / (MAX_INTERACTION_LENGTH + 1);
            int maybeWaitCount = 0;
            double randomValue = Math.random();
            while (randomValue >= (maybeWaitCount + 1) * singleProp) {
                maybeWaitCount++;
            }

            ArrayDeque<MaybeWait> firstSchedule = createSchedule(maybeWaitCount);
            ArrayDeque<MaybeWait> secondSchedule = createSchedule(maybeWaitCount);
            MultiTest<CombinedTest> multiTest = new MultiTest<>(first, second);
            ScheduleVariant<CombinedTest> variant = new ScheduleVariant<>(multiTest, firstSchedule, secondSchedule);
            logger.info("Schedule Client 1: " + printSchedule(firstSchedule));
            logger.info("Schedule Client 2: " + printSchedule(secondSchedule));

            first.setup(barrier, firstDriver, firstSchedule, exchanger, experimentSpec.exclusionRectangles,
                    experimentSpec.neutralEvents);
            second.setup(barrier, secondDriver, secondSchedule, exchanger, experimentSpec.exclusionRectangles,
                    experimentSpec.neutralEvents);
            first.setNeutralEventsAtEnd(true);
            second.setNeutralEventsAtEnd(true);
            firstDriver.get(first.getInitialURL());
            secondDriver.get(second.getInitialURL());
            if (experimentSpec.init != null) {
                experimentSpec.init.run(firstDriver);
            }

            List<Test> tests = Lists.newArrayList(first, second);
            try {
                Tests.executeConcurrently(tests);

            } catch (InterruptedException | ExecutionException e) {
                failureRecorder.recordError(firstDriver, secondDriver, 0, e);
                if (first.getTestFailures().size() == 0 && second.getTestFailures().size() == 0) {
                    logger.severe(ExceptionUtils.getStackTrace(e));
                } else  {
                    failureRecorder.recordFailures(runCount, variant, first, second);
                }
            } finally {
                tests.forEach(Test::cleanUp);
                if (Paths.exists(path)) {
                    try {
                        List<List<byte[]>> screenshots = tests.stream()
                                .map(Test::getScreenshotList).collect(Collectors.toList());
                        Optional<byte[]> firstScr = Optional.empty();
                        Optional<byte[]> secondScr = Optional.empty();

                        if (!screenshots.get(0).isEmpty()) {
                            byte[] scr = screenshots.get(0).get(screenshots.get(0).size() - 1);
                            firstScr = Optional.of(scr);
                            FileUtils.writeByteArrayToFile(
                                    new File(String.format("%s/%d_f.png", path, runCount)), scr);
                        }
                        if (!screenshots.get(1).isEmpty()) {
                            byte[] scr = screenshots.get(1).get(screenshots.get(1).size() - 1);
                            secondScr = Optional.of(scr);
                            FileUtils.writeByteArrayToFile(
                                    new File(String.format("%s/%d_s.png", path, runCount)), scr);
                        }
                        if (firstScr.isPresent() && secondScr.isPresent()) {
                            boolean hasDifference;
                            try {
                                ImageDiff diff = Images.getDiff(firstScr.get(), secondScr.get(),
                                        experimentSpec.exclusionRectangles);
                                hasDifference = diff.hasDifference();
                            } catch (ImageDimensionException e) {
                                hasDifference = true;
                            }

                            if (hasDifference) {
                                failureRecorder.recordVariantFailure(runCount, variant, 0, "Client state comparison");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                runCount++;
            }
        }
    }

    private CombinedTest createTest(int threadIndex) {
        List<Interaction> interactions = new ArrayList<>();
        for (int i = 0; i < MAX_INTERACTION_LENGTH; i++) {
            double singleProp =  1.0d / experimentSpec.interactionSet.size();
            Iterator<Interaction> it = experimentSpec.interactionSet.iterator();
            int index = 1;
            double randomValue = Math.random();
            while (randomValue >= index * singleProp) {
                it.next();
                index++;
            }

            interactions.add(it.next());
        }

        StringBuilder sb = new StringBuilder("Thread ").append(threadIndex).append(": ");
        for (Interaction act : interactions) {
            sb.append(act).append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        logger.info(sb.toString());

        return experimentSpec.combinator.combine(interactions);
    }

    private ArrayDeque<MaybeWait> createSchedule(int maybeWaitCount) {
        List<MaybeWait> maybeWaitList = new ArrayList<>();
        IntStream.rangeClosed(0, MAX_INTERACTION_LENGTH).forEach(i -> maybeWaitList.add(new MaybeWait(0)));
        double singleProp = 1.0d / (MAX_INTERACTION_LENGTH + 1);
        for (int i = 0; i < maybeWaitCount; i++) {
            int index = 0;
            double randomValue = Math.random();
            while (randomValue >= (index + 1) * singleProp) {
                index++;
            }

            MaybeWait mw = maybeWaitList.get(index);
            mw.setWaitCount(mw.getWaitCount() + 1);
        }

        return new ArrayDeque<>(maybeWaitList);
    }

    private String printSchedule(Deque<MaybeWait> schedule) {
        StringBuilder sb = new StringBuilder();
        schedule.forEach(mw -> sb.append(mw).append(" "));
        return sb.toString();
    }
}
