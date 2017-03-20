package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GDocsInteractionWriteLine extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final int index;
    private final CharSequence[] content;

    public GDocsInteractionWriteLine(int index, CharSequence... content) {
        this.index = index;
        this.content = content;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        List<WebElement> lines = driver.findElements(
                By.className(GDocs.lineClass));
        WebElement line = lines.get(index);
        WebActions.click(line);
        gDocs.sendKeys(WebActions.args(Keys.END, content));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsInteractionWriteLine that = (GDocsInteractionWriteLine) o;
        return index == that.index &&
                Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, content);
    }
}
