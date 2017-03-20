package de.crispda.sola.multitester.web;

import de.crispda.sola.multitester.Images;
import de.crispda.sola.multitester.TestWebServer;
import de.crispda.sola.multitester.scenario.GDocs;
import de.crispda.sola.multitester.util.ImageFrame;
import de.crispda.sola.multitester.util.WebServer;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.Image;

public class ScreenshotTest {
    private WebDriver driver;

    @BeforeMethod
    public void setUp() throws InterruptedException {
        driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    private void screenshotOf(String url) throws InterruptedException {
        driver.get(url);
        final byte[] screenshot =
                ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        ImageFrame.showImage(screenshot);
    }

    @Test
    public void showInputHtml() throws Exception {
        WebServer server = new TestWebServer();
        try {
            screenshotOf(server.get("input.html"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void showGDocs() throws Exception {
        screenshotOf(GDocs.url);
    }

    @Test
    public void difference() throws Exception {
        WebServer server = new TestWebServer();
        try {
            driver.get(server.get("input.html"));
            TakesScreenshot scrshotter = (TakesScreenshot) driver;
            final byte[] beforeScr = scrshotter.getScreenshotAs(OutputType.BYTES);
            final WebElement inp = driver.findElement(By.id("inp"));
            inp.sendKeys("Keys");
            (new WebDriverWait(driver, 5))
                    .ignoring(StaleElementReferenceException.class)
                    .until(new PropertyCondition(inp, "value", "Keys"));
            final byte[] afterScr = scrshotter.getScreenshotAs(OutputType.BYTES);
            Image diff = Images.getDiffImage(beforeScr, afterScr);
            ImageFrame.showImage(diff);
        } finally {
            server.stop();
        }
    }
}
