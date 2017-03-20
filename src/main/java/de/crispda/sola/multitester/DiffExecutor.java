package de.crispda.sola.multitester;

import de.crispda.sola.multitester.util.ImageFrame;
import de.crispda.sola.multitester.web.DriverSupplier;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class DiffExecutor extends AbstractExecutor<Test> {
    private final List<ImageDiff> diffs;
    private final List<BufferedImage> images;
    private TestInit init;
    private List<Rectangle> exclusionRectangles;

    public DiffExecutor(DriverSupplier driverSupplier) {
        super(driverSupplier);
        diffs = new ArrayList<>();
        images = new ArrayList<>();
        exclusionRectangles = new ArrayList<>();
    }

    public List<ImageDiff> getImageDiffs() {
        return diffs;
    }

    public List<BufferedImage> getImages() {
        return images;
    }

    public void setInit(TestInit init) {
        this.init = init;
    }

    public void setExclusionRectangles(List<Rectangle> exclusionRectangles) {
        this.exclusionRectangles = exclusionRectangles;
    }

    @Override
    protected void executeTest(Test test) throws ExecutionException,
            InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(1);
        final Deque<MaybeWait> schedule = new ArrayDeque<>();
        IntStream.range(0, test.getMaybeWaitCount())
                .forEach(i -> schedule.addLast(new MaybeWait(0)));
        final WebDriver driver = driverSupplier.get(1);
        driver.get(test.getInitialURL());
        if (init != null)
            init.run(driver);

        final TakesScreenshot scrshotter = (TakesScreenshot) driver;
        final byte[] beforeScr = scrshotter.getScreenshotAs(OutputType.BYTES);
        images.add(Images.toImage(beforeScr));

        test.setup(barrier, driver, schedule, new Exchanger<>());

        final ExecutorService esService = Executors.newSingleThreadExecutor();
        final Future<?> future = esService.submit(test);
        esService.shutdown();
        try {
            future.get();
        } finally {
            final byte[] afterScr = scrshotter.getScreenshotAs(OutputType.BYTES);
            images.add(Images.toImage(afterScr));
            test.cleanUp();
            try {
                diffs.add(Images.getDiff(beforeScr, afterScr, exclusionRectangles));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ImageDimensionException e) {
                try {
                    InputStream beforeIS = new ByteArrayInputStream(beforeScr);
                    Image before = ImageIO.read(beforeIS);
                    beforeIS.close();
                    InputStream afterIS = new ByteArrayInputStream(beforeScr);
                    Image after = ImageIO.read(afterIS );
                    afterIS.close();
                    ImageFrame.showImages(before, after);
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
    }
}
