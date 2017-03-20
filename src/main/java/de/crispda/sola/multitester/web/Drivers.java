package de.crispda.sola.multitester.web;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class Drivers {
    public static DriverSupplier firefoxDriver(final String firefoxVersion) {
        System.setProperty("webdriver.firefox.bin", firefoxVersion);
        return new FirefoxDriverSupplier();
    }

    public static FirefoxDriverSupplier firefoxDriver(String firefoxVersion, boolean manualQuit) {
        System.setProperty("webdriver.firefox.bin", firefoxVersion);
        return new FirefoxDriverSupplier(manualQuit);
    }

    public static RemoteDriverSupplier remoteDriver() {
        return new RemoteDriverSupplier();
    }

    public static RemoteDriverSupplier remoteDriver(boolean manualQuit) {
        return new RemoteDriverSupplier(manualQuit);
    }

    public static void setup(WebDriver driver) {
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
    }
}
