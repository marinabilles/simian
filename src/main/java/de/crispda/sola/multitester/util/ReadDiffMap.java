package de.crispda.sola.multitester.util;

import com.beust.jcommander.internal.Lists;
import de.crispda.sola.multitester.*;
import de.crispda.sola.multitester.runner.ExperimentSpec;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.scenario.GDocsWrite;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.zip.DataFormatException;

public class ReadDiffMap {
    public static void main(String[] args) throws IOException, InterruptedException, DataFormatException,
            ExecutionException {
        if (args.length < 2) {
            System.out.println("Missing file");
            return;
        }

        boolean skipInference = false;
        if (args.length > 2 && args[2].equals("-skip"))
            skipInference = true;

        boolean equiv = false;
        if (args.length > 2 && args[2].equals("-equiv"))
            equiv = true;

        boolean dry = false;
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-dry")) {
                dry = true;
                break;
            }
        }

        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.OFF);

        MapMap<ExplorationState, Interaction, DiffWrapper> diffMap =
                SerializationUtils.deserialize(
                        ZipUtils.unzip(FileUtils.readFileToByteArray(new File(args[0]))));
        HashMap<ExplorationState, byte[]> stateScreenshotMap =
                SerializationUtils.deserialize(
                        ZipUtils.unzip(FileUtils.readFileToByteArray(new File(args[0] + ".scr"))));

        ExperimentSpec spec = SerializationUtils.deserialize(
                FileUtils.readFileToByteArray(new File(args[1])));

        if (!(spec instanceof SetExperimentSpec)) {
            System.err.println("Not a SetExperimentSpec! Exiting.");
            return;
        }

        List<byte[]> stateImages = new ArrayList<>();
        List<String> stateDescs = new ArrayList<>();
        stateScreenshotMap.entrySet().forEach(e -> {
            stateImages.add(e.getValue());
            stateDescs.add(e.getKey().toString());
        });

        System.out.println(diffMap.entrySet().stream().mapToInt(pair ->
                Optional.ofNullable(pair.getValue()).map(Map::size).orElse(0)).sum());

        if (!skipInference && !equiv) {
            // sameDiffOverlaps(diffMap);
            System.out.println("------------------------");

            MapMap<ExplorationState, Interaction, DiffWrapper> diffCopyMap = new MapMap<>();
            diffMap.entrySet().forEach(e -> diffCopyMap.put(e.getKey(), e.getValue()));
            SetMap<ExplorationState, UnorderedPair<Interaction>> testMap =
                    GuidedStateSpaceExplorer.inferOverlaps(diffCopyMap, stateScreenshotMap,
                            spec.exclusionRectangles);
            for (Map.Entry<ExplorationState, Set<UnorderedPair<Interaction>>> entry : testMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue().size());

                for (UnorderedPair<Interaction> pair : entry.getValue()) {
                    System.out.println("   (" + pair.first + ", " + pair.second + ")");
                }

                System.out.println("--------");
            }
        }

        if (equiv) {
            int classIndex = 0;
            Map<ExplorationState, Integer> equivalenceClassMap = new HashMap<>();
            for (ExplorationState state : stateScreenshotMap.keySet()) {
                equivalenceClassMap.put(state, classIndex);
                classIndex++;
            }
            Set<UnorderedPair<ExplorationState>> stateEquivalenceSet = GuidedStateSpaceExplorer.inferEquivalentStates(
                    stateScreenshotMap, spec.exclusionRectangles);

            for (UnorderedPair<ExplorationState> pair : stateEquivalenceSet) {
                int firstClass = equivalenceClassMap.get(pair.first);
                int secondClass = equivalenceClassMap.get(pair.second);
                if (firstClass != secondClass) {
                    // merge
                    equivalenceClassMap.entrySet().stream().filter(e -> e.getValue() == secondClass)
                            .forEach(e -> e.setValue(firstClass));
                }
            }

            Set<Integer> classSet = new HashSet<>(equivalenceClassMap.values());
            System.out.println("Number of equivalence classes: " + classSet.size());

            Map<Integer, Set<ExplorationState>> reverseClassMap = new HashMap<>();
            IntStream.range(0, classIndex).forEach(i -> reverseClassMap.put(i, new HashSet<>()));
            equivalenceClassMap.entrySet().forEach(entry ->
                reverseClassMap.get(entry.getValue()).add(entry.getKey()));

            for (Map.Entry<Integer, Set<ExplorationState>> entry : reverseClassMap.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    System.out.println(String.format("Class: %d", entry.getKey()));
                    for (ExplorationState state : entry.getValue()) {
                        System.out.println("\t" + state.toString());
                    }
                    System.out.println("--------");
                }
            }
        }

        if (!equiv && !dry) {
            SequenceImageFrame.view(stateImages, stateDescs, spec.exclusionRectangles);

            System.out.println("------------------------");

            List<String> descriptions = new ArrayList<>();
            List<List<ImageDiff>> diffs = new ArrayList<>();

            Predicate<Map.Entry<Interaction, DiffWrapper>> gDocsHome = e2 ->
                    e2.getKey() instanceof GDocsWrite &&
                            e2.getKey().toString().equals("GDocsWrite{Keys.HOME}");
            Predicate<Map.Entry<Interaction, DiffWrapper>> pTrue = e2 -> true;

            diffMap.entrySet().forEach(e ->
                e.getValue().entrySet().stream().filter(pTrue)
                        .forEach(e2 -> {
                            descriptions.add(e.getKey().toString() + ": " + e2.getKey());
                            e2.getValue().unzip();
                            diffs.add(Lists.newArrayList(e2.getValue().get()));
                        })
            );

            SequenceImageFrame.view(diffs, descriptions);

            System.out.println("Done");
        }
    }

    private static void sameDiffOverlaps(MapMap<ExplorationState, Interaction, ImageDiff> diffMap)
            throws InterruptedException {
        Set<ImageDiff> diffsToShow = new HashSet<>();
        Map<ExplorationState, Map<Interaction, List<ImageDiff>>> executionMap = new HashMap<>();
        diffMap.keySet().forEach(k -> executionMap.put(k, new HashMap<>()));

        for (Map.Entry<ExplorationState, Map<Interaction, ImageDiff>> entry : diffMap.entrySet()) {
            System.out.println(entry.getKey());
            Map<Interaction, List<ImageDiff>> diffListMap = executionMap.get(entry.getKey());
            for (Map.Entry<Interaction, ImageDiff> pair : entry.getValue().entrySet()) {
                if (diffListMap.containsKey(pair.getKey())) {
                    List<ImageDiff> diffList = diffListMap.get(pair.getKey());
                    diffList.add(pair.getValue());
                } else {
                    List<ImageDiff> diffList = new ArrayList<>();
                    diffList.add(pair.getValue());
                    diffListMap.put(pair.getKey(), diffList);
                }
            }

            for (Map.Entry<Interaction, List<ImageDiff>> diffListMapEntry : diffListMap.entrySet()) {
                List<ImageDiff> diffList = diffListMapEntry.getValue();
                System.out.println(diffListMapEntry.getKey() + ": " + diffList.size());
                for (int i = 0; i < diffList.size(); i++) {
                    ImageDiff first = diffList.get(i);
                    for (int j = i + 1; j < diffList.size(); j++) {
                        ImageDiff second = diffList.get(j);
                        List<Optional<Integer>> pixels;
                        if (first != null) {
                            pixels = first.getOverlappingPixels(second);
                        } else if (second != null) {
                            pixels = second.getOverlappingPixels(null);
                        } else {
                            pixels = new ArrayList<>();
                            pixels.add(Optional.empty());
                            pixels.add(Optional.empty());
                            pixels.add(Optional.empty());
                        }
                        System.out.println(String.format("(%d, %d) ---- %s - %s - %s",
                                i, j,
                                pixels.get(0).map(it -> Integer.toString(it)).orElse("null"),
                                pixels.get(1).map(it -> Integer.toString(it)).orElse("null"),
                                pixels.get(2).map(it -> Integer.toString(it)).orElse("null")));

                        if (pixels.get(0).map(it -> it > 100).orElse(false)) {
                            diffsToShow.add(first);
                        }
                    }
                }
            }

            System.out.println("--------");
        }


        for (ImageDiff diff : diffsToShow) {
            ImageFrame.showImage(diff.getImage(false));
        }
    }
}
