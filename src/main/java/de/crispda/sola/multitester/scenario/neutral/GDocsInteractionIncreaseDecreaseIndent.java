package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.GDocsInteraction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class GDocsInteractionIncreaseDecreaseIndent extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebElement moreButton = driver.findElement(By.id("moreButton"));
        if (moreButton.getAttribute("aria-pressed").equals("false")) {
            moreButton.click();
            Thread.sleep(500);
        }
        WebElement indentButton = driver.findElement(By.id("indentButton"));
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(indentButton));
        indentButton.click();
        Thread.sleep(500);
        moreButton = driver.findElement(By.id("moreButton"));
        if (moreButton.getAttribute("aria-pressed").equals("false")) {
            moreButton.click();
            Thread.sleep(1000);
        }
        driver.findElement(By.id("outdentButton")).click();
        Thread.sleep(1000);
        moreButton = driver.findElement(By.id("moreButton"));
        if (moreButton.getAttribute("aria-pressed").equals("true")) {
            moreButton.click();
            Thread.sleep(1000);
        }
        Actions builder = new Actions(driver);
        builder.moveToElement(gDocs.getLastLine()).build().perform();
        Thread.sleep(1000);
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
