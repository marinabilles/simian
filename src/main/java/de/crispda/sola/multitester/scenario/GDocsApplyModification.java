package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class GDocsApplyModification extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    private final GDocs.Modification modification;
    private final Selection selection;

    public GDocsApplyModification(Selection selection, GDocs.Modification modification) {
        this.modification = modification;
        this.selection = selection;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        gDocs.select(selection);
        WebActions.click(driver.findElement(By.id(modification.get())));
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.className("kix-appview-editor"))).perform();
        gDocs.deselect();
    }

    @Override
    public String toString() {
        return String.format("%s{%s, %s}",
                this.getClass().getSimpleName(),
                selection,
                modification);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GDocsApplyModification that = (GDocsApplyModification) o;
        return modification == that.modification &&
                selection == that.selection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modification, selection);
    }
}
