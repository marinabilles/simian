package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.util.StringConverter;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Arrays;

public class FirepadWrite extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    private final CharSequence[] chars;

    public FirepadWrite(CharSequence... chars) {
        this.chars = chars;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        firepad.focus();
        WebElement textarea = driver.findElement(By.tagName("textarea"));
        WebActions.sendKeys(textarea, chars);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                StringConverter.convert(chars) +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirepadWrite that = (FirepadWrite) o;
        return Arrays.equals(chars, that.chars);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(chars);
    }
}
