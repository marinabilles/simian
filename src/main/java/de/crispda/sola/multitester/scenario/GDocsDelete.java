package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.Objects;

public class GDocsDelete extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;

    public GDocsDelete(Selection selection) {
        this.selection = selection;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        gDocs.select(selection);
        gDocs.sendKeys(Keys.BACK_SPACE);
        gDocs.deselect();
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
        GDocsDelete that = (GDocsDelete) o;
        return selection == that.selection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection);
    }
}
