package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.TestInit;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class GDocsInitLines extends TestInit {
    @Override
    protected void init() throws InterruptedException {
        final GDocs gDocs = new GDocs(driver);
        WebElement lastLine = gDocs.getLastLine();
        WebActions.click(lastLine);
        try {
            gDocs.sendKeysControl("a");
            Thread.sleep(500);
            gDocs.sendKeys(Keys.BACK_SPACE);
            Thread.sleep(500);
            lastLine = gDocs.getLastLine();
            WebActions.click(lastLine);
            gDocs.unbold();
            gDocs.sendKeys("Test", Keys.RETURN, Keys.RETURN, Keys.RETURN, "Test");
            Thread.sleep(5000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
