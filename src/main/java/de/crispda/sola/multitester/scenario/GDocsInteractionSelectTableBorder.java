package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class GDocsInteractionSelectTableBorder extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.className("docs-border-selection-button-normal")));
        Thread.sleep(500);
        WebActions.click(driver.findElement(
                By.cssSelector("[aria-label=\"Row 2. Column 3. Select bottom border\"]")));
        Thread.sleep(500);
        driver.findElement(By.id("borderWidthMenuButton")).click();
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.attributeToBe(
                By.id("borderWidthMenuButton"), "aria-expanded", "true"
        ));
        WebElement higherWidth = (new WebDriverWait(driver, 5)).until(
                ExpectedConditions.presenceOfElementLocated(By.id("#\\:dp .goog-menuitem-content")));
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(higherWidth));
        higherWidth.click();
        Thread.sleep(500);
        Actions builder = new Actions(driver);
        builder.moveToElement(gDocs.getLastLine())
                .perform();
        Thread.sleep(500);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
