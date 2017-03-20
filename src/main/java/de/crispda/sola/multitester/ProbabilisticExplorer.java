package de.crispda.sola.multitester;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This explorer generates random sequences of actions on two threads that execute sequentially.
 * E.g. write.1 write.2 del.1
 * If a conflict (= affected area overlap) between two actions has been detected, a reversal of
 * those actions may be explored, e.g. for the conflict (write.1, write.2), the sequence
 * write.2 write.1 del.1 might be explored. There is a 50% chance of exploring the reversed sequence
 * vs. exploring a different, randomly generated sequence.
 * This is different from {@link RandomExplorer} which explores a random interleaving of the actions of
 * the two threads, and from {@link RandomStateSpaceExplorer}, which generates multi-client interactions
 * of a sequential prefix and concurrent suffix.
 */
public class ProbabilisticExplorer extends Stoppable {
    private final BiMap<Interaction, Integer> interactionMap;
    private final SeleniumRunner runner;
    private final Combinator combinator;
    private final AppearanceMap appearanceMap;
    private static final double newSequenceProbability = 0.5;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ProbabilisticExplorer(Set<Interaction> interactions, SeleniumRunner runner, Combinator combinator) {
        int i = 0;
        interactionMap = HashBiMap.create(interactions.size());
        for (Interaction act : interactions) {
            interactionMap.put(act, i);
            i++;
        }
        appearanceMap = new AppearanceMap();
        this.runner = runner;
        this.combinator = combinator;
    }

    public void explore(int maxSequenceLength, Duration timeout) throws InterruptedException {
        Instant before = Instant.now();

        List<Set<Transition>> executedSequences = new ArrayList<>();
        runState = RunState.RUNNING;

        boolean foundError = false;
        do {
            if (isStopped())
                break;

            double conflictCount = appearanceMap.values().stream().filter(AppearanceList::isConflicted)
                    .mapToInt(ArrayList::size).sum();
            logger.info(String.format("Conflict count: %f", conflictCount));

            if (Math.random() < newSequenceProbability || conflictCount == 0) {
                Set<Interaction> keySet = interactionMap.keySet();
                List<Transition> transitions = new ArrayList<>();
                for (int sequenceLength = 0; sequenceLength < maxSequenceLength; sequenceLength++) {
                    int threadId = Math.random() < 0.5d ? 1 : 2;
                    double singleProp =  1.0d / keySet.size();
                    Iterator<Interaction> it = keySet.iterator();
                    int index = 1;
                    double randomValue = Math.random();
                    while (randomValue >= index * singleProp) {
                        it.next();
                        index++;
                    }

                    Interaction act = it.next();
                    transitions.add(new Transition(threadId, interactionMap.get(act)));
                }

                TransitionSequence sequence = new TransitionSequence(transitions);
                Set<Transition> transitionSet = new HashSet<>(sequence.getTransitions());
                if (executedSequences.stream().anyMatch(s -> s.equals(transitionSet))) {
                    // this was already generated
                    continue;
                }
                logger.info("Generated new sequence " + printSequence(sequence));
                logger.info(String.format("Replaying %s", sequence));
                Optional<List<Pair<UnorderedPair<Transition>, Appearance>>> appearanceList = run(sequence);
                executedSequences.add(new HashSet<>(sequence.getTransitions()));
                if (appearanceList.isPresent()) {
                    for (Pair<UnorderedPair<Transition>, Appearance> pair : appearanceList.get()) {
                        appearanceMap.add(pair.getLeft(), pair.getRight());
                    }
                } else {
                    foundError = true;
                }
            } else {
                Iterator<Map.Entry<UnorderedPair<Transition>, AppearanceList>> it = appearanceMap.entrySet().stream()
                        .filter(e -> e.getValue().size() > 0).iterator();

                double selectionProb = 1.0d / conflictCount;
                double randomValue = Math.random();
                int index = 1;

                Map.Entry<UnorderedPair<Transition>, AppearanceList> current = it.next();
                int localCount = current.getValue().size();
                while (randomValue >= index * selectionProb) {
                    localCount--;
                    if (localCount <= 0) {
                        current = it.next();
                        localCount = current.getValue().size();
                    }
                    index++;
                }

                Appearance appearance = current.getValue().get(current.getValue().size() - localCount);
                logger.info("Reordering appearance " + appearance);

                List<Transition> newTransitionList = new ArrayList<>();
                List<Transition> oldTransitionList = appearance.sequence.getTransitions();
                for (int i = 0; i < oldTransitionList.size(); i++) {
                    if (i == appearance.firstIndex) {
                        newTransitionList.add(oldTransitionList.get(appearance.secondIndex));
                    } else if (i == appearance.secondIndex) {
                        newTransitionList.add(oldTransitionList.get(appearance.firstIndex));
                    } else {
                        newTransitionList.add(oldTransitionList.get(i));
                    }
                }

                current.getValue().remove(appearance);

                TransitionSequence newSequence = new TransitionSequence(newTransitionList);
                logger.info("Generated reordered sequence " + printSequence(newSequence));
                logger.info(String.format("Replaying %s", newSequence));
                Optional<List<Pair<UnorderedPair<Transition>, Appearance>>> appearanceList = run(newSequence);
                if (appearanceList.isPresent()) {
                    for (Pair<UnorderedPair<Transition>, Appearance> pair : appearanceList.get()) {
                        if (!pair.getLeft().equals(current.getKey())) {
                            appearanceMap.add(pair.getLeft(), pair.getRight());
                        }
                    }
                } else {
                    foundError = true;
                }
            }

        } while (!timeout.minus(Duration.between(before, Instant.now())).isNegative() && !foundError);

        if (foundError) {
            logger.warning("Done with error");
        } else {
            logger.info("Done without error");
        }
    }

    private String printSequence(TransitionSequence sequence) {
        StringBuilder sb = new StringBuilder("{");
        List<Transition> transitions = sequence.getTransitions();
        for (Transition t: transitions) {
            sb.append(" ").append(interactionMap.inverse().get(t.sequenceId));
        }
        sb.append(" }");
        return sb.toString();
    }

    private Optional<List<Pair<UnorderedPair<Transition>, Appearance>>> run(TransitionSequence sequence) {
        List<Transition> transitions = sequence.getTransitions();
        List<Interaction> actsFirst = transitions.stream().filter(t -> t.threadId == 1)
                .map(t -> interactionMap.inverse().get(t.sequenceId)).collect(Collectors.toList());
        List<Interaction> actsSecond = transitions.stream().filter(t -> t.threadId == 2)
                .map(t -> interactionMap.inverse().get(t.sequenceId)).collect(Collectors.toList());
        TestSpec testSpec = new SetTest(combinator, actsFirst, actsSecond);

        Optional<List<List<ImageDiff>>> sequenceDiffs;
        try {
            sequenceDiffs = runner.runSequence(sequence, testSpec, true);
        } catch (InterruptedException | ImageDimensionException | IOException e) {
            return Optional.empty();
        }

        if (sequenceDiffs.isPresent()) {
            List<Pair<UnorderedPair<Transition>, Appearance>> appearanceList = new ArrayList<>();

            for (int i = 0; i < sequence.size(); i++) {
                Transition t_i = sequence.get(i);
                List<ImageDiff> thisTransitionDiffs = sequenceDiffs.get().get(i);
                for (int j = i + 1; j < sequence.size(); j++) {
                    Transition t_j = sequence.get(j);
                    List<ImageDiff> otherTransitionDiffs = sequenceDiffs.get().get(j);
                    boolean conflict = false;
                    try {
                        if (Images.diffsOverlap(thisTransitionDiffs.get(0), otherTransitionDiffs.get(0)) ||
                                Images.diffsOverlap(thisTransitionDiffs.get(1), otherTransitionDiffs.get(1))) {
                            conflict = true;
                        } else {
                            logger.info(String.format("Inferred independence between %s and %s", t_i, t_j));
                        }
                    } catch (ImageDimensionException e) {
                        conflict = true;
                    }

                    appearanceList.add(new ImmutablePair<>(new UnorderedPair<>(t_i, t_j),
                            new Appearance(sequence, i, j, conflict)));
                }
            }

            return Optional.of(appearanceList);
        } else {
            return Optional.empty();
        }
    }
}
