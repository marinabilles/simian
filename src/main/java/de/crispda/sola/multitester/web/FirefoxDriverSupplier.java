package de.crispda.sola.multitester.web;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.ArrayList;
import java.util.List;

public class FirefoxDriverSupplier implements DriverSupplier, Quittable {
    private final boolean manualQuit;
    private List<WrappedDriver> drivers = new ArrayList<>();

    FirefoxDriverSupplier() {
        this.manualQuit = false;
    }

    FirefoxDriverSupplier(boolean manualQuit) {
        this.manualQuit = manualQuit;
    }

    @Override
    public WebDriver get(int number) throws InterruptedException {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("intl.accept_languages", "en");
        WebDriver driver = new FirefoxDriver(profile);
        WebDriver.Window window = driver.manage().window();
        window.setPosition(new Point((number - 1) * 1200, 30));
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
