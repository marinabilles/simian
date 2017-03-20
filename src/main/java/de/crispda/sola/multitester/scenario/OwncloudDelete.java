package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.Objects;

public class OwncloudDelete extends OwncloudInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;

    public OwncloudDelete(Selection selection) {
        this.selection = selection;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        owncloud.select(selection);
        WebActions.sendKeys(driver.findElement(By.id("canvas")), Keys.BACK_SPACE);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + selection.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwncloudDelete that = (OwncloudDelete) o;
        return selection == that.selection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection);
    }
}
