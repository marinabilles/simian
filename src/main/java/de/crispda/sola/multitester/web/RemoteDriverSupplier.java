package de.crispda.sola.multitester.web;

import de.crispda.sola.multitester.util.Paths;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RemoteDriverSupplier implements DriverSupplier, Quittable {
    private List<WrappedDriver> drivers = new ArrayList<>();
    private final boolean manualQuit;

    RemoteDriverSupplier() {
        manualQuit = false;
    }

    RemoteDriverSupplier(boolean manualQuit) {
        this.manualQuit = manualQuit;
    }

    @Override
    public WebDriver get(int number) throws InterruptedException {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setVersion("45");
        WebDriver driver;
        try {
            driver = new RemoteWebDriver(new URL(Paths.get("seleniumGridUrl")), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        WebDriver.Window window = driver.manage().window();
        window.setPosition(new Point(0, 0));
        window.setSize(new Dimension(960, 900));

        WrappedDriver wd = new WrappedDriver(driver, number, false);
        wd.setManualQuit(manualQuit);
        if (manualQuit)
            drivers.add(wd);
        return wd;
    }

    @Override
    public void manualQuit() {
        if (!manualQuit)
            throw new UnsupportedOperationException("Not in manual quit mode");
        drivers.forEach(WrappedDriver::manualQuit);
        drivers.clear();
    }

    @Override
    public List<WrappedDriver> getDrivers() {
        return drivers;
    }
}
