package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.scenario.GDocs;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CalculationTime {
    public static void main(String[] args) {
        int actionSetSize = args.length < 1 ? GDocs.actionSet.size() : Integer.parseInt(args[0]);
        System.out.println("Action set size: " + actionSetSize);
        int[] depths = {1, 2, 3, 4};
        for (int depth : depths) {
            long noExecutions = new Double(Math.pow(actionSetSize, depth)).longValue();
            Duration seqDur = Duration.ofSeconds(7)
                    .plus(Duration.ofMillis(818))
                    .multipliedBy(noExecutions);
            System.out.println("depth " + depth + ": " + noExecutions + " executions");
            System.out.print("\tSequential step: " + seqDur.toString());
            System.out.print(" (" + seqDur.toHours() / 24 + " days)\n");

            long parallelEx = IntStream.range(0, depth).map(i ->
                    new Double(Math.pow(actionSetSize, i)).intValue()).sum() *
                    CombinatoricsUtils.binomialCoefficient(actionSetSize, 2);
            System.out.println("\tParallel executions: " + parallelEx);

            List<Pair<String, Duration>> parDurs = new ArrayList<>();

            parDurs.add(new ImmutablePair<>("Original", Duration.ofSeconds(29).plus(Duration.ofMillis(765))));
            parDurs.add(new ImmutablePair<>("GDocs-AS10", Duration.ofSeconds(57).plus(Duration.ofMillis(163))));
            parDurs.add(new ImmutablePair<>("Firepad-AS10", Duration.ofSeconds(44).plus(Duration.ofMillis(126))));
            parDurs.add(new ImmutablePair<>("Owncloud-AS10", Duration.ofSeconds(82).plus(Duration.ofMillis(26))));

            for (Pair<String, Duration> parDur : parDurs) {
                Duration totalDur = parDur.getRight().multipliedBy(parallelEx);
                System.out.print("\tParallel step " + parDur.getLeft() + ": " + totalDur.toString());
                System.out.print(" (" + totalDur.toHours() / 24 + " days)\n");
            }
        }
    }
}
