package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Objects;

public class GDocsMakeFont extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;
    private final GDocs.Font font;

    public GDocsMakeFont(Selection selection, GDocs.Font font) {
        this.selection = selection;
        this.font = font;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        gDocs.select(selection);
        driver.findElement(By.id("docs-font-family")).click();
        WebElement fontElement = driver.findElement(By.xpath(
                "//*[contains(concat(' ', @class, ' '), ' goog-menuitem-content ') and span='" +
                        font.getFontName() + "']"));
        Thread.sleep(3000);
        new WebDriverWait(driver, 10)
                .until(ExpectedConditions.visibilityOf(fontElement));

	try {
        new WebDriverWait(driver, 15)
                .ignoring(StaleElementReferenceException.class)
                .until(new ExpectedCondition<Boolean>(){
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        WebElement fontElement = driver.findElement(By.xpath(
                                "//*[contains(concat(' ', @class, ' '), ' goog-menuitem-content ')" +
                                " and span='" + font.getFontName() + "']"));
                        Actions builder = new Actions(driver);
                        builder.moveToElement(fontElement)
                                .sendKeys(Keys.RETURN)
                                .perform();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {
                        }
                        WebElement fontMenu = driver.findElement(By.className("docs-fontmenu"));
                        return !fontMenu.isDisplayed();
                    }
                }
        );
	} catch (TimeoutException e) {
	    boolean visible;
	    WebElement fontMenu = driver.findElement(By.className("docs-fontmenu"));
	    visible = fontMenu.isDisplayed();
	    while (visible) {
            new WebDriverWait(driver, 30)
                .ignoring(StaleElementReferenceException.class)
                .until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    WebElement fontElement = driver.findElement(By.xpath(
                                          "//*[contains(concat(' ', @class, ' '), ' goog-menuitem-content ')" +
                                          " and span='" + font.getFontName() + "']"));
                    Actions builder = new Actions(driver);
                    builder.moveToElement(fontElement)
                    .click()
                    .perform();
                    return true;
                }
                });
            Thread.sleep(500);
            fontMenu = driver.findElement(By.className("docs-fontmenu"));
            visible = fontMenu.isDisplayed();
	    }
	}

        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.className("kix-appview-editor")))
                .perform();
        gDocs.deselect();
    }

    @Override
    public String toString() {
        return String.format("%s{%s, %s}",
                this.getClass().getSimpleName(),
                selection,
                font);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsMakeFont that = (GDocsMakeFont) o;
        return selection == that.selection &&
                font == that.font;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection, font);
    }
}
