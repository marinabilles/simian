package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirepadMakeFontColor extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;
    private final String color;
    private static final Pattern colorPattern = Pattern.compile("background-color: ([A-Za-z]*);$");

    public FirepadMakeFontColor(Selection selection, String color) {
        this.selection = selection;
        this.color = color;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        firepad.select(selection);
        WebElement colorBtn = driver.findElement(By.partialLinkText("Color"));
        colorBtn.click();
        WebElement colorElBtn = new WebDriverWait(driver, 10)
                .ignoring(StaleElementReferenceException.class)
                .until(
            new ExpectedCondition<WebElement>() {
                @Override
                public WebElement apply(WebDriver driver) {
                    List<WebElement> colors = colorBtn.findElements(By.className("firepad-color-dropdown-item"));
                    for (WebElement colorel : colors) {
                        String style = colorel.getAttribute("style");
                        Matcher styleMatcher = colorPattern.matcher(style);

                        if (styleMatcher.find()) {
                            String colorstr = styleMatcher.group(1);
                            if (colorstr.equals(color) && colorel.isDisplayed()) {
                                return colorel;
                            }
                        }
                    }
                    return null;
                }
        });

        colorElBtn.click();
        firepad.deselect();
    }

    @Override
    public String toString() {
        return String.format("%s{%s, %s}",
                this.getClass().getSimpleName(),
                selection,
                color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirepadMakeFontColor that = (FirepadMakeFontColor) o;
        return selection == that.selection &&
                Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection, color);
    }
}
