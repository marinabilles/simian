package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GDocsInteractionWriteFirstLine extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final CharSequence[] content;

    public GDocsInteractionWriteFirstLine(CharSequence... content) {
        this.content = content;
    }

    public void perform() throws IOException, InterruptedException {
        List<WebElement> lines = driver.findElements(
                By.className(GDocs.lineClass));
        WebElement secondLine = lines.get(1);
        WebActions.click(secondLine);
        gDocs.sendKeys(WebActions.args(Keys.END, content));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsInteractionWriteFirstLine that = (GDocsInteractionWriteFirstLine) o;
        return Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(content);
    }
}
