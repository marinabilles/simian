package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Objects;

public class FirepadMakeFontSize extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;
    private final String fontSize;

    public FirepadMakeFontSize(Selection selection, String fontSize) {
        this.selection = selection;
        this.fontSize = fontSize;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        firepad.select(selection);
        WebElement font = driver.findElement(By.partialLinkText("Size"));
        font.click();
        WebElement fontSizeBtn = new WebDriverWait(driver, 10).until(
                ExpectedConditions.visibilityOfElementLocated(By.linkText(fontSize)));
        fontSizeBtn.click();
        firepad.deselect();
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
        FirepadMakeFontSize that = (FirepadMakeFontSize) o;
        return selection == that.selection &&
                Objects.equals(fontSize, that.fontSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection, fontSize);
    }
}
