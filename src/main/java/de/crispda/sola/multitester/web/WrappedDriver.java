package de.crispda.sola.multitester.web;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;
import java.util.Set;

public class WrappedDriver implements WebDriver, JavascriptExecutor,
        FindsById, FindsByClassName, FindsByLinkText, FindsByName,
        FindsByCssSelector, FindsByTagName, FindsByXPath,
        HasInputDevices, HasCapabilities, TakesScreenshot {
    private final RemoteWebDriver driver;
    private final int number;
    private final boolean remote;
    private boolean manualQuit = false;

    public WrappedDriver(WebDriver driver, int number, boolean remote) {
        while (driver instanceof WrappedDriver)
            driver = ((WrappedDriver) driver).driver;
        Drivers.setup(driver);
        this.driver = (RemoteWebDriver) driver;
        this.number = number;
        this.remote = remote;
    }

    public int getNumber() {
        return number;
    }

    public boolean isRemote() {
        return remote;
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return driver.executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return driver.executeAsyncScript(script, args);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return driver.getScreenshotAs(target);
    }

    @Override
    public void get(String url) {
        driver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        if (!manualQuit)
            driver.quit();
    }

    public void manualQuit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }

    @Override
    public Capabilities getCapabilities() {
        return driver.getCapabilities();
    }

    @Override
    public Keyboard getKeyboard() {
        return driver.getKeyboard();
    }

    @Override
    public Mouse getMouse() {
        return driver.getMouse();
    }

    @Override
    public WebElement findElementByClassName(String using) {
        return driver.findElementByClassName(using);
    }

    @Override
    public List<WebElement> findElementsByClassName(String using) {
        return driver.findElementsByClassName(using);
    }

    @Override
    public WebElement findElementByCssSelector(String using) {
        return driver.findElementByCssSelector(using);
    }

    @Override
    public List<WebElement> findElementsByCssSelector(String using) {
        return driver.findElementsByCssSelector(using);
    }

    @Override
    public WebElement findElementById(String using) {
        return driver.findElementById(using);
    }

    @Override
    public List<WebElement> findElementsById(String using) {
        return driver.findElementsById(using);
    }

    @Override
    public WebElement findElementByLinkText(String using) {
        return driver.findElementByLinkText(using);
    }

    @Override
    public List<WebElement> findElementsByLinkText(String using) {
        return driver.findElementsByLinkText(using);
    }

    @Override
    public WebElement findElementByPartialLinkText(String using) {
        return driver.findElementByPartialLinkText(using);
    }

    @Override
    public List<WebElement> findElementsByPartialLinkText(String using) {
        return driver.findElementsByPartialLinkText(using);
    }

    @Override
    public WebElement findElementByName(String using) {
        return driver.findElementByName(using);
    }

    @Override
    public List<WebElement> findElementsByName(String using) {
        return driver.findElementsByName(using);
    }

    @Override
    public WebElement findElementByTagName(String using) {
        return driver.findElementByTagName(using);
    }

    @Override
    public List<WebElement> findElementsByTagName(String using) {
        return driver.findElementsByTagName(using);
    }

    @Override
    public WebElement findElementByXPath(String using) {
        return driver.findElementByXPath(using);
    }

    @Override
    public List<WebElement> findElementsByXPath(String using) {
        return driver.findElementsByXPath(using);
    }

    public void setManualQuit(boolean manualQuit) {
        this.manualQuit = manualQuit;
    }
}
