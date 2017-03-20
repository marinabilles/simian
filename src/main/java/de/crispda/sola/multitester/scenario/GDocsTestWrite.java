package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Test;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.Optional;

public class GDocsTestWrite extends Test {
    @Override
    public void test() throws Exception {
        GDocs gdocs = new GDocs(driver);
        maybeWait();

        WebElement line = gdocs.getLastLine();
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(line));
        WebActions.click(line);
        Thread.sleep(1000);
        maybeWait();

        line = gdocs.getLastLine();
        WebActions.sendKeys(line, "Test");
        line = gdocs.getLastLine();
        Optional<String> lineText = gdocs.getLineText(line);
        assert lineText.isPresent();
        Assert.assertTrue(lineText.get().contains("Test"));
        Thread.sleep(5000);
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
