package de.crispda.sola.multitester.util;

import com.google.common.collect.ImmutableMap;
import de.crispda.sola.multitester.*;
import de.crispda.sola.multitester.runner.ExperimentSpec;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.scenario.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.openqa.selenium.Keys;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportComparison {
    public static void main(String[] args) throws Exception {
        if (args.length < 4)
            return;
        String specFilename = args[0];
        String diffmapFilename = args[1];
        String firstFailures = args[2];
        String secondFailures = args[3];

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        for (Handler h : logger.getHandlers()) {
            h.setLevel(Level.WARNING);
        }

        String secondSpecFilename = null;
        if (args.length > 4)
            secondSpecFilename = args[4];

        ExperimentSpec spec;
        if (specFilename.endsWith(".xml")) {
            spec = getSpecFromLog(specFilename, secondSpecFilename);
        } else {
            spec = SerializationUtils.deserialize(
                    FileUtils.readFileToByteArray(new File(specFilename)));
        }

        if (!(spec instanceof SetExperimentSpec)) {
            System.err.println("Not a SetExperimentSpec! Exiting.");
            return;
        }

        HashMap<ExplorationState, byte[]> stateScreenshotMap = new HashMap<>();
        stateScreenshotMap.putAll(
                SerializationUtils.<HashMap<ExplorationState, byte[]>>deserialize(
                        ZipUtils.unzip(FileUtils.readFileToByteArray(new File(diffmapFilename + ".scr")))));

        Set<UnorderedPair<ExplorationState>> stateEquivalenceSet = GuidedStateSpaceExplorer.inferEquivalentStates(
                stateScreenshotMap, spec.exclusionRectangles);

        List<ExplorationState> firstStates = loadStates(firstFailures);
        List<ExplorationState> secondStates = loadStates(secondFailures);

        int overlapCount = 0;
        for (ExplorationState firstState : firstStates) {
            GuidedStateSpaceExplorer.ParallelStep firstPar = (GuidedStateSpaceExplorer.ParallelStep)
                    firstState.get(firstState.size() - 1);
            ExplorationState firstPrefix = new ExplorationState();
            firstPrefix.addAll(firstState.subList(0, firstState.size() - 1));
            for (ExplorationState secondState : secondStates) {
                GuidedStateSpaceExplorer.ParallelStep secondPar = (GuidedStateSpaceExplorer.ParallelStep)
                        secondState.get(secondState.size() - 1);
                if (firstPar.first.equals(secondPar.first) && firstPar.second.equals(secondPar.second) ||
                        firstPar.second.equals(secondPar.first) && firstPar.first.equals(secondPar.second)) {
                    ExplorationState secondPrefix = new ExplorationState();
                    secondPrefix.addAll(secondState.subList(0, secondState.size() - 1));
                    UnorderedPair<ExplorationState> statePair = new UnorderedPair<>(firstPrefix, secondPrefix);
                    if (firstPrefix.equals(secondPrefix) || stateEquivalenceSet.contains(statePair)) {
                        System.out.println("Overlap between \n" + firstState + " and \n" + secondState);
                        overlapCount++;
                        break;
                    }
                }
            }
        }

        System.out.println("Overlap count: " + overlapCount);
    }

    private static ExperimentSpec getSpecFromLog(String logFilename, String secondFilename) throws
            IOException, SAXException, ParserConfigurationException {
        String specName = getSpecName(logFilename);
        if (secondFilename != null) {
            String secondSpecName = getSpecName(secondFilename);
            if (!specName.equals(secondSpecName)) {
                System.out.println("These two executions are not for the same test specification:");
                System.out.println(logFilename + ": " + specName);
                System.out.println(secondFilename + ": " + secondSpecName);
                System.exit(1);
            }
        }
        List<ExperimentSpec> experimentSpecs = ExperimentSpec.getExperimentSpecs();
        for (ExperimentSpec experimentSpec : experimentSpecs) {
            if (experimentSpec.getName().equals(specName))
                return experimentSpec;
        }

        throw new RuntimeException("This execution's test specification was not found in the cache.");
    }

    private static String getSpecName(String logFilename) throws ParserConfigurationException,
            SAXException, IOException {
        XMLFile log = new XMLFile(logFilename);
        NodeList records = log.root.getChildNodes();
        for (int i = 0; i < records.getLength(); i++) {
            Node recordNode = records.item(i);
            if (!(recordNode instanceof Element))
                continue;

            Element recordEl = (Element) recordNode;
            String recordClass = recordEl.getElementsByTagName("class").item(0).getTextContent();
            String recordMessage = recordEl.getElementsByTagName("message").item(0).getTextContent();
            if ((recordClass.equals("de.crispda.sola.multitester.runner.GuidedExperimentTask") ||
                    recordClass.equals("de.crispda.sola.multitester.runner.GuidedCachedExperimentTask"))
                    && recordMessage.startsWith("Running ")) {
                return recordMessage.substring("Running ".length());
            }
        }

        throw new IllegalStateException("Could not find test specification in log file");
    }

    private static Pattern depth0par = Pattern.compile("\\[Parallel\\(([^)]*)\\)\\]");
    private static Pattern depth1par = Pattern.compile("\\[Sequential\\(([^)]*)\\), Parallel\\(([^)]*)\\)\\]");
    private static Pattern depth2par =
            Pattern.compile("\\[Sequential\\(([^)]*)\\), Sequential\\(([^)]*)\\), Parallel\\(([^)]*)\\)\\]");
    private static Pattern depth1seq = Pattern.compile("\\[Sequential\\(([^)]*)\\)\\]");
    private static Pattern depth2seq = Pattern.compile("\\[Sequential\\(([^)]*)\\), Sequential\\(([^)]*)\\)\\]");
    private static Pattern par = Pattern.compile("([A-Za-z0-9]*\\{.*\\}), ([A-Za-z0-9]*\\{.*\\})");
    private static final Map<Pattern, Function<Matcher, Interaction>> interactionMap =
            ImmutableMap.<Pattern, Function<Matcher, Interaction>>builder()
                    .put(Pattern.compile("GDocsWrite\\{([ab ])\\}"), m -> new GDocsWrite(m.group(1)))
                    .put(Pattern.compile("GDocsWrite\\{Keys.([A-Z]*)\\}"), m -> new GDocsWrite(Keys.valueOf(m.group(1))))
                    .put(Pattern.compile("GDocsApplyModification\\{([A-Za-z]*), ([A-Za-z]*)\\}"),
                            m -> new GDocsApplyModification(Selection.valueOf(m.group(1)),
                                    GDocs.Modification.valueOf(m.group(2))))
                    .put(Pattern.compile("GDocsDelete\\{([A-Za-z]*)\\}"), m ->
                            new GDocsDelete(Selection.valueOf(m.group(1))))
                    .put(Pattern.compile("GDocsMakeFont\\{([A-Za-z]*), ([A-Za-z]*)\\}"), m ->
                            new GDocsMakeFont(Selection.valueOf(m.group(1)), GDocs.Font.valueOf(m.group(2))))
                    .put(Pattern.compile("GDocsMakeFontSize\\{([A-Za-z]*), ([A-Za-z0-9]*)\\}"), m ->
                            new GDocsMakeFontSize(Selection.valueOf(m.group(1)), m.group(2)))
                    .put(Pattern.compile("FirepadWrite\\{([ab ])\\}"), m -> new FirepadWrite(m.group(1)))
                    .put(Pattern.compile("FirepadWrite\\{Keys.([A-Z]*)\\}"), m -> new FirepadWrite(Keys.valueOf(m.group(1))))
                    .put(Pattern.compile("FirepadApplyModification\\{([A-Za-z]*), ([A-Za-z]*)\\}"), m ->
                            new FirepadApplyModification(Selection.valueOf(m.group(1)),
                                    Firepad.Modification.valueOf(m.group(2))))
                    .put(Pattern.compile("FirepadDelete\\{([A-Za-z]*)\\}"), m ->
                            new FirepadDelete(Selection.valueOf(m.group(1))))
                    .put(Pattern.compile("FirepadMakeFont\\{([A-Za-z]*), ([A-Za-z]*)\\}"), m ->
                            new FirepadMakeFont(Selection.valueOf(m.group(1)), Firepad.Font.valueOf(m.group(2))))
                    .put(Pattern.compile("FirepadMakeFontSize\\{([A-Za-z]*), ([A-Za-z0-9]*)\\}"), m ->
                            new FirepadMakeFontSize(Selection.valueOf(m.group(1)), m.group(2)))
                    .put(Pattern.compile("OwncloudWrite\\{([ab ])\\}"), m -> new OwncloudWrite(m.group(1)))
                    .put(Pattern.compile("OwncloudWrite\\{Keys.([A-Z]*)\\}"), m -> new OwncloudWrite(Keys.valueOf(m.group(1))))
                    .put(Pattern.compile("OwncloudApplyModification\\{([A-Za-z]*), ([A-Za-z]*)\\}"), m ->
                            new OwncloudApplyModification(Selection.valueOf(m.group(1)),
                                    Owncloud.Modification.valueOf(m.group(2))))
                    .put(Pattern.compile("OwncloudMakeFont\\{([A-Za-z]*), ([A-Za-z]*)\\}"), m ->
                            new OwncloudMakeFont(Selection.valueOf(m.group(1)), Owncloud.Font.valueOf(m.group(2))))
                    .put(Pattern.compile("OwncloudDelete\\{([A-Za-z]*)\\}"), m ->
                            new OwncloudDelete(Selection.valueOf(m.group(1))))
                    .put(Pattern.compile("OwncloudMakeFontSize18\\{([A-Za-z]*)\\}"), m ->
                            new OwncloudMakeFontSize18(Selection.valueOf(m.group(1))))
                    .build();

    private static List<ExplorationState> loadStates(String filename) throws ParserConfigurationException,
            SAXException, IOException {
        XMLFile file = new XMLFile(filename);
        Element root = file.root;
        NodeList children = root.getChildNodes();
        List<ExplorationState> stateList = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            if (!(children.item(i) instanceof Element))
                continue;

            Element failure = (Element) children.item(i);
            String sequence = failure.getElementsByTagName("sequence").item(0).getTextContent();
            stateList.add(matchExplorationState(sequence));
        }

        return stateList;
    }

    public static ExplorationState matchExplorationState(String sequence) {
        if (sequence.equals("[]"))
            return new ExplorationState();

        Matcher depth0matcher = depth0par.matcher(sequence);
        Matcher depth1matcher = depth1par.matcher(sequence);
        Matcher depth2matcher = depth2par.matcher(sequence);
        Matcher depth1seqmatcher = depth1seq.matcher(sequence);
        Matcher depth2seqmatcher = depth2seq.matcher(sequence);

        if (depth0matcher.find()) {
            Matcher parmatch = par.matcher(depth0matcher.group(1));
            if (!parmatch.find())
                throw new RuntimeException("parmatch: unable to match " + depth0matcher.group(1));
            return ExplorationState.create(new GuidedStateSpaceExplorer.ParallelStep(
                    matchInteraction(parmatch.group(1)),
                    matchInteraction(parmatch.group(2)))
            );
        } else if (depth1matcher.find()) {
            Matcher parmatch = par.matcher(depth1matcher.group(2));
            if (!parmatch.find())
                throw new RuntimeException("parmatch: unable to match " + depth1matcher.group(2));
            return ExplorationState.create(
                    new GuidedStateSpaceExplorer.SequentialStep(
                            matchInteraction(depth1matcher.group(1))),
                    new GuidedStateSpaceExplorer.ParallelStep(
                            matchInteraction(parmatch.group(1)),
                            matchInteraction(parmatch.group(2))
                    )
            );
        } else if (depth2matcher.find()) {
            Matcher parmatch = par.matcher(depth2matcher.group(3));
            if (!parmatch.find())
                throw new RuntimeException("parmatch: unable to match " + depth2matcher.group(3));
            return ExplorationState.create(
                    new GuidedStateSpaceExplorer.SequentialStep(
                            matchInteraction(depth2matcher.group(1))),
                    new GuidedStateSpaceExplorer.SequentialStep(
                            matchInteraction(depth2matcher.group(2))),
                    new GuidedStateSpaceExplorer.ParallelStep(
                            matchInteraction(parmatch.group(1)),
                            matchInteraction(parmatch.group(2))
                    )
            );
        } else if (depth1seqmatcher.find()) {
            return ExplorationState.create(
                    new GuidedStateSpaceExplorer.SequentialStep(
                            matchInteraction(depth1seqmatcher.group(1)))
            );
        } else if (depth2seqmatcher.find()) {
            return ExplorationState.create(
                    new GuidedStateSpaceExplorer.SequentialStep(
                            matchInteraction(depth2seqmatcher.group(1))),
                    new GuidedStateSpaceExplorer.SequentialStep(
                            matchInteraction(depth2seqmatcher.group(2)))
            );
        } else {
            throw new RuntimeException("Parsing of " + sequence + " failed");
        }
    }

    private static Interaction matchInteraction(String desc) {
        for (Map.Entry<Pattern, Function<Matcher, Interaction>> mapping : interactionMap.entrySet()) {
            Matcher matcher = mapping.getKey().matcher(desc);
            if (matcher.find()) {
                return mapping.getValue().apply(matcher);
            }
        }

        throw new RuntimeException("Unable to match " + desc);
    }
}
