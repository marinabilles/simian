package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class GDocsMakeFontSize extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;
    private final String fontSize;

    public GDocsMakeFontSize(Selection selection, String fontSize) {
        this.selection = selection;
        this.fontSize = fontSize;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        gDocs.select(selection);
        driver.findElement(By.id("fontSizeSelect")).click();
        WebElement fontSizeInput = driver.findElement(By.xpath(
                "//*[@id='fontSizeSelect']/div[1]/div[1]/div[1]/input[1]"));
        fontSizeInput.sendKeys(fontSize, Keys.RETURN);
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
                fontSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsMakeFontSize that = (GDocsMakeFontSize) o;
        return selection == that.selection &&
                Objects.equals(fontSize, that.fontSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection, fontSize);
    }
}
