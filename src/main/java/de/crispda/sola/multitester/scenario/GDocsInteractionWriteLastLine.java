package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Arrays;

public class GDocsInteractionWriteLastLine extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final CharSequence[] content;

    public GDocsInteractionWriteLastLine(CharSequence... content) {
        this.content = content;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        WebElement lastLine = gDocs.getLastLine();
        WebActions.click(lastLine);
        gDocs.sendKeys(WebActions.args(Keys.END, content));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsInteractionWriteLastLine that = (GDocsInteractionWriteLastLine) o;
        return Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(content);
    }
}
