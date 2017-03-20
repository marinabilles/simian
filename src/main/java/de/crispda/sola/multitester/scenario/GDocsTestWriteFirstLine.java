package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Test;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

public class GDocsTestWriteFirstLine extends Test {
    @Override
    public void test() throws Exception {
        final GDocs gDocs = new GDocs(driver);
        gDocs.hideCursor();
        maybeWait();
        List<WebElement> lines = driver.findElements(
                By.className(GDocs.lineClass));
        WebElement secondLine = lines.get(1);
        WebActions.click(secondLine);
        gDocs.sendKeys(Keys.END, "ing");
        Thread.sleep(10000);
        maybeWait();
    }

    @Override
    public int getMaybeWaitCount() {
        return 2;
    }

    @Override
    public String getInitialURL() {
        return GDocs.url;
    }
}
