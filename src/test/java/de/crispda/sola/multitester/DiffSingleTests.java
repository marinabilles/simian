package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.scenario.GDocs;
import de.crispda.sola.multitester.scenario.GDocsInitLines;
import de.crispda.sola.multitester.scenario.GDocsTestSelectAndDelete;
import de.crispda.sola.multitester.scenario.GDocsTestWrite;
import de.crispda.sola.multitester.util.ImageFrame;
import de.crispda.sola.multitester.web.DriverSupplier;
import de.crispda.sola.multitester.web.Drivers;
import de.crispda.sola.multitester.web.Firefox;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.*;
import org.testng.Assert;

import java.awt.image.BufferedImage;
import java.util.List;

public class DiffSingleTests {
    @org.testng.annotations.Test
    public void diffsOverlap() throws Exception {
        List<Test> tests = Lists.newArrayList(new GDocsTestWrite(),
                new GDocsTestSelectAndDelete());
        DriverSupplier driverSupplier = Drivers.firefoxDriver(Firefox.ESR);
        DiffExecutor executor = new DiffExecutor(driverSupplier);
        executor.scheduleAll(tests);
        executor.execute();

        List<ImageDiff> diffs = executor.getImageDiffs();
        Assert.assertEquals(diffs.size(), 2);
        ImageFrame.showImages(diffs.get(0), diffs.get(1));
        ImageFrame.showImage(diffs.get(0).getOverlapImage(diffs.get(1), false).orElseThrow(RuntimeException::new));
    }

    @org.testng.annotations.Test
    public void diffsNoOverlap() throws Exception {
        List<Test> tests = Lists.newArrayList(
                new Test () {
                    @Override
                    public void test() throws Exception {
                        final GDocs gDocs = new GDocs(driver);
                        WebElement lastLine = gDocs.getLastLine();
                        WebActions.click(lastLine);
                        gDocs.sendKeys(Keys.END, "ing");
                        Thread.sleep(2000);
                    }

                    @Override
                    public int getMaybeWaitCount() {
                        return 0;
                    }

                    @Override
                    public String getInitialURL() {
                        return GDocs.url;
                    }
                },
                new Test() {
                    @Override
                    public void test() throws Exception {
                        final GDocs gDocs = new GDocs(driver);
                        List<WebElement> lines = driver.findElements(
                                By.className(GDocs.lineClass));
                        WebElement secondLine = lines.get(1);
                        WebActions.click(secondLine);
                        gDocs.sendKeys(Keys.END, "ing");
                        Thread.sleep(2000);
                    }

                    @Override
                    public int getMaybeWaitCount() {
                        return 0;
                    }

                    @Override
                    public String getInitialURL() {
                        return GDocs.url;
                    }
                }
        );

        DriverSupplier supplier = Drivers.firefoxDriver(Firefox.ESR);
        DiffExecutor executor = new DiffExecutor(supplier);
        executor.scheduleAll(tests);
        executor.setInit(new GDocsInitLines());
        executor.execute();

        List<ImageDiff> diffs = executor.getImageDiffs();
        Assert.assertEquals(diffs.size(), 2);
        ImageFrame.showImages(diffs.get(0), diffs.get(1));
    }

    @org.testng.annotations.Test
    public void diffSingle() throws Exception {
        List<Test> tests = Lists.newArrayList(
                new Test () {
                    @Override
                    public void test() throws Exception {
                        final GDocs gDocs = new GDocs(driver);
                        WebElement lastLine = gDocs.getLastLine();
                        WebActions.click(lastLine);
                        gDocs.sendKeys(Keys.END, "ing");
                        driver.switchTo().defaultContent();
                        Thread.sleep(2000);
                    }

                    @Override
                    public int getMaybeWaitCount() {
                        return 0;
                    }

                    @Override
                    public String getInitialURL() {
                        return GDocs.url;
                    }
                }
        );

        DriverSupplier supplier = Drivers.firefoxDriver(Firefox.ESR);
        DiffExecutor executor = new DiffExecutor(supplier);
        executor.scheduleAll(tests);
        executor.setInit(new GDocsInitLines());
        executor.execute();

        List<ImageDiff> diffs = executor.getImageDiffs();
        Assert.assertEquals(diffs.size(), 1);
        ImageFrame.showImage(diffs.get(0).getImage());

        List<BufferedImage> images = executor.getImages();
        Assert.assertEquals(images.size(), 2);
    }

}
