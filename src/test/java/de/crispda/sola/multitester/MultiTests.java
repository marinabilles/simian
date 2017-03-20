package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.scenario.*;
import de.crispda.sola.multitester.web.DriverSupplier;
import de.crispda.sola.multitester.web.Drivers;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;

public class MultiTests extends TestFixture {
    @org.testng.annotations.Test
    public void gDocsWriteMulti() {
        List<Test> scenarios = Lists.newArrayList(
                new GDocsTestWrite(), new GDocsTestWrite());
        setInit(new GDocsInitEmpty());
        simpleScheduleTest(scenarios);
    }

    @org.testng.annotations.Test
    public void gDocsConflict() {
        List<Test> scenarios = Lists.newArrayList(
                new GDocsTestWrite(), new GDocsTestSelectAndDelete());
        setInit(new GDocsInitEmpty());
        simpleScheduleTest(scenarios);
    }

    @org.testng.annotations.Test
    public void stateEquivalenceTest() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();
        Test first = combinator.combine(Lists.newArrayList(
                new GDocsWrite("a"),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.bold)
        ));

        Test second = combinator.combine(Lists.newArrayList(
                new GDocsWrite(Keys.END, "b")
        ));

        // TODO: was a conflict detected between ()a and ()b ? do their areas of effect overlap starting from ()?
        // -> action of "first" probably does not have any overlap with other interaction
        // how to modify the scenario so this holds
        // then check that ()a||b doesn't cause an oracle positive
        // but the state ()ab can cause and ()ba cannot for the same pair

        System.out.println("run1:  make bold -> write b;  result: b is bold");
        Deque<MaybeWait> firstSchedule = new ArrayDeque<>();
        Arrays.asList(1, 0, 2).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
        Deque<MaybeWait> secondSchedule = new ArrayDeque<>();
        Arrays.asList(2, 1).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));
        runTest(first, second, firstSchedule, secondSchedule);

        List<byte[]> run1scrsFirst = new ArrayList<>(first.getScreenshotList());
        List<byte[]> run1scrsSecond = new ArrayList<>(second.getScreenshotList());

        System.out.println("run2:  write b -> make bold;  result: b is not bold");
        Arrays.asList(1, 2, 1).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
        Arrays.asList(2, 2).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));
        runTest(first, second, firstSchedule, secondSchedule);

        List<byte[]> run2scrsFirst = new ArrayList<>(first.getScreenshotList());
        List<byte[]> run2scrsSecond = new ArrayList<>(second.getScreenshotList());

        System.out.println("Are the output states of runs 1 & 2 different? " +
                (Images.getDiff(run1scrsFirst.get(run1scrsFirst.size() - 1),
                        run2scrsFirst.get(run2scrsFirst.size() - 1),
                        GDocs.exclusionRectangles)
                        .hasDifference() ||
                Images.getDiff(run1scrsSecond.get(run1scrsSecond.size() - 1),
                        run2scrsSecond.get(run2scrsSecond.size() - 1),
                        GDocs.exclusionRectangles)
                        .hasDifference()) + " (expected: true)");

        System.out.println("Do ()make bold and ()write b conflict? " +
                Images.getDiff(run1scrsFirst.get(1), run1scrsFirst.get(2)).overlapsWith(
                        Images.getDiff(run2scrsSecond.get(0), run2scrsSecond.get(1))) +
                " (expected: true)");

        Arrays.asList(1, 1, 1).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
        Arrays.asList(2, 1).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));
        runTest(first, second, firstSchedule, secondSchedule);

        System.out.println("Does (){make bold || write b} trigger the oracle? " +
                Images.getDiff(first.getScreenshotList().get(first.getScreenshotList().size() - 1),
                        second.getScreenshotList().get(second.getScreenshotList().size() - 1),
                        GDocs.exclusionRectangles).hasDifference() +
                " (expected: false)");

        Test firstExpanded = combinator.combine(Lists.newArrayList(
                new GDocsWrite("a"),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.bold),
                new GDocsWrite(Keys.END, "text")
        ));
        Test secondExpanded = combinator.combine(Lists.newArrayList(
                new GDocsWrite(Keys.END, "b"),
                new GDocsDelete(Selection.LineBefore)
        ));
        Arrays.asList(1, 0, 2, 1).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
        Arrays.asList(2, 1, 1).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));
        runTest(firstExpanded, secondExpanded, firstSchedule, secondSchedule);

        System.out.println("Does (){make bold, write b, delete || write text} trigger the oracle? " +
                Images.getDiff(firstExpanded.getScreenshotList().get(firstExpanded.getScreenshotList().size() - 1),
                        secondExpanded.getScreenshotList().get(secondExpanded.getScreenshotList().size() - 1),
                        GDocs.exclusionRectangles).hasDifference() +
                " (expected: true)");

        Arrays.asList(1, 2, 1, 1).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
        Arrays.asList(2, 2, 1).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));
        runTest(firstExpanded, secondExpanded, firstSchedule, secondSchedule);

        System.out.println("Does (){write b, make bold, delete || write text} trigger the oracle? " +
                Images.getDiff(firstExpanded.getScreenshotList().get(firstExpanded.getScreenshotList().size() - 1),
                        secondExpanded.getScreenshotList().get(secondExpanded.getScreenshotList().size() - 1),
                        GDocs.exclusionRectangles).hasDifference() +
                " (expected: false)");
    }

    private void runTest(Test first, Test second, Deque<MaybeWait> firstSchedule, Deque<MaybeWait> secondSchedule)
            throws InterruptedException, ExecutionException {
        DriverSupplier driverSupplier = Drivers.remoteDriver();
        WebDriver firstDriver = driverSupplier.get(1);
        WebDriver secondDriver = driverSupplier.get(2);
        try {
            firstDriver.get(first.getInitialURL());
            secondDriver.get(second.getInitialURL());
            CyclicBarrier barrier = new CyclicBarrier(2);
            Exchanger<Boolean> exchanger = new Exchanger<>();

            first.setup(barrier, firstDriver, firstSchedule, exchanger);
            second.setup(barrier, secondDriver, secondSchedule, exchanger);
            new GDocsInitCompleteReset().run(firstDriver);

            Tests.executeConcurrently(Lists.newArrayList(first, second));
        } finally {
            firstDriver.quit();
            secondDriver.quit();
        }
    }
}
