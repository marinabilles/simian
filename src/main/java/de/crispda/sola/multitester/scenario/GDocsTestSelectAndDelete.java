package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Test;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import java.util.Optional;

public class GDocsTestSelectAndDelete extends Test {
    @Override
    public void test() throws Exception {
        GDocs gdocs = new GDocs(driver);
        maybeWait();

        WebElement line = gdocs.getLastLine();
        WebActions.click(line);
        maybeWait();

        Actions builder = new Actions(driver);
        builder.keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .perform();
        Assert.assertTrue(gdocs.hasSelection());
        maybeWait();

        line = gdocs.getLastLine();
        WebActions.sendKeys(line, "\b");
        line = gdocs.getLastLine();
        Optional<String> lineText = gdocs.getLineText(line);
        Assert.assertTrue(!lineText.isPresent() || lineText.get().trim().equals(""));
        Thread.sleep(2000);
        maybeWait();
    }

    @Override
    public int getMaybeWaitCount() {
        return 4;
    }

    @Override
    public String getInitialURL() {
        return GDocs.url;
    }
}
