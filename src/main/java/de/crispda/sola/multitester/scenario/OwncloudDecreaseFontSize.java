package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class OwncloudDecreaseFontSize extends OwncloudInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;

    public OwncloudDecreaseFontSize(Selection selection) {
        this.selection = selection;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        owncloud.select(selection);
        WebElement toolbar = driver.findElement(By.id("toolbar"));
        WebElement upArrowButton = toolbar.findElement(
                By.xpath(".//input[@name='FontPicker']/ancestor::table[@role='listbox']" +
                        "/following-sibling::div[1]" +
                        "/descendant::div[contains(concat(' ', @class, ' '), ' dijitDownArrowButton ')]"));
        upArrowButton.click();
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.id("canvas"))).perform();

        owncloud.deselect();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + selection.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwncloudDecreaseFontSize that = (OwncloudDecreaseFontSize) o;
        return selection == that.selection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection);
    }
}
