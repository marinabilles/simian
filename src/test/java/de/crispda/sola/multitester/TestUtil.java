package de.crispda.sola.multitester;

import de.crispda.sola.multitester.util.FrameWaiter;
import de.crispda.sola.multitester.util.WaiterFrame;
import de.crispda.sola.multitester.web.Quittable;

import java.util.concurrent.CyclicBarrier;
import java.util.function.Supplier;

public class TestUtil {
    public static void waitFor(Quittable supplier) throws InterruptedException {
        waitFor(supplier, () -> null);
    }

    public static void waitFor(Quittable supplier, Supplier<?> f) throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(2);
        WaiterFrame frame = new WaiterFrame(barrier);
        frame.setVisible(true);
        Thread waiterThread = new FrameWaiter(barrier);
        waiterThread.start();
        waiterThread.join();

        f.get();
        supplier.manualQuit();
    }
}
