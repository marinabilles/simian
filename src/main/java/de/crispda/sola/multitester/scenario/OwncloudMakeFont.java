package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Objects;

public class OwncloudMakeFont extends OwncloudInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;
    private final Owncloud.Font font;

    public OwncloudMakeFont(Selection selection, Owncloud.Font font) {
        this.selection = selection;
        this.font = font;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        owncloud.select(selection);

        WebElement toolbar = driver.findElement(By.id("toolbar"));
        WebElement comboBox = toolbar.findElement(
                By.xpath(".//input[@name='FontPicker']/ancestor::table[@role='listbox']"));

        Actions builder = new Actions(driver);
        builder.clickAndHold(comboBox).perform();
        try {
            String widgetid = comboBox.getAttribute("widgetid");

            WebElement menuItem = new WebDriverWait(driver, 10).until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[@dijitpopupparent='" + widgetid + "']" +
                                    "/descendant::td[contains(concat(' ', @class, ' '), ' dijitMenuItemLabel ')" +
                                    " and span='" + font.getFontName() + "']/parent::*"
                            )
                    ));

            builder.moveToElement(menuItem).release().perform();
        } catch (Exception e) {
            builder.release().perform();
            throw e;
        }
        owncloud.deselect();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + selection.toString() + ", " + font.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwncloudMakeFont that = (OwncloudMakeFont) o;
        return selection == that.selection &&
                font == that.font;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection, font);
    }
}
