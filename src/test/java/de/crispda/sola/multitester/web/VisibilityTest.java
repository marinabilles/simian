package de.crispda.sola.multitester.web;

import de.crispda.sola.multitester.TestWebServer;
import de.crispda.sola.multitester.util.WebServer;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VisibilityTest {
    private WebServer server;
    private WebDriver driver;

    @BeforeMethod
    public void setUp() throws Exception {
        server = new TestWebServer();
        driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        driver.quit();
        server.stop();
    }

    @Test
    public void visibilityTest() throws Exception {
        driver.get(server.get("visibility.html"));
        try {
            driver.findElement(By.id("menu")).click();
            driver.findElement(By.id("clickhere")).click();
        } catch (ElementNotVisibleException e) {
            System.out.println("Caught exception!");
            driver.findElement(By.id("clickhere")).click();
            driver.findElement(By.id("menu")).click();
        }
        Thread.sleep(5000);
    }
}
