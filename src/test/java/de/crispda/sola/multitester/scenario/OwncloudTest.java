package de.crispda.sola.multitester.scenario;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.*;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.web.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OwncloudTest {
    @org.testng.annotations.Test
    public void owncloudTest() throws Exception {
        List<Interaction> interactions = Lists.newArrayList(
                new OwncloudDecreaseFontSize(Selection.LineAfter),
                new OwncloudDecreaseFontSize(Selection.LineAfter),
                new OwncloudDecreaseFontSize(Selection.LineAfter),
                new OwncloudWrite("text"),
                new OwncloudWrite(Keys.RETURN, "more text")
        );
        Arrays.asList(Owncloud.Button.values()).forEach(b ->
                interactions.add(new OwncloudClickButton(b)));
        Arrays.asList(Owncloud.Font.values()).forEach(f ->
                interactions.add(new OwncloudMakeFont(Selection.LineBefore, f)));
        Arrays.asList(Owncloud.Modification.values()).forEach(m ->
                interactions.add(new OwncloudApplyModification(Selection.LineBefore, m))
        );
        testOwncloudInteractions(interactions);
    }

    @org.testng.annotations.Test
    public void owncloudDeleteTest() throws Exception {
        List<Interaction> interactions = Lists.newArrayList(
                new OwncloudWrite("An example text"),
                new OwncloudDelete(Selection.WordBefore),
                new OwncloudDelete(Selection.WordBefore),
                new OwncloudDelete(Selection.WordBefore)
        );

        testOwncloudInteractions(interactions);
    }

    @org.testng.annotations.Test
    public void owncloudMoveTest() throws Exception {
        List<Interaction> interactions = Lists.newArrayList(
                new OwncloudGotoEnd(),
                new OwncloudGotoHome()
        );
        Lists.newArrayList(Keys.RIGHT, Keys.END, Keys.LEFT, Keys.HOME).forEach(k ->
                interactions.add(new OwncloudWrite(k)));
        testOwncloudInteractions(interactions);
    }

    @org.testng.annotations.Test
    public void owncloudFontTest() throws Exception {
        List<Interaction> interactions = Lists.newArrayList(
                new OwncloudWrite("a"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.arial)
        );
        testOwncloudInteractions(interactions);
    }

    private void testOwncloudInteractions(List<Interaction> interactions) throws InterruptedException {
        OwncloudCombinator combinator = new DebugCombinator(Owncloud.url);
        Test octest = combinator.combine(interactions);

        SingleThreadedExecutor executor = new SingleThreadedExecutor(Drivers.firefoxDriver(Firefox.ESR));
        executor.setInit(new OwncloudInitEmpty());
        executor.scheduleAll(Lists.newArrayList(octest));
        executor.execute();
        Thread.sleep(2000);
    }

    @org.testng.annotations.Test
    public void testOwncloudSetFontSize() throws Exception {
        Combinator combinator = new OwncloudCombinator(Owncloud.url);
        OwncloudInteraction setFontSize = new OwncloudInteraction() {
            @Override
            public void perform() throws IOException, InterruptedException {
                owncloud.select(Selection.LineBefore);
                WebElement toolbar = driver.findElement(By.id("toolbar"));
                WebElement numberSpinner = toolbar.findElement(
                        By.xpath(".//input[@name='FontPicker']/ancestor::table[@role='listbox']" +
                                "/following-sibling::div[1]"));
                WebElement spinButtonInput = numberSpinner.findElement(By.xpath(".//input[@role='spinbutton']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = 24",  spinButtonInput);
                spinButtonInput.click();
                owncloud.deselect();
            }
        };
        Test octest = combinator.combine(Lists.newArrayList(
                new OwncloudWrite("a"),
                new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                setFontSize
        ));
        FirefoxDriverSupplier supplier = Drivers.firefoxDriver(Firefox.ESR, true);
        SingleThreadedExecutor executor = new SingleThreadedExecutor(supplier);
        executor.scheduleAll(Lists.newArrayList(octest));
        try {
            executor.execute();
        } finally {
            supplier.getDrivers().forEach(WebDriver::quit);
        }
    }

    private class DebugCombinator extends OwncloudCombinator {
        public DebugCombinator(String url) {
            super(url);
        }

        @Override
        public CombinedTest combine(List<Interaction> scenarios) {
            List<OwncloudInteraction> owncloudInteractions =
                    scenarios.stream().map(s -> (OwncloudInteraction) s).collect(Collectors.toList());
            final int maybeWaitCount = scenarios.size() + 1;
            return new CombinedTest() {
                @Override
                public List<Interaction> getInteractions() {
                    return scenarios;
                }

                @Override
                public void test() throws Exception {
                    Owncloud owncloud = new Owncloud(driver);
                    owncloud.login();
                    WebElement shareButton = driver.findElement(By.id("odf-invite"));

                    try {
                        maybeWait();
                        for (OwncloudInteraction scenario : owncloudInteractions) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].textContent = arguments[1];",
                                    shareButton, scenario.toString());

                            scenario.setDriver(driver);
                            try {
                                scenario.perform();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Thread.sleep(7000);
                            maybeWait();
                        }
                    } finally {
                        owncloud.logout();
                    }
                }

                @Override
                public int getMaybeWaitCount() {
                    return maybeWaitCount;
                }

                @Override
                public String getInitialURL() {
                    return url;
                }
            };
        }
    }

    @org.testng.annotations.Test
    public void owncloudDoubleTest() throws Exception {
        DriverSupplier supplier = Drivers.firefoxDriver(Firefox.ESR);
        WebDriver first = supplier.get(1);
        WebDriver second = supplier.get(2);
        try {
            OwncloudCombinator combinator = new OwncloudCombinator(Owncloud.url);
            List<Interaction> intlist = Lists.newArrayList(new OwncloudInteraction() {
               @Override
               public void perform() throws IOException, InterruptedException {
               }
            });
            Test octest1 = combinator.combine(intlist);
            Test octest2 = combinator.combine(intlist);

            Deque<MaybeWait> sched1 = new ArrayDeque<>();
            IntStream.range(0, 2).forEach(i -> sched1.add(new MaybeWait(1)));
            Deque<MaybeWait> sched2 = new ArrayDeque<>();
            IntStream.range(0, 2).forEach(i -> sched2.add(new MaybeWait(1)));
            CyclicBarrier barrier = new CyclicBarrier(2);
            Exchanger<Boolean> exchanger = new Exchanger<>();
            octest1.setup(barrier, first, sched1, exchanger);
            first.get(octest1.getInitialURL());
            octest2.setup(barrier, second, sched2, exchanger);
            second.get(octest2.getInitialURL());

            Tests.executeConcurrently(Lists.newArrayList(octest1, octest2));

            Thread.sleep(2000);
        } finally {
            first.quit();
            second.quit();
        }
    }

    @org.testng.annotations.Test
    public void owncloudInitTest() throws Exception {
        FirefoxDriverSupplier supplier = Drivers.firefoxDriver(Firefox.ESR, true);
        WebDriver driver = supplier.get(1);
        Owncloud owncloud = new Owncloud(driver);
        try {
            driver.get(Owncloud.url);
            TestInit init = new OwncloudInitEmpty();
            init.run(driver);
            byte[] scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            FileUtils.writeByteArrayToFile(new File(Paths.get("execution") + "/owncloud.png"), scr);
        } finally {
            TestUtil.waitFor(supplier, () -> {
                System.out.println("Executing logout...");
                owncloud.logout();
                return null;
            });
        }
    }

    @org.testng.annotations.Test
    public void ac5test() throws Exception {
        FirefoxDriverSupplier supplier = Drivers.firefoxDriver(Firefox.ESR, true);
        OwncloudCombinator combinator = new OwncloudCombinator(Owncloud.url);
        combinator.setLogOut(false);
        CombinedTest test = combinator.combine(Lists.newArrayList(
                new OwncloudWrite("test"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                new OwncloudMakeFontSize18(Selection.LineBefore),
                new OwncloudMakeFontSize18(Selection.LineBefore)
        ));
        TestInit init = new OwncloudInitEmpty();
        SingleThreadedExecutor executor = new SingleThreadedExecutor(supplier);
        executor.setInit(init);
        executor.scheduleAll(Lists.newArrayList(test));
        try {
            executor.execute();
        } finally {
            WebDriver driver = supplier.getDrivers().get(0);
            TestUtil.waitFor(supplier, () -> {
                new Owncloud(driver).logout();
                return null;
            });
        }
    }
}
