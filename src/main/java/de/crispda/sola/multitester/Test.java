package de.crispda.sola.multitester;

import de.crispda.sola.multitester.scenario.Adaptable;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;

public abstract class Test extends Adaptable implements Runnable {
    protected WebDriver driver;
    private CyclicBarrier barrier;
    private Exchanger<Boolean> exchanger;
    private Deque<MaybeWait> schedule;
    private List<byte[]> screenshotList;
    private List<Rectangle> exclusionRectangles;
    private List<Interaction> neutralEvents;
    private List<byte[]> testFailures = new ArrayList<>();
    private FailureReason failureReason;
    private boolean wasFirst;
    private int index;
    private boolean neutralEventsAtEnd = false;

    public void setup(CyclicBarrier barrier, WebDriver driver, Deque<MaybeWait> schedule,
                      Exchanger<Boolean> exchanger) {
        this.barrier = barrier;
        this.driver = driver;
        this.schedule = schedule;
        this.exchanger = exchanger;
        screenshotList = new ArrayList<>();
        exclusionRectangles = new ArrayList<>();
        neutralEvents = new ArrayList<>();
        testFailures = new ArrayList<>();
        failureReason = null;
        index = -1;
    }

    public void setup(CyclicBarrier barrier, WebDriver driver, Deque<MaybeWait> schedule,
                      Exchanger<Boolean> exchanger, List<Rectangle> exclusionRectangles,
                      List<Interaction> neutralEvents) {
        setup(barrier, driver, schedule, exchanger);
        this.exclusionRectangles = exclusionRectangles;
        this.neutralEvents = neutralEvents;
    }

    @Override
    public void run() {
        try {
            test();
            if (neutralEventsAtEnd) {
                try {
                    runBothNeutralEvents(barrier.await());
                } catch (Exception se) {
                    screenshotList.add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
                    throw se;
                }
                screenshotList.add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void test() throws Exception;

    public abstract int getMaybeWaitCount();

    public abstract String getInitialURL();

    /*
     * When running neutral events in a thread at each step, we need to make sure that only one thread at a time
     * runs its neutral events.
     * Based on the index returned from the barrier, we can identify one of the two threads to go first
     */
    protected void maybeWait() throws BrokenBarrierException, InterruptedException, IOException,
            ImageDimensionException, StateDifferenceException {
        for (int waitCount = schedule.removeFirst().getWaitCount(); waitCount > 0; waitCount--) {
            int barrierIndex = barrier.await();
            screenshotList.add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            if (!neutralEvents.isEmpty() && !neutralEventsAtEnd) {
                runBothNeutralEvents(barrierIndex);
            }
        }

        index++;
    }

    private void runBothNeutralEvents(int barrierIndex) throws InterruptedException, BrokenBarrierException,
            ImageDimensionException, StateDifferenceException, IOException {
        if (barrierIndex == 0) {
            wasFirst = true;
            runNeutralEvents(true);
            runNeutralEvents(false);
        } else {
            wasFirst = false;
            runNeutralEvents(false);
            runNeutralEvents(true);
        }
    }

    /*
     * When a neutral event is executed, both threads need to compare their states before and after.
     * This is true for the executing as well as the non-executing thread, which is why the functionality
     * for both is combined here.
     * If one thread has a difference, the other thread needs to be notified with the Exchanger, so that
     * both threads converge to the same value of the "failed" variable.
     */
    private void runNeutralEvents(boolean execute) throws BrokenBarrierException, InterruptedException, IOException,
            ImageDimensionException, StateDifferenceException {
        TakesScreenshot scrshotter = (TakesScreenshot) driver;
        for (Interaction neutralEvent : neutralEvents) {
            byte[] before = scrshotter.getScreenshotAs(OutputType.BYTES);
            barrier.await();
            if (execute) {
                neutralEvent.setDriver(driver);
                neutralEvent.perform();
            }
            barrier.await();

            byte[] after = scrshotter.getScreenshotAs(OutputType.BYTES);
            ImageDiff diff = Images.getDiff(before, after, exclusionRectangles);

            boolean failed;
            if (diff.hasDifference()) {
                testFailures.add(before);
                testFailures.add(after);
                exchanger.exchange(true);
                failed = true;
                failureReason = new FailureReason(neutralEvent.getClass().getName(), index, execute);
            } else {
                failed = exchanger.exchange(false);
                if (failed) {
                    testFailures.add(before);
                    testFailures.add(after);
                }
            }

            barrier.await();

            if (failed) {
                throw new StateDifferenceException(neutralEvent.getClass().getName());
            }
        }

        barrier.await();
    }

    public void cleanUp() {
        driver.quit();
    }

    public List<byte[]> getScreenshotList() {
        return screenshotList;
    }

    public List<byte[]> getTestFailures() {
        return testFailures;
    }

    public FailureReason getFailureReason() {
        return failureReason;
    }

    public int getIndex() {
        return index;
    }

    public void setNeutralEventsAtEnd(boolean neutralEventsAtEnd) {
        this.neutralEventsAtEnd = neutralEventsAtEnd;
    }

    public boolean getWasFirst() {
        return wasFirst;
    }
}
