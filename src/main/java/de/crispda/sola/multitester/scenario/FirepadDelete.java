package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Objects;

public class FirepadDelete extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;

    public FirepadDelete(Selection selection) {
        this.selection = selection;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        firepad.select(selection);
        WebElement textarea = driver.findElement(By.tagName("textarea"));
        WebActions.sendKeysJS(textarea, Keys.BACK_SPACE);
    }

    @Override
    public String toString() {
        return String.format("%s{%s}",
                this.getClass().getSimpleName(),
                selection.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirepadDelete that = (FirepadDelete) o;
        return selection == that.selection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection);
    }
}
