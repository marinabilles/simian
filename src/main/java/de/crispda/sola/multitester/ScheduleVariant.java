package de.crispda.sola.multitester;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;

public class ScheduleVariant<T extends Test> {
    private final ArrayDeque<MaybeWait> actionsFirst;
    private final ArrayDeque<MaybeWait> actionsSecond;
    private final MultiTest<T> multiTest;

    public ScheduleVariant(MultiTest<T> multiTest) {
        actionsFirst = new ArrayDeque<>();
        actionsSecond = new ArrayDeque<>();
        this.multiTest = multiTest;
    }

    public ScheduleVariant(MultiTest<T> multiTest, ArrayDeque<MaybeWait> actionsFirst,
                           ArrayDeque<MaybeWait> actionsSecond) {
        this.multiTest = multiTest;
        this.actionsFirst = actionsFirst.clone();
        this.actionsSecond = actionsSecond.clone();
    }

    public void queueFirst(MaybeWait maybeWait) {
        actionsFirst.addLast(maybeWait);
    }

    public void queueSecond(MaybeWait maybeWait) {
        actionsSecond.addLast(maybeWait);
    }

    public Deque<MaybeWait> getFirstSchedule() {
        return actionsFirst.clone();
    }

    public Deque<MaybeWait> getSecondSchedule() {
        return actionsSecond.clone();
    }

    public T getFirstTest() {
        return multiTest.getFirstTest();
    }

    public T getSecondTest() {
        return multiTest.getSecondTest();
    }

    public void incrementFirst(int diff) {
        MaybeWait last = actionsFirst.getLast();
        last.setWaitCount(last.getWaitCount() + diff);
    }

    public void incrementSecond(int diff) {
        MaybeWait last = actionsSecond.getLast();
        last.setWaitCount(last.getWaitCount() + diff);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        sb.append(getFirstTest().getClass().getSimpleName()).append(" ");
        actionsFirst.forEach(m -> sb.append(m).append(" "));
        sb.append("\n");
        sb.append(getSecondTest().getClass().getSimpleName()).append(" ");
        actionsSecond.forEach(m -> sb.append(m).append(" "));
        sb.append("\n");
        return sb.toString();
    }

    public List<String> toSequence() {
        final List<String> sequence = new ArrayList<>();
        int firstIndex = 0, secondIndex = 0, lastFirstIndex = -1, lastSecondIndex = -1;
        final MaybeWait[] firstArray = actionsFirst.toArray(new MaybeWait[0]),
                secondArray = actionsSecond.toArray(new MaybeWait[0]);
        int firstWaitCount = 0, secondWaitCount = 0;
        while (firstIndex < firstArray.length && secondIndex < secondArray.length) {
            if (firstIndex != lastFirstIndex)
                firstWaitCount = firstArray[firstIndex].getWaitCount();
            if (secondIndex != lastSecondIndex)
                secondWaitCount = secondArray[secondIndex].getWaitCount();

            lastFirstIndex = firstIndex;
            lastSecondIndex = secondIndex;
            if (firstWaitCount < secondWaitCount) {
                secondWaitCount -= firstWaitCount;
                firstIndex++;
                sequence.add("First." + firstIndex);
            } else if (secondWaitCount < firstWaitCount) {
                firstWaitCount -= secondWaitCount;
                secondIndex++;
                sequence.add("Second." + secondIndex);
            } else {
                firstIndex++;
                secondIndex++;
                sequence.add("First." + firstIndex + " and Second." + secondIndex);
            }
        }

        return sequence;
    }

    public static ScheduleVariant<Test> createInitial(MultiTest<Test> multiTest) {
        // "sequential" variant, e.g.
        // first:  wait(1) wait(1) wait(2)
        // second: wait(3) wait(1)
        ScheduleVariant<Test> variant = new ScheduleVariant<>(multiTest);
        int firstCount = multiTest.getFirstMaybeWaitCount();
        int secondCount = multiTest.getSecondMaybeWaitCount();
        IntStream.rangeClosed(0, firstCount - 2)
                .forEach(i -> variant.queueFirst(new MaybeWait(1)));
        variant.queueFirst(new MaybeWait(secondCount));
        variant.queueSecond(new MaybeWait(firstCount));
        IntStream.rangeClosed(0, secondCount - 2)
                .forEach(i -> variant.queueSecond(new MaybeWait(1)));
        return variant;
    }
}
