package de.crispda.sola.multitester.util;

import com.beust.jcommander.internal.Lists;
import de.crispda.sola.multitester.ExplorationState;
import de.crispda.sola.multitester.GuidedStateSpaceExplorer;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreePrinter {
    public static void main(String[] args) throws Exception {
        SetExperimentSpec spec = SerializationUtils.deserialize(
                FileUtils.readFileToByteArray(new File(args[0])));

        Map<ExplorationState, Integer> connectivityMap = new HashMap<>();
        Pattern seqPattern = Pattern.compile("^(\\[.*\\]): ([0-9]+)");
        int depth = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
            String line = br.readLine();
            int stateNum = Integer.parseInt(line);
            while (stateNum - Math.pow(spec.interactionSet.size(), depth) > 0)
                depth++;
            depth--;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("---"))
                    continue;

                if (Objects.equals(line.trim(), ""))
                    break;

                if (line.startsWith("\t")) {
                    continue;
                }

                Matcher matcher = seqPattern.matcher(line);
                if (matcher.find()) {
                    ExplorationState state = ReportComparison.matchExplorationState(matcher.group(1));
                    int connectivity = Integer.parseInt(matcher.group(2));
                    connectivityMap.put(state, connectivity);
                }
            }
        }

        Map<ExplorationState, Integer> nodeNumber = new HashMap<>();
        int nodeCount = 0;
        System.out.println("digraph G {");
        for (int currentDepth = 0; currentDepth < depth; currentDepth++) {
            ExplorationState currentState = new ExplorationState();
            List<Interaction> interactions = Lists.newArrayList(spec.interactionSet);
            for (int i = 0; i < currentDepth; i++) {
                currentState.add(new GuidedStateSpaceExplorer.SequentialStep(interactions.get(0)));
            }

            boolean incremented;

            do {
                int connectivity = Optional.ofNullable(connectivityMap.get(currentState)).orElse(0);
                if (connectivity > 0) {
                    System.out.println(String.format("\ta%d [label=\"%d\", style=filled];", nodeCount,
                            connectivity));
                } else {
                    System.out.println(String.format("\ta%d [shape=point];", nodeCount));
                }
                nodeNumber.put(currentState, nodeCount);

                ExplorationState prevState = (ExplorationState) currentState.clone();
                boolean added = false;
                while (!added && !prevState.isEmpty()) {
                    prevState.remove(prevState.size() - 1);
                    Optional<Integer> prevNumber = Optional.ofNullable(nodeNumber.get(prevState));
                    if (prevNumber.isPresent()) {
                        System.out.println(String.format("\ta%d -> a%d;", prevNumber.get(), nodeCount));
                        added = true;
                    }
                }

                nodeCount++;

                ExplorationState newState = new ExplorationState();
                incremented = false;
                for (int i = currentDepth - 1; i >= 0; i--) {
                    if (incremented) {
                        newState.add(currentState.get(i));
                    } else {
                        Interaction act = ((GuidedStateSpaceExplorer.SequentialStep) currentState.get(i)).interaction;
                        int indexOf = interactions.indexOf(act);
                        if (indexOf == interactions.size() - 1) {
                            newState.add(new GuidedStateSpaceExplorer.SequentialStep(interactions.get(0)));
                        } else {
                            newState.add(new GuidedStateSpaceExplorer.SequentialStep(interactions.get(indexOf + 1)));
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

        System.out.println("}");
    }
}
