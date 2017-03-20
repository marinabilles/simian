package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.*;
import de.crispda.sola.multitester.runner.ExperimentSpec;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.scenario.*;
import de.crispda.sola.multitester.web.Drivers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.Keys;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecutionLogParser {
    private static final Pattern argumentPattern =
            Pattern.compile("([A-Za-z._]+)(?:,|$)");
    private static final String interaction = "[A-Za-z]+\\{(?:[A-Za-z._]*(?:, )?)+\\}";
    private static final Pattern interactionPattern =
            Pattern.compile("([A-Za-z]+)\\{((?:[A-Za-z._]*(?:, )?)+)\\}");
    private static final Pattern atStateFrontPattern =
            Pattern.compile("At state \\[(?:" + interaction + "(?:, )?)*\\]: ");
    private static final Pattern atStateBackPattern = Pattern.compile(
            "\\((" + interaction + "), (" + interaction + ")$");

    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            return;

        XMLFile log = new XMLFile(args[0]);

        // if "parse" parameter is given, entire parallel exploration step is performed with depth 3
        boolean parse = false;
        if (args.length > 1 && args[1].equals("parse"))
            parse = true;
        SetMap<ExplorationState, UnorderedPair<Interaction>> testMap = new SetMap<>();

        NodeList records = log.root.getChildNodes();

        boolean sequential = false;
        boolean inference = false;
        Instant firstSequential = null;
        Instant lastSequential = null;
        Instant afterSequential = null;
        Instant afterInference = null;
        Instant afterParallel = null;
        int sequentialCount = 0;
        int parallelCount = 0;

        Instant recordTime = null;
        for (int i = 0; i < records.getLength(); i++) {
            Node recordNode = records.item(i);
            if (!(recordNode instanceof Element))
                continue;

            Element recordEl = (Element) recordNode;
            recordTime = Instant.ofEpochMilli(Long.parseLong(
                    recordEl.getElementsByTagName("millis").item(0).getTextContent()));
            String recordClass = recordEl.getElementsByTagName("class").item(0).getTextContent();
            String method = recordEl.getElementsByTagName("method").item(0).getTextContent();
            String recordMessage = recordEl.getElementsByTagName("message").item(0).getTextContent();

            if ((recordClass.equals("de.crispda.sola.multitester.runner.GuidedExperimentTask") ||
                    recordClass.equals("de.crispda.sola.multitester.runner.GuidedCachedExperimentTask"))
                    && recordMessage.startsWith("Running ")) {
                String specName = recordMessage.substring("Running ".length());
                System.out.print(specName);
                if (recordClass.contains("Cached")) {
                    System.out.print(" -- cached");
                }
                System.out.print("\n");
            } else if (recordMessage.startsWith("Depth:")) {
                System.out.println(recordMessage);
            }

            if (method.equals("sequentialExplore")) {
                if (!sequential) {
                    sequential = true;
                    firstSequential = recordTime;
                }
                lastSequential = recordTime;
                sequentialCount++;
            } else if (recordMessage.startsWith("At state")) {
                if (sequential) {
                    sequential = false;
                    afterSequential = recordTime;
                }
                inference = true;

                if (parse) {
                    Pair<ExplorationState, UnorderedPair<Interaction>> stateAndPair = getStateAndPair(recordMessage);
                    testMap.add(stateAndPair.getKey(), stateAndPair.getValue());
                }
            } else if (recordMessage.equals("Done exploring.")) {
                afterParallel = recordTime;
            } else if (method.equals("parallelRun")) {
                if (inference) {
                    inference = false;
                    afterInference = recordTime;
                }
                parallelCount++;
            }
        }

        if (afterInference != null && afterParallel == null) {
            afterParallel = recordTime;
        }

        System.out.println("Sequential count:                              " + sequentialCount);
        if (firstSequential != null && lastSequential != null && afterSequential != null) {
            Duration allSequential = Duration.between(firstSequential, afterSequential);
            System.out.println("Entire sequential exploration took:            " + allSequential);
            if (sequentialCount > 0) {
                Duration singleSequential = allSequential.dividedBy(sequentialCount);
                System.out.println("Individual sequential exploration averaged at: " + singleSequential);
                if (sequentialCount > 1) {
                    Duration singleWithoutLast = Duration.between(firstSequential, lastSequential)
                            .dividedBy(sequentialCount - 1);
                    System.out.println("Individual sequential without last:            " + singleWithoutLast);
                }
            }
        }

        if (afterSequential != null && afterInference != null) {
            Duration allInference = Duration.between(afterSequential, afterInference);
            System.out.println("Inference step:                                " + allInference);
        }

        System.out.println("Parallel count:                                " + parallelCount);
        if (afterInference != null && afterParallel != null) {
            Duration allParallel = Duration.between(afterInference, afterParallel);
            System.out.println("Entire parallel step took:                     " + allParallel);
            if (parallelCount > 0) {
                Duration singleParallel = allParallel.dividedBy(parallelCount);
                System.out.println("Individual parallel exploration averaged at:   " + singleParallel);
            }
        }

        if (parse && args.length > 2) {
            ExperimentSpec spec = SerializationUtils.deserialize(
                    FileUtils.readFileToByteArray(new File(args[2])));
            if (!(spec instanceof SetExperimentSpec))
                throw new Exception("Not a SetExperimentSpec!");

            if (!Paths.exists("."))
                throw new IOException("Path not found!");
            GuidedStateSpaceExplorer explorer = new GuidedStateSpaceExplorer(
                    (SetExperimentSpec) spec, Drivers.remoteDriver(), ".");
            explorer.withTestMap(testMap);
            explorer.setRunState(RunState.RUNNING);
            explorer.parallelExplorationStep(3);
        }
    }

    private static Pair<ExplorationState, UnorderedPair<Interaction>> getStateAndPair(String recordMessage)
            throws Exception {
        String[] parts = recordMessage.split("adding");
        if (parts.length != 2)
            throw new Exception("Unable to parse " + recordMessage);
        Matcher frontMatcher = atStateFrontPattern.matcher(parts[0]);
        Matcher backMatcher = atStateBackPattern.matcher(parts[1]);
        if (!frontMatcher.find() || !backMatcher.find())
            throw new Exception("Unable to parse " + recordMessage);

        ExplorationState state = new ExplorationState();

        Matcher frontInteractionMatcher = interactionPattern.matcher(frontMatcher.group(0));
        while (frontInteractionMatcher.find()) {
            Interaction act = matchInteraction(frontInteractionMatcher.group(1),
                    frontInteractionMatcher.group(2));
            state.add(new GuidedStateSpaceExplorer.SequentialStep(act));
        }

        Matcher backInteractionMatcher = interactionPattern.matcher(backMatcher.group(0));
        if (!backInteractionMatcher.find())
            throw new Exception("Unable to parse " + recordMessage);
        Interaction first = matchInteraction(backInteractionMatcher.group(1),
                backInteractionMatcher.group(2));
        if (!backInteractionMatcher.find())
            throw new Exception("Unable to parse " + recordMessage);
        Interaction second = matchInteraction(backInteractionMatcher.group(1),
                backInteractionMatcher.group(2));
        UnorderedPair<Interaction> upair = new UnorderedPair<>(first, second);

        return new ImmutablePair<>(state, upair);
    }

    private static Interaction matchInteraction(String className, String arguments) throws Exception {
        Matcher argumentMatcher = argumentPattern.matcher(arguments);
        Interaction act;
        List<CharSequence> charargs = new ArrayList<>();
        switch (className) {
            case "GDocsWrite":
                while (argumentMatcher.find()) {
                    String arg = argumentMatcher.group(1);
                    if (arg.startsWith("Keys.")) {
                        charargs.add(Keys.valueOf(arg.substring(5)));
                    } else {
                        charargs.add(arg);
                    }
                }
                act = new GDocsWrite(charargs.toArray(new CharSequence[0]));
                break;
            case "GDocsApplyModification":
                if (!argumentMatcher.find())
                    throw new Exception("Unable to parse interaction " + className);
                Selection sel = Selection.valueOf(argumentMatcher.group(1));
                if (!argumentMatcher.find())
                    throw new Exception("Unable to parse interaction " + className);
                GDocs.Modification modification = GDocs.Modification.valueOf(argumentMatcher.group(1));
                act = new GDocsApplyModification(sel, modification);
                break;
            case "GDocsButtonClick":
                if (!argumentMatcher.find())
                    throw new Exception("Unable to parse interaction " + className);
                GDocs.Button button = GDocs.Button.valueOf(argumentMatcher.group(1));
                act = new GDocsButtonClick(button);
                break;
            case "GDocsDelete":
                if (!argumentMatcher.find())
                    throw new Exception("Unable to parse interaction " + className);
                act = new GDocsDelete(Selection.valueOf(argumentMatcher.group(1)));
                break;
            default:
                throw new Exception("Unable to parse interaction " + className);
        }

        return act;
    }
}
