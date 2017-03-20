package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.util.StringConverter;

import java.io.IOException;
import java.util.Arrays;

public class GDocsWrite extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final CharSequence[] chars;

    public GDocsWrite(CharSequence... chars) {
        this.chars = chars;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        gDocs.sendKeys(chars);
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
        GDocsWrite that = (GDocsWrite) o;
        return Arrays.equals(chars, that.chars);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(chars);
    }
}
