package de.crispda.sola.multitester;

import de.crispda.sola.multitester.web.DriverSupplier;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class SingleThreadedExecutor extends AbstractExecutor<Test> {
    private TestInit init;
    private int maybeWaitCount = 0;

    public SingleThreadedExecutor(DriverSupplier driverSupplier) {
        super(driverSupplier);
    }

    public void setInit(TestInit init) {
        this.init = init;
    }

    @Override
    protected void executeTest(Test test) throws ExecutionException, InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(1);
        final Deque<MaybeWait> skipSchedule = new ArrayDeque<>();
        IntStream.range(0, test.getMaybeWaitCount())
                .forEach(i -> skipSchedule.addLast(new MaybeWait(maybeWaitCount)));
        final WebDriver driver = driverSupplier.get(1);
        driver.get(test.getInitialURL());
        test.setup(barrier, driver, skipSchedule, new Exchanger<>());
        if (init != null) {
            try {
                init.run(driver);
            } catch (WebDriverException e) {
                test.cleanUp();
                throw e;
            }
        }

        final ExecutorService esService = Executors.newSingleThreadExecutor();
        final Future<?> future = esService.submit(test);
        esService.shutdown();
        try {
            future.get();
        } finally {
            test.cleanUp();
        }
    }

    public void setMaybeWaitCount(int maybeWaitCount) {
        this.maybeWaitCount = maybeWaitCount;
    }
}
