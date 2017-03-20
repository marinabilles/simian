package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class OwncloudMakeFontSize18 extends OwncloudInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;

    public OwncloudMakeFontSize18(Selection selection) {
        this.selection = selection;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        owncloud.select(selection);
        WebElement toolbar = driver.findElement(By.id("toolbar"));
        WebElement numberSpinner = toolbar.findElement(
                By.xpath(".//input[@name='FontPicker']/ancestor::table[@role='listbox']" +
                        "/following-sibling::div[1]"));
        WebElement spinbutton = numberSpinner.findElement(By.xpath(".//input[@role='spinbutton']"));
        String valueNow = spinbutton.getAttribute("aria-valuenow");
        Actions builder = new Actions(driver);
        if (valueNow.equals("14")) {
            WebElement upArrow = numberSpinner.findElement(By.xpath(
                    ".//div[contains(concat(' ', @class, ' '), ' dijitUpArrowButton ')]"));
            builder.moveToElement(upArrow)
                    .click()
                    .click()
                    .click()
                    .click()
                    .perform();
        }
        builder.moveToElement(driver.findElement(By.id("canvas"))).perform();
        owncloud.deselect();
    }

    @Override
    public String toString() {
        return String.format("%s{%s}",
                this.getClass().getSimpleName(),
                selection);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwncloudMakeFontSize18 that = (OwncloudMakeFontSize18) o;
        return selection == that.selection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection);
    }
}
