package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Test;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class GDocsTestWriteLastLine extends Test {
    @Override
    public void test() throws Exception {
        final GDocs gDocs = new GDocs(driver);
        gDocs.hideCursor();
        maybeWait();
        WebElement lastLine = gDocs.getLastLine();
        WebActions.click(lastLine);
        gDocs.sendKeys(Keys.END, "ing");
        Thread.sleep(10000);
        maybeWait();
        gDocs.sendKeys(" the application");
        Thread.sleep(10000);
        maybeWait();
    }

    @Override
    public int getMaybeWaitCount() {
        return 3;
    }

    @Override
    public String getInitialURL() {
        return GDocs.url;
    }
}
