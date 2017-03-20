package de.crispda.sola.multitester;

import de.crispda.sola.multitester.web.DriverSupplier;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class AbstractExecutor<V> implements TestExecutor<V> {
    private final List<V> testList;
    protected final DriverSupplier driverSupplier;

    public AbstractExecutor(DriverSupplier driverSupplier) {
        testList = new ArrayList<>();
        this.driverSupplier = driverSupplier;
    }

    @Override
    public void scheduleAll(Collection<V> tests) {
        this.testList.addAll(tests);
    }

    @Override
    public void execute() {
        final int size = testList.size();
        int successful = 0;
        for (int i = 0; i < size; i++) {
            V test = testList.get(i);
            try {
                System.out.println(String.format("Executing test %d of %d:", i + 1, size));
                System.out.println(test.toString());
                executeTest(test);
                System.out.println(String.format("Test %d of %d successful:", i + 1, size));
                System.out.println(test.toString());
                successful++;
            } catch (ExecutionException | InterruptedException | RuntimeException e) {
                e.printStackTrace();
                System.err.println(String.format("Test %d of %d failed:", i + 1, size));
                System.err.println(test.toString());
            }
        }
        System.out.println(String.format("Done with %d tests", size));
        System.out.println(String.format("Successful: %d", successful));
        System.out.println(String.format("Error: %d", size - successful));
        Assert.assertEquals(size - successful, 0);
    }

    protected abstract void executeTest(V test) throws ExecutionException, InterruptedException;
}
