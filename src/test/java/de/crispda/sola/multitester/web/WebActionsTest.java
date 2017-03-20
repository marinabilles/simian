package de.crispda.sola.multitester.web;

import de.crispda.sola.multitester.TestWebServer;
import de.crispda.sola.multitester.util.WebServer;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

public class WebActionsTest {
    private static WebServer server;
    private WebDriver driver;

    @BeforeClass
    public void setUpClass() throws Exception {
        server = new TestWebServer();
    }

    @AfterClass
    public void tearDownClass() throws Exception {
        server.stop();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Test
    public void webActionsClick() throws Exception {
        driver.get(server.get("overlapping.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements");
        WebElement infront = driver.findElement(By.id("infront"));
        WebActions.click(infront);
        WebElement inbehind = driver.findElement(By.id("inbehind"));
        WebActions.click(inbehind);
        (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.textToBe(By.id("output"), "infront (66,66)\ninfront (66,66)"));
    }

    @Test(expectedExceptions = WebDriverException.class)
    public void behindSimpleClickCrashes() {
        driver.get(server.get("overlapping.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements");
        WebElement inbehind = driver.findElement(By.id("inbehind"));
        inbehind.click();
    }

    @Test(expectedExceptions = WebDriverException.class)
    public void behindActionClickCrashes() {
        driver.get(server.get("overlapping.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements");
        WebElement inbehind = driver.findElement(By.id("inbehind"));
        Actions builder = new Actions(driver);
        builder.moveToElement(inbehind)
                .click()
                .perform();
    }

    @Test
    public void frontSimpleClick() {
        driver.get(server.get("overlapping.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements");
        WebElement infront = driver.findElement(By.id("infront"));
        infront.click();
        (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.textToBe(By.id("output"), "infront (58,58)"));
    }

    @Test
    public void frontActionClick() {
        driver.get(server.get("overlapping.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements");
        WebElement infront = driver.findElement(By.id("infront"));
        Actions builder = new Actions(driver);
        builder.moveToElement(infront)
                .click()
                .perform();
        (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.textToBe(By.id("output"), "infront (58,58)"));
    }

    private String keyOutput(String element, int... codes) {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < codes.length; i++) {
            builder.append(element).append(" (");
            builder.append(codes[i]).append(")");
            if (i < codes.length - 1)
                builder.append("\n");
        }
        return builder.toString();
    }

    @Test
    public void webActionsSendKeys() {
        driver.get(server.get("overlapping-keys.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements - Keys");
        WebElement inbehind = driver.findElement(By.id("inbehind"));
        WebActions.click(inbehind);
        WebActions.sendKeys(inbehind, "Test");
        (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.textToBe(By.id("output"),
                        keyOutput("infront", 84, 16, 69, 83, 84)));
    }

    @Test
    public void frontSimpleSendKeys() {
        driver.get(server.get("overlapping-keys.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements - Keys");
        WebElement infront = driver.findElement(By.id("infront"));
        infront.sendKeys("Test");
        (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.textToBe(By.id("output"),
                        keyOutput("infront", 84, 16, 69, 83, 84)));
    }

    @Test
    public void frontActionSendKeys() {
        driver.get(server.get("overlapping-keys.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements - Keys");
        WebElement infront = driver.findElement(By.id("infront"));
        Actions builder = new Actions(driver);
        builder.moveToElement(infront)
                .click()
                .sendKeys("Test")
                .perform();
        (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.textToBe(By.id("output"),
                        keyOutput("infront", 84, 16, 69, 83, 84)));
    }

    @Test
    public void behindSimpleSendKeys() throws InterruptedException {
        driver.get(server.get("overlapping-keys.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements - Keys");
        WebElement inbehind = driver.findElement(By.id("inbehind"));
        inbehind.sendKeys("Test");
        (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.textToBe(By.id("output"),
                        keyOutput("inbehind", 84, 16, 69, 83, 84)));
    }

    @Test(expectedExceptions = WebDriverException.class)
    public void behindActionSendKeys() {
        driver.get(server.get("overlapping-keys.html"));
        Assert.assertEquals(driver.getTitle(), "Overlapping Elements - Keys");
        WebElement inbehind = driver.findElement(By.id("inbehind"));
        Actions builder = new Actions(driver);
        builder.moveToElement(inbehind)
                .click()
                .sendKeys("Test")
                .perform();
    }

    @Test
    public void sendKeysJSTest() throws Exception {
        driver.get(server.get("keyboard.html"));
        WebElement inp = driver.findElement(By.id("inp"));
        WebActions.sendKeysJS(inp, "aaa");
        Thread.sleep(1000);
        WebElement output = driver.findElement(By.id("output"));
        final String aaa = "Event key code keyCode charCode which ctrlKey shiftKey altKey\n" +
                "keydown a KeyA 65 0 65 false false false\n" +
                "keypress a KeyA 0 97 97 false false false\n" +
                "keyup a KeyA 65 0 65 false false false\n" +
                "keydown a KeyA 65 0 65 false false false\n" +
                "keypress a KeyA 0 97 97 false false false\n" +
                "keyup a KeyA 65 0 65 false false false\n" +
                "keydown a KeyA 65 0 65 false false false\n" +
                "keypress a KeyA 0 97 97 false false false\n" +
                "keyup a KeyA 65 0 65 false false false";
        Assert.assertEquals(output.getText(), aaa);
        Assert.assertEquals(WebElements.getProperty(inp, "value"), "aaa");
        Object selectionStart = WebElements.getProperty(inp, "selectionStart");
        Assert.assertTrue(selectionStart instanceof Long, selectionStart.getClass().getName());
        Assert.assertEquals(selectionStart, 3L);

        WebActions.sendKeysJS(inp, Keys.END);
        Thread.sleep(1000);
        Assert.assertEquals(output.getText(), aaa + "\n" +
                "keydown End End 35 0 35 false false false\n" +
                "keypress End End 35 0 0 false false false\n" +
                "keyup End End 35 0 35 false false false");
        System.out.println(output.getText());
    }
}