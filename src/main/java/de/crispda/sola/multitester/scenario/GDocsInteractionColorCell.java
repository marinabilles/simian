package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class GDocsInteractionColorCell extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.className(GDocs.cellClass)));
        WebActions.click(driver.findElement(By.className(GDocs.cellClass)));

        WebElement moreButton = driver.findElement(By.id("moreButton"));
        if (moreButton.getAttribute("aria-pressed").equals("false")) {
            WebActions.click(moreButton);
            Thread.sleep(500);
        }

        WebElement cellColorButton = driver.findElement(By.id("cellColorMenuButton"));
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(cellColorButton));
        cellColorButton.click();
        // Thread.sleep(500);
        Thread.sleep(5000);
        // driver.findElement(By.id("jfk-palette-cell-294")).click();
        WebElement orange = driver.findElement(By.cssSelector("[aria-label=\"orange \"]"));
        WebActions.click(orange);
        // Thread.sleep(1000);
        Thread.sleep(5000);
        WebActions.click(driver.findElement(By.id("moreButton")));
        Thread.sleep(500);
        Actions builder = new Actions(driver);
        builder.moveToElement(gDocs.getLastLine()).build().perform();
        WebActions.click(driver.findElement(By.className(GDocs.cellClass)));
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
