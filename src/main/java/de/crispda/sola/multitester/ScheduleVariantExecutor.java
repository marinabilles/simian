package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.web.DriverSupplier;
import org.openqa.selenium.WebDriver;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;

public class ScheduleVariantExecutor extends AbstractExecutor<ScheduleVariant<?>> {
    private TestInit init;
    private boolean cleanUp = true;
    private List<Rectangle> exclusionRectangles = new ArrayList<>();
    private List<Interaction> neutralEvents = new ArrayList<>();

    public ScheduleVariantExecutor(DriverSupplier driverSupplier) {
        super(driverSupplier);
    }

    public ScheduleVariantExecutor(DriverSupplier driverSupplier, List<Rectangle> exclusionRectangles,
                                   List<Interaction> neutralEvents) {
        super(driverSupplier);
        this.exclusionRectangles = exclusionRectangles;
        this.neutralEvents = neutralEvents;
    }

    @Override
    protected void executeTest(final ScheduleVariant<?> variant) throws ExecutionException,
            InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final Exchanger<Boolean> exchanger = new Exchanger<>();
        final Test first = variant.getFirstTest();
        final Test second = variant.getSecondTest();

        final WebDriver firstDriver = driverSupplier.get(1);
        final WebDriver secondDriver = driverSupplier.get(2);
        firstDriver.get(first.getInitialURL());
        secondDriver.get(second.getInitialURL());
        if (init != null) {
            init.run(firstDriver);
        }

        first.setup(barrier, firstDriver, variant.getFirstSchedule(), exchanger, exclusionRectangles, neutralEvents);
        second.setup(barrier, secondDriver, variant.getSecondSchedule(), exchanger, exclusionRectangles, neutralEvents);
        if (!neutralEvents.isEmpty()) {
            first.setNeutralEventsAtEnd(true);
            second.setNeutralEventsAtEnd(true);
        }

        final List<Test> tests = Lists.newArrayList(first, second);

        try {
            Tests.executeConcurrently(tests);
        } finally {
            beforeCleanUp(variant, tests);
            if (cleanUp)
                tests.forEach(Test::cleanUp);
        }
    }

    protected void beforeCleanUp(ScheduleVariant<?> variant, List<Test> tests) {}

    public void setInit(TestInit init) {
        this.init = init;
    }

    public void setCleanUp(boolean cleanUp) {
        this.cleanUp = cleanUp;
    }
}
