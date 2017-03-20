package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.TestInit;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class GDocsInitEmpty extends TestInit {
    @Override
    protected void init() throws InterruptedException {
        System.out.println("Running init...");
        List<WebElement> lines = driver.findElements(By.className(GDocs.lineClass));
        WebElement line = lines.get(lines.size() - 1);
        WebActions.click(line);
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .perform();
        WebActions.sendKeys(line, "\b");
        Thread.sleep(2000);
        System.out.println("Done running init.");
    }
}
