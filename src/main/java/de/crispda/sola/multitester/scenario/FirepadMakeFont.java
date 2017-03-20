package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Objects;

public class FirepadMakeFont extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    private final Firepad.Font font;
    private final Selection selection;

    public FirepadMakeFont(Selection selection, Firepad.Font font) {
        this.selection = selection;
        this.font = font;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        firepad.select(selection);
        WebElement fontLink = driver.findElement(By.partialLinkText("Font"));
        fontLink.click();
        WebElement fontFace = new WebDriverWait(driver, 10).until(
                ExpectedConditions.visibilityOfElementLocated(By.linkText(font.getFontName())));
        fontFace.click();
        firepad.deselect();
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
        FirepadMakeFont that = (FirepadMakeFont) o;
        return font == that.font &&
                selection == that.selection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, selection);
    }
}
