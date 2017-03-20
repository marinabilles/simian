package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.util.StringConverter;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Arrays;

public class OwncloudWrite extends OwncloudInteraction {
    private static final long serialVersionUID = 1L;
    private final CharSequence[] text;

    public OwncloudWrite(CharSequence... text) {
        this.text = text;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        WebElement canvas = driver.findElement(By.id("canvas"));
        WebActions.sendKeys(canvas, text);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                StringConverter.convert(text) +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwncloudWrite that = (OwncloudWrite) o;
        return Arrays.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(text);
    }
}
