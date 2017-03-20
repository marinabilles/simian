package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class FirepadApplyModification extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;
    private final Firepad.Modification modification;

    public FirepadApplyModification(Selection selection, Firepad.Modification modification) {
        this.selection = selection;
        this.modification = modification;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        firepad.select(selection);
        WebActions.click(driver.findElement(By.className(modification.getClassName())));
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.tagName("body"))).perform();
        firepad.deselect();
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
        FirepadApplyModification that = (FirepadApplyModification) o;
        return selection == that.selection &&
                modification == that.modification;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection, modification);
    }
}
