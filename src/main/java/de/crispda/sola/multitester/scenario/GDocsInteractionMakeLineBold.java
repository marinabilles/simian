package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GDocsInteractionMakeLineBold extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final int index;

    public GDocsInteractionMakeLineBold(int index) {
        this.index = index;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        List<WebElement> lines = driver.findElements(
                By.className(GDocs.lineClass));
        WebElement line = lines.get(index);
        WebActions.click(line);
        gDocs.sendKeysShift(WebActions.args(Keys.HOME));
        Thread.sleep(500);
        WebActions.click(driver.findElement(By.id("boldButton")));
        gDocs.sendKeys(Keys.END);
        Actions builder = new Actions(driver);
        builder.moveToElement(gDocs.getLastLine()).build().perform();
        Thread.sleep(500);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsInteractionMakeLineBold that = (GDocsInteractionMakeLineBold) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
