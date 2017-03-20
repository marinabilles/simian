package de.crispda.sola.multitester;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.util.DatabaseConnector;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.util.ZipUtils;
import de.crispda.sola.multitester.web.DriverSupplier;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import javax.xml.transform.TransformerConfigurationException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

/**
 * The two-phase exploration approach, "Simian".
 * The entry point to the analysis is the explore method
 */
public class GuidedStateSpaceExplorer extends StateSpaceExplorer {
    private final SingleThreadedExecutor singleThreadedExecutor;
    private final MapMap<ExplorationState, Interaction, DiffWrapper> diffMap = new MapMap<>();
    private final HashMap<ExplorationState, byte[]> stateScreenshotMap = new HashMap<>();
    private final SetMap<ExplorationState, UnorderedPair<Interaction>> testMap = new SetMap<>();

    /**
     * @param experimentSpec    provides the URL, list of actions and combinator function for
     *                          the website in question
     * @param driverSupplier    provides the browser startup and exit functionality
     * @param path              folder where results for this execution are saved
     * @throws TransformerConfigurationException
     */
    public GuidedStateSpaceExplorer(SetExperimentSpec experimentSpec,
                                    DriverSupplier driverSupplier, String path)
            throws TransformerConfigurationException {
        super(experimentSpec, driverSupplier, path);
        singleThreadedExecutor = new SingleThreadedExecutor(driverSupplier);
        singleThreadedExecutor.setInit(experimentSpec.init);
        singleThreadedExecutor.setMaybeWaitCount(1);
    }

    public GuidedStateSpaceExplorer(SetExperimentSpec experimentSpec, DriverSupplier driverSupplier)
            throws TransformerConfigurationException {
        this(experimentSpec, driverSupplier, null);
    }

    /**
     * Explores the state space of multi-client sequences up to length {@code depth} and saves
     * results to the specified path
     * @param depth     the maximum length of explored multi-client sequences
     * @throws InterruptedException
     */
    public void explore(int depth) throws InterruptedException, ExecutionException {
        // First step: sequential testing
        // start off with currentState as the left-most branch of the exploration tree
        ExplorationState currentState = new ExplorationState();
        for (int i = 0; i < depth; i++) {
            currentState.add(new SequentialStep(interactions.get(0)));
        }

        logger.info("Sequentially exploring");
        runState = RunState.RUNNING;
        boolean incremented;
        do {
            if (isStopped())
                return;

            // intermediate results of sequential explorations are saved in diffMap and stateScreenshotMap
            sequentialExplore(currentState);
            ExplorationState newState = new ExplorationState();
            incremented = false;
            for (int i = depth - 1; i >= 0; i--) {
                if (incremented) {
                    newState.add(currentState.get(i));
                } else {
                    Interaction act = ((SequentialStep) currentState.get(i)).interaction;
                    int indexOf = interactions.indexOf(act);
                    if (indexOf == interactions.size() - 1) {
                        newState.add(new SequentialStep(interactions.get(0)));
                    } else {
                        newState.add(new SequentialStep(interactions.get(indexOf + 1)));
                        incremented = true;
                    }
                }
            }

            if (incremented) {
                Collections.reverse(newState);
                currentState = newState;
            }
        } while (incremented);

        // persist maps to the hard drive
        if (saveFiles && Paths.exists(path)) {
            try {
                FileUtils.writeByteArrayToFile(new File(path + "/diffmap.zip"),
                        ZipUtils.zip(SerializationUtils.serialize(diffMap)));
                FileUtils.writeByteArrayToFile(new File(path + "/diffmap.zip.scr"),
                        ZipUtils.zip(SerializationUtils.serialize(stateScreenshotMap)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Second step: infer overlapping interactions
        inferOverlaps(diffMap, stateScreenshotMap, experimentSpec.exclusionRectangles).forEach(testMap::put);

        parallelExplorationStep(depth);
    }

    public void loadDiffMap(String diffMapFilename) throws IOException, DataFormatException, InterruptedException,
            ExecutionException {
        diffMap.putAll(
                SerializationUtils.<MapMap<ExplorationState, Interaction, DiffWrapper>>deserialize(
                        ZipUtils.unzip(FileUtils.readFileToByteArray(new File(diffMapFilename)))));
        stateScreenshotMap.putAll(
                SerializationUtils.<HashMap<ExplorationState, byte[]>>deserialize(
                        ZipUtils.unzip(FileUtils.readFileToByteArray(new File(diffMapFilename + ".scr")))));

        inferOverlaps(diffMap, stateScreenshotMap, experimentSpec.exclusionRectangles).forEach(testMap::put);
        logger.info("Loaded " + testMap.entrySet().stream()
                .mapToInt(e -> e.getValue().size()).sum() + " parallel executions to test.");
    }

    public void withTestMap(SetMap<ExplorationState, UnorderedPair<Interaction>> setMap) {
        setMap.forEach(testMap::put);
    }

    public void parallelExplorationStep(int depth) throws InterruptedException {
        // Third step: explore conflicting cases
        List<Map.Entry<ExplorationState, Set<UnorderedPair<Interaction>>>> entryList =
                new ArrayList<>(testMap.entrySet());
        StateDepthComparator comparator = new StateDepthComparator();
        entryList.sort(comparator);

        try {
            for (Map.Entry<ExplorationState, Set<UnorderedPair<Interaction>>> entry : entryList) {
                if (isStopped())
                    return;

                ExplorationState state = entry.getKey();
                if (!testMap.containsKey(state)) {
                    continue;
                }

                while (testMap.containsKey(state)) {
                    parallelExplore(depth, state, 1);
                    runId++;
                }
            }

            logger.info("Done exploring.");
        } finally {
            DatabaseConnector.close();
        }
    }

    private class StateDepthComparator implements
            Comparator<Map.Entry<ExplorationState, Set<UnorderedPair<Interaction>>>> {
        @Override
        public int compare(Map.Entry<ExplorationState, Set<UnorderedPair<Interaction>>> entry1,
                           Map.Entry<ExplorationState, Set<UnorderedPair<Interaction>>> entry2) {
            if (entry1.getKey().size() < entry2.getKey().size()) {
                return -1;
            } else if (entry1.getKey().size() > entry2.getKey().size()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static SetMap<ExplorationState, UnorderedPair<Interaction>> inferOverlaps(
            MapMap<ExplorationState, Interaction, DiffWrapper> diffMap,
            Map<ExplorationState, byte[]> stateScreenshotMap,
            List<Rectangle> exclusionRectangles) throws InterruptedException, ExecutionException {

        // find equivalent states
        Set<UnorderedPair<ExplorationState>> stateEquivalenceSet = inferEquivalentStates(stateScreenshotMap,
                exclusionRectangles);

        SetMap<ExplorationState, UnorderedPair<Interaction>> testMap = new SetMap<>();
        SetMap<UnorderedPair<Interaction>, ExplorationState> reverseTestMap = new SetMap<>();

        // Iterate over all exploration states
        Iterator<Map.Entry<ExplorationState, Map<Interaction, DiffWrapper>>> diffMapIt =
                diffMap.entrySet().iterator();
        while (diffMapIt.hasNext()) {
            Map.Entry<ExplorationState, Map<Interaction, DiffWrapper>> entry = diffMapIt.next();
            ExplorationState state = entry.getKey();

            // DiffWrapper objects have been zipped upon insertion into diffMap. Here we need to
            // unzip them to prevent UnsupportedOperationException upon accessing the contained ImageDiff.
            entry.getValue().values().forEach(DiffWrapper::unzip);

            // In each state, an interaction is mapped to its diff
            // iterate over PAIRS of map entries
            List<Map.Entry<Interaction, DiffWrapper>> pairs = new ArrayList<>(entry.getValue().entrySet());
            for (int i = 0; i < pairs.size(); i++) {
                Map.Entry<Interaction, DiffWrapper> first = pairs.get(i);
                for (int j = i + 1; j < pairs.size(); j++) {
                    Map.Entry<Interaction, DiffWrapper> second = pairs.get(j);
                    UnorderedPair<Interaction> pair = new UnorderedPair<>(first.getKey(), second.getKey());
                    // in case we saw this pair of interactions already in an equivalent state, skip it here
                    Optional<Set<ExplorationState>> otherStates = Optional.ofNullable(reverseTestMap.get(pair));
                    if (otherStates.isPresent()) {
                        if (otherStates.get().stream().anyMatch(otherState ->
                                stateEquivalenceSet.contains(new UnorderedPair<>(state, otherState)))) {
                            continue;
                        }
                    }

                    if (first.getValue() == null || second.getValue() == null ||
                            first.getValue().get().overlapsWith(second.getValue().get())) {
                        if (Optional.ofNullable(testMap.get(state)).map(s -> !s.contains(pair)).orElse(true)) {
                            if (state.stream().allMatch(stp -> stp instanceof SequentialStep)) {
                                logger.info("At state " + state.stream().map(stp -> ((SequentialStep) stp).interaction)
                                        .collect(Collectors.toList()) + ": adding (" + first.getKey() + ", " +
                                        second.getKey());
                            }
                            testMap.add(state, pair);
                            reverseTestMap.add(pair, state);
                        }
                    }
                }
            }

            // Consume the list while iterating over it to free space
            diffMapIt.remove();
        }

        return testMap;
    }

    public static Set<UnorderedPair<ExplorationState>> inferEquivalentStates(
            Map<ExplorationState, byte[]> stateScreenshotMap,
            List<Rectangle> exclusionRectangles) throws InterruptedException, ExecutionException {
        List<Pair<ExplorationState, Img<ARGBType>>> screenshotList = stateScreenshotMap.entrySet()
                .stream().map(e ->
                        new ImmutablePair<>(e.getKey(), convertScreenshot(e.getValue())))
                .collect(Collectors.toList());

        stateScreenshotMap.clear();

        ExecutorService stateInferEx = Executors.newWorkStealingPool();
        ListeningExecutorService listeningStateInferEx = MoreExecutors.listeningDecorator(stateInferEx);

        List<ListenableFuture<UnorderedPair<ExplorationState>>> futureList = new ArrayList<>();
        for (int i = 0; i < screenshotList.size(); i++) {
            for (int j = i + 1; j < screenshotList.size(); j++) {
                futureList.add(listeningStateInferEx.submit(new StateInferenceCallable(screenshotList.get(i),
                        screenshotList.get(j), exclusionRectangles)));
            }
        }

        screenshotList.clear();

        List<UnorderedPair<ExplorationState>> result = Futures.allAsList(futureList).get();

        return result.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private static class StateInferenceCallable implements Callable<UnorderedPair<ExplorationState>> {
        private Pair<ExplorationState, Img<ARGBType>> first;
        private Pair<ExplorationState, Img<ARGBType>> second;
        private List<Rectangle> exclusionRectangles;

        StateInferenceCallable(Pair<ExplorationState, Img<ARGBType>> first,
                               Pair<ExplorationState, Img<ARGBType>> second,
                               List<Rectangle> exclusionRectangles) {
            this.first = first;
            this.second = second;
            this.exclusionRectangles = exclusionRectangles;
        }

        @Override
        public UnorderedPair<ExplorationState> call() throws Exception {
            ImageDiff diff = new ImageDiff(first.getValue(), second.getValue(), exclusionRectangles);
            if (!diff.hasDifference()) {
                logger.info("Inferred equivalence between " + first.getKey() + " and " + second.getKey());
                return new UnorderedPair<>(first.getKey(), second.getKey());
            }
            return null;
        }
    }

    public void exhaustiveExplore(int depth) throws InterruptedException {
        runState = RunState.RUNNING;
        try {
            for (int currentDepth = 0; currentDepth < depth; currentDepth++) {
                if (isStopped())
                    return;

                ExplorationState currentState = new ExplorationState();
                for (int prefixIndex = 0; prefixIndex < currentDepth; prefixIndex++) {
                    currentState.add(new SequentialStep(interactions.get(0)));
                }

                boolean incremented;
                do {
                    for (int i = 0; i < interactions.size(); i++) {
                        for (int j = i + 1; j < interactions.size(); j++) {
                            if (isStopped())
                                return;

                            ExplorationState toExplore = new ExplorationState();
                            toExplore.addAll(currentState);
                            toExplore.add(new ParallelStep(interactions.get(i), interactions.get(j)));
                            parallelRun(toExplore);
                        }
                    }

                    ExplorationState newState = new ExplorationState();
                    incremented = false;
                    for (int i = currentDepth - 1; i >= 0; i--) {
                        if (incremented) {
                            newState.add(currentState.get(i));
                        } else {
                            Interaction act = ((SequentialStep) currentState.get(i)).interaction;
                            int indexOf = interactions.indexOf(act);
                            if (indexOf == interactions.size() - 1) {
                                newState.add(new SequentialStep(interactions.get(0)));
                            } else {
                                newState.add(new SequentialStep(interactions.get(indexOf + 1)));
                                incremented = true;
                            }
                        }
                    }

                    if (incremented) {
                        Collections.reverse(newState);
                        currentState = newState;
                    }

                } while (incremented);

            }

            logger.info("Done exploring.");
        } finally {
            DatabaseConnector.close();
        }
    }

    private boolean parallelExplore(int depth, ExplorationState state, int timesParallel) throws InterruptedException {
        Set<UnorderedPair<Interaction>> pairs = testMap.get(state);
        Optional<UnorderedPair<Interaction>> pair = pairs.stream().findFirst();
        if (pair.isPresent()) {
            pairs.remove(pair.get());
            if (pairs.isEmpty())
                testMap.remove(state);
            ExplorationState firstOrder = (ExplorationState) state.clone();
            firstOrder.add(new ParallelStep(pair.get().first, pair.get().second));
            timesParallel--;
            if (firstOrder.size() >= depth || timesParallel <= 0) {
                /* NOTE:
                 * for multi-client interactions without a sequential prefix:
                 * if { a || b, c || d } detects a violation after just a || b, then c || d needs to be
                 * re-added to the pairs list
                 */
                parallelRun(firstOrder);
                return true;
            } else {
                ExplorationState secondOrder = (ExplorationState) state.clone();
                secondOrder.add(new ParallelStep(pair.get().second, pair.get().first));
                if (parallelExplore(depth, firstOrder, timesParallel)) {
                    return true;
                } else if (parallelExplore(depth, secondOrder, timesParallel)) {
                    return true;
                } else {
                    parallelRun(firstOrder);
                    return true;
                }
            }
        } else {
            if (state.size() == depth - 1)
                return false;

            for (Interaction next : interactions) {
                ExplorationState nextState = (ExplorationState) state.clone();
                nextState.add(new SequentialStep(next));
                if (parallelExplore(depth, nextState, timesParallel))
                    return true;
            }

            return false;
        }
    }

    public void sequentialExplore(ExplorationState state) {
        List<Interaction> interactions = state.stream().map(stp -> ((SequentialStep) stp).interaction)
                .collect(Collectors.toList());
        logger.info("Running " + interactions);
        Test sequentialTest = experimentSpec.combinator.combineSequential(interactions);
        int attemptCount = 0;
        boolean succeeded = false;
        while (attemptCount < 5 && !succeeded) {
            try {
                attemptCount++;
                singleThreadedExecutor.executeTest(sequentialTest);
                succeeded = true;
            } catch (Exception e) {
                logger.warning(ExceptionUtils.getStackTrace(e));
            }
        }

        if (attemptCount >= 5 && !succeeded)
            logger.warning("Giving up on sequential explore " + state);

        List<byte[]> byteScreenshots = sequentialTest.getScreenshotList();
        List<Img<ARGBType>> screenshots = byteScreenshots.stream()
                .map(GuidedStateSpaceExplorer::convertScreenshot).collect(Collectors.toList());
        for (int i = 0; i < interactions.size(); i++) {
            ExplorationState current = new ExplorationState();
            current.addAll(interactions.subList(0, i).stream().map(SequentialStep::new)
                    .collect(Collectors.toList()));

            Interaction next = interactions.get(i);
            ImageDiff diff = null;
            if (i < screenshots.size() - 1) {
                if (!stateScreenshotMap.containsKey(current))
                    stateScreenshotMap.put(current, byteScreenshots.get(i));

                Img<ARGBType> imgBefore = screenshots.get(i);
                Img<ARGBType> imgAfter = screenshots.get(i + 1);
                if (imgBefore != null && imgAfter != null) {
                    diff = new ImageDiff(imgBefore, imgAfter, experimentSpec.exclusionRectangles);
                }
            }
            diffMap.add(current, next, new DiffWrapper(diff, useZip));
        }
    }

    public MapMap<ExplorationState, Interaction, DiffWrapper> getDiffMap() {
        return diffMap;
    }

    public void setUseZip(boolean useZip) {
        this.useZip = useZip;
    }

    public void increaseRunId() {
        runId++;
    }

    public boolean getLastFailed() {
        return lastFailed;
    }

    private static Img<ARGBType> convertScreenshot(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        Img<ARGBType> img;
        try {
            BufferedImage bi = ImageIO.read(is);
            is.close();
            int width = bi.getWidth();
            int height = bi.getHeight();
            img = new ARGBScreenImage(width, height,
                    bi.getRGB(0, 0, width, height, null, 0, width));
        } catch (IOException e) {
            img = null;
        }
        return img;
    }

    public static class SequentialStep extends ExplorationStep {
        private static final long serialVersionUID = 1L;
        public final Interaction interaction;

        public SequentialStep(Interaction interaction) {
            this.interaction = interaction;
        }

        @Override
        public String toString() {
            return "Sequential(" + interaction + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SequentialStep that = (SequentialStep) o;
            return Objects.equals(interaction, that.interaction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(interaction);
        }
    }

    public static class ParallelStep extends ExplorationStep {
        private static final long serialVersionUID = 1L;
        public final Interaction first;
        public final Interaction second;

        public ParallelStep(Interaction first, Interaction second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "Parallel(" + first + ", " + second + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParallelStep that = (ParallelStep) o;
            return Objects.equals(first, that.first) &&
                    Objects.equals(second, that.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
}
