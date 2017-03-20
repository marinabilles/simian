package de.crispda.sola.multitester.scenario;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import de.crispda.sola.multitester.*;
import de.crispda.sola.multitester.util.FrameWaiter;
import de.crispda.sola.multitester.util.WaiterFrame;
import de.crispda.sola.multitester.web.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class GDocsTest {
    @Test
    public void writeLine() throws InterruptedException, IOException {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        try {
            driver.get(GDocs.url);
            GDocs gdocs = new GDocs(driver);

            WebElement line = gdocs.getLastLine();
            WebActions.click(line);
            Actions builder = new Actions(driver);
            builder.keyDown(Keys.CONTROL)
                    .sendKeys("a")
                    .keyUp(Keys.CONTROL)
                    .perform();
            WebActions.sendKeys(line, "\b");
            Thread.sleep(500);

            line = gdocs.getLastLine();
            Optional<String> lineText = gdocs.getLineText(line);
            Assert.assertTrue(!lineText.isPresent() ||
                    lineText.get().trim().equals(""));


            Thread.sleep(500);
            WebActions.click(line);
            WebActions.sendKeys(line, "Test");
            Thread.sleep(500);

            line = gdocs.getLastLine();
            Optional<String> lineText1 = gdocs.getLineText(line);
            assert lineText1.isPresent();
            Assert.assertEquals(lineText1.get().trim(), "Test");
        } finally {
            driver.quit();
        }
    }

    @Test
    public void logKeys() throws Exception {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        try {
            driver.get(GDocs.url);
            WebElement eventTargetFrame = driver.findElement(By.className("docs-texteventtarget-iframe"));
            driver.switchTo().frame(eventTargetFrame);
            final JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript(Resources.toString(Resources.getResource("keyLogger.js"), Charsets.UTF_8));
            driver.switchTo().defaultContent();
            final GDocs gDocs = new GDocs(driver);
            WebElement lastLine = gDocs.getLastLine();
            WebActions.click(lastLine);
            final Actions builder = new Actions(driver);
            builder.keyDown(Keys.CONTROL)
                    .sendKeys("a")
                    .keyUp(Keys.CONTROL)
                    .perform();
            WebActions.sendKeys(lastLine, "\n");
            gDocs.sendKeys(Keys.ENTER);
            gDocs.sendKeys(Keys.RETURN);
            Thread.sleep(500);
            driver.switchTo().frame(eventTargetFrame);
            WebElement keyLogger = driver.findElement(By.id("keyLogger"));
            System.out.println(keyLogger.getText());
        } finally {
            driver.quit();
        }
    }

    @Test
    public void modifications() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();
        List<Interaction> acts = new ArrayList<>();
        for (Selection s : Selection.values()) {
            Arrays.asList(GDocs.Modification.values()).forEach(m -> acts.add(new GDocsApplyModification(s, m)));
        }
        de.crispda.sola.multitester.Test test = combinator.combine(acts);
        SingleThreadedExecutor executor = new SingleThreadedExecutor(Drivers.firefoxDriver(Firefox.ESR));
        executor.scheduleAll(Lists.newArrayList(test));
        executor.execute();
    }

    @Test
    public void completeReset() throws Exception {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        TestInit init = new GDocsInitCompleteReset();
        try {
            driver.get(GDocs.url);
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(
                    By.className(GDocs.lineClass)));
            init.run(driver);
            CyclicBarrier barrier = new CyclicBarrier(2);
            WaiterFrame frame = new WaiterFrame(barrier);
            frame.setVisible(true);
            Thread waiterThread = new FrameWaiter(barrier);
            waiterThread.start();
            waiterThread.join();
        } finally {
            driver.quit();
        }
    }

    @Test
    public void readEmptyLine() throws Exception {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        try {
            driver.get(GDocs.url);
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(
                    By.className(GDocs.lineClass)));
            WebElement line = driver.findElement(By.cssSelector("." + GDocs.contentClass + " ." + GDocs.lineClass));
            String textContent = (String) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].textContent;", line);

            for (int i = 0; i < textContent.length(); i++) {
                char c = textContent.charAt(i);
                System.out.print((int) c + " ");
            }
            System.out.print("\n");

            System.out.println(textContent.substring(textContent.length() - 1).equals(" "));
            System.out.println(textContent.substring(textContent.length() - 1).equals("\u00A0"));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void fontTest() throws Exception {
        Combinator combinator = new GDocsFailsafeCombinator();
        TestInit init = new GDocsInitCompleteReset();
        List<Interaction> interactions = Lists.newArrayList(
                new GDocsWrite("1234"),
                new GDocsMakeFontSize(Selection.LineBefore, "18"),
                new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)
        );

        CombinedTest test = combinator.combine(interactions);

        RemoteDriverSupplier supplier = Drivers.remoteDriver(true);
        SingleThreadedExecutor executor = new SingleThreadedExecutor(supplier);
        executor.setInit(init);
        executor.scheduleAll(Lists.newArrayList(test));
        try {
            executor.execute();
        } finally {
            TestUtil.waitFor(supplier);
        }
    }
}
