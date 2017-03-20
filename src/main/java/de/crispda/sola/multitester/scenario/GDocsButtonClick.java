package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class GDocsButtonClick extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final GDocs.Button button;

    public GDocsButtonClick(GDocs.Button button) {
        this.button = button;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.pressRelease(driver.findElement(By.id(button.get())));
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.className("kix-appview-editor"))).perform();
    }

    @Override
    public String toString() {
        return String.format("%s{%s}",
                this.getClass().getSimpleName(),
                button);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsButtonClick that = (GDocsButtonClick) o;
        return button == that.button;
    }

    @Override
    public int hashCode() {
        return Objects.hash(button);
    }
}
