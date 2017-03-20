package de.crispda.sola.multitester.web;

import de.crispda.sola.multitester.scenario.Firepad;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RemoteDriverTest {
    @Test
    public void remoteDriver() throws Exception {
        WebDriver driver = Drivers.remoteDriver().get(1);
        try {
            driver.get("http://example.com");
            WebElement h1 = driver.findElement(By.tagName("h1"));
            Assert.assertEquals(h1.getText(), "Example Domain");
        } finally {
            driver.quit();
        }
    }

    @Test
    public void drivers() throws Exception {
        DriverSupplier supplier = Drivers.remoteDriver();
        WebDriver driver1 = supplier.get(1);
        WebDriver driver2 = supplier.get(2);
        driver1.get(Firepad.url);
        driver2.get(Firepad.url);
        Thread.sleep(50000);
        driver1.quit();
        driver2.quit();
    }

    @Test
    public void driversFocus() throws Exception {
        DriverSupplier supplier = Drivers.remoteDriver();
        WebDriver driver1 = supplier.get(1);
        WebDriver driver2 = supplier.get(2);
        driver1.get(Firepad.url);
        driver2.get(Firepad.url);

        Firepad firepad1 = new Firepad(driver1);
        Firepad firepad2 = new Firepad(driver2);
        firepad1.hideCursor();
        firepad2.hideCursor();
        Thread.sleep(5000);
        firepad1.focus();
        Thread.sleep(5000);
        firepad2.focus();
        Thread.sleep(5000);
        firepad1.focus();
        Thread.sleep(5000);

        driver1.quit();
        driver2.quit();
    }
}
