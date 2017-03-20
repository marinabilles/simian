package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.scenario.GDocs;
import de.crispda.sola.multitester.scenario.GDocsInitLines;
import de.crispda.sola.multitester.scenario.GDocsTestWriteFirstLine;
import de.crispda.sola.multitester.scenario.GDocsTestWriteLastLine;
import de.crispda.sola.multitester.util.FrameWaiter;
import de.crispda.sola.multitester.util.SequenceImageFrame;
import de.crispda.sola.multitester.web.DriverSupplier;
import de.crispda.sola.multitester.web.Drivers;
import de.crispda.sola.multitester.web.Firefox;
import org.testng.Assert;

import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class DiffMultiTests {
    @org.testng.annotations.Test
    public void testMultiDiffs() throws Exception {
        final List<Test> tests = Lists.newArrayList(
                new GDocsTestWriteLastLine(),
                new GDocsTestWriteFirstLine()
        );

        final MultiTestGenerator generator = new SimpleMultiTestGenerator();
        generator.putAll(tests);
        final Set<MultiTest<Test>> multiTests = generator.generateTests();
        Assert.assertEquals(multiTests.size(), 1);

//        final VariantCreator variantCreator = new SimpleVariantCreator();
//        variantCreator.putAll(multiTests);
//        final List<ScheduleVariant> variants = variantCreator.createVariants();
//        Assert.assertEquals(variants.size(), 1);
        ScheduleVariant<Test> theVariant = new ScheduleVariant<>(multiTests.stream().findFirst()
                .orElseThrow(RuntimeException::new));
        theVariant.queueFirst(new MaybeWait(1));
        theVariant.queueFirst(new MaybeWait(2));
        theVariant.queueFirst(new MaybeWait(1));
        theVariant.queueSecond(new MaybeWait(2));
        theVariant.queueSecond(new MaybeWait(2));
        List<ScheduleVariant<?>> variants = Lists.newArrayList(theVariant);

        final DiffMultiExecutor executor = new DiffMultiExecutor(Drivers.firefoxDriver(Firefox.ESR));
        executor.setInit(new GDocsInitLines());
        executor.scheduleAll(variants);
        executor.execute();

        final Map<ScheduleVariant<?>, List<List<byte[]>>> screenshotsMap = executor.getScreenshotsMap();
        Assert.assertEquals(screenshotsMap.size(), 1);
        final ScheduleVariant<?> variant = variants.get(0);
        Assert.assertTrue(screenshotsMap.containsKey(variant));
        final List<List<byte[]>> screenshots = screenshotsMap.get(variant);
        Assert.assertEquals(screenshots.size(), 2);
        final List<byte[]> screenshotsFirst = screenshots.get(0);
        final List<byte[]> screenshotsSecond = screenshots.get(1);

        final int screenshotCount = screenshotsFirst.size();

        List<List<ImageDiff>> sequenceDiffs = new ArrayList<>();
        for (int i = 0; i < screenshotCount - 1; i++) {
            List<ImageDiff> clientDiffs = new ArrayList<>();
            byte[] firstBefore = screenshotsFirst.get(i);
            byte[] firstAfter = screenshotsFirst.get(i + 1);
            clientDiffs.add(Images.getDiff(firstBefore, firstAfter, GDocs.exclusionRectangles));
            byte[] secondBefore = screenshotsSecond.get(i);
            byte[] secondAfter = screenshotsSecond.get(i + 1);
            clientDiffs.add(Images.getDiff(secondBefore, secondAfter, GDocs.exclusionRectangles));
            sequenceDiffs.add(clientDiffs);
        }

        final CyclicBarrier barrier = new CyclicBarrier(2);
        final List<String> sequence = variant.toSequence();
        sequence.remove(sequence.size() - 1);
        final SequenceImageFrame frame = new SequenceImageFrame(sequenceDiffs, sequence, barrier);
        frame.setVisible(true);
        final Thread waiterThread = new FrameWaiter(barrier);
        waiterThread.start();
        waiterThread.join();
    }

    private class DiffMultiExecutor extends ScheduleVariantExecutor {
        private final Map<ScheduleVariant<?>, List<List<byte[]>>> screenshotsMap;

        DiffMultiExecutor(DriverSupplier driverSupplier) {
            super(driverSupplier);
            screenshotsMap = new HashMap<>();
        }

        Map<ScheduleVariant<?>, List<List<byte[]>>> getScreenshotsMap() {
            return screenshotsMap;
        }

        @Override
        protected void beforeCleanUp(ScheduleVariant<?> variant, List<Test> tests) {
            final List<List<byte[]>> screenshots = new ArrayList<>();
            tests.forEach((t) -> screenshots.add(t.getScreenshotList()));
            screenshotsMap.put(variant, screenshots);
        }
    }
}
