package de.crispda.sola.multitester.scenario;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.*;
import de.crispda.sola.multitester.web.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.function.BiConsumer;

public class CursorTest {
    @org.testng.annotations.Test
    public void gDocs() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator(false);
        combinator.setHideCursor(false);
        runTest(combinator, new GDocsCursorSender(), new GDocsCursorReceiver(), new GDocsInitLines(), null);
    }

    @org.testng.annotations.Test
    public void firepad() throws Exception {
        runTest(new FirepadCombinator(Firepad.url, false), new FirepadCursorSender(),
                new FirepadCursorReceiver(), new FirepadInitLines(), new FirepadWrite("AEIOU"));
    }

    @org.testng.annotations.Test
    public void firepad2() throws Exception {
        Combinator combinator = new FirepadCombinator(Firepad.url, false);
        Exchanges sender = new FirepadCursorSender();
        Exchanges receiver = new FirepadCursorReceiver();
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadWrite(Keys.LEFT, Keys.LEFT, Keys.LEFT),
                sender
        ));
        Test second = combinator.combine(Lists.newArrayList(
                receiver,
                new FirepadWrite("AEIOU")
        ));

        ArrayDeque<MaybeWait> firstSchedule = new ArrayDeque<>();
        ArrayDeque<MaybeWait> secondSchedule = new ArrayDeque<>();
        Arrays.asList(1, 1, 1).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
        Arrays.asList(2, 1, 0).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));

        runTest(first, second, firstSchedule, secondSchedule, sender, receiver, new FirepadInitLines(),
                Drivers.remoteDriver(true));
    }

    @org.testng.annotations.Test
    public void owncloud() throws Exception {
        OwncloudCombinator combinator = new OwncloudCombinator(Owncloud.url);
        combinator.setLogOut(false);
        Exchanges sender = new OwncloudCursorSender();
        Exchanges receiver = new OwncloudCursorReceiver();
        Test first = combinator.combine(Lists.newArrayList(
                new OwncloudWrite("a long text this is"),
                sender
        ));
        Test second = combinator.combine(Lists.newArrayList(
                receiver
        ));

        ArrayDeque<MaybeWait> firstSchedule = new ArrayDeque<>();
        ArrayDeque<MaybeWait> secondSchedule = new ArrayDeque<>();
        Arrays.asList(1, 1, 1).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
        Arrays.asList(2, 1).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));

        runTest(first, second, firstSchedule, secondSchedule, sender, receiver, new OwncloudInitEmpty(),
                Drivers.firefoxDriver(Firefox.ESR, true), (firstDriver, secondDriver) -> {
                    new Owncloud(firstDriver).logout();
                    new Owncloud(secondDriver).logout();
        });
    }

    private void runTest(Combinator combinator, Exchanges sender, Exchanges receiver, TestInit init,
                         Interaction additional) throws InterruptedException {
        Test first = combinator.combine(Lists.newArrayList(
                sender
        ));
        List<Interaction> secondActions = Lists.newArrayList(
                receiver
        );
        if (additional != null)
            secondActions.add(additional);
        Test second = combinator.combine(secondActions);

        ArrayDeque<MaybeWait> firstSchedule = new ArrayDeque<>();
        ArrayDeque<MaybeWait> secondSchedule = new ArrayDeque<>();

        if (additional == null) {
            Arrays.asList(1, 1).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
            Arrays.asList(1, 1).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));
        } else {
            Arrays.asList(1, 2).forEach(c -> firstSchedule.addLast(new MaybeWait(c)));
            Arrays.asList(1, 1, 1).forEach(c -> secondSchedule.addLast(new MaybeWait(c)));
        }

        runTest(first, second, firstSchedule, secondSchedule, sender, receiver, init, Drivers.remoteDriver(true));
    }

    private <T extends DriverSupplier & Quittable> void runTest(Test first, Test second,
                                                                ArrayDeque<MaybeWait> firstSchedule,
                                                                ArrayDeque<MaybeWait> secondSchedule,
                                                                Exchanges sender, Exchanges receiver, TestInit init,
                                                                T supplier) throws InterruptedException {
        runTest(first, second, firstSchedule, secondSchedule, sender, receiver, init, supplier,
                (d1, d2) -> {});
    }

    private <T extends DriverSupplier & Quittable> void runTest(Test first, Test second,
                                                                ArrayDeque<MaybeWait> firstSchedule,
                                                                ArrayDeque<MaybeWait> secondSchedule,
                                                                Exchanges sender, Exchanges receiver, TestInit init,
                                                                T supplier,
                                                                BiConsumer<WebDriver, WebDriver> f)
            throws InterruptedException {
        Exchanger<Point> exchanger = new Exchanger<>();
        sender.setExchanger(exchanger);
        receiver.setExchanger(exchanger);

        MultiTest<Test> test = new MultiTest<>(first, second);
        ScheduleVariant<Test> variant = new ScheduleVariant<>(test, firstSchedule, secondSchedule);

        ScheduleVariantExecutor executor = new ScheduleVariantExecutor(supplier);
        executor.setInit(init);
        executor.scheduleAll(Lists.newArrayList(variant));
        try {
            executor.execute();
        } finally {
            List<WrappedDriver> drivers = supplier.getDrivers();
            WrappedDriver d1 = drivers.get(0);
            WrappedDriver d2 = drivers.get(1);
            TestUtil.waitFor(supplier, () -> {
                f.accept(d1, d2);
                return null;
            });
        }
    }
}
