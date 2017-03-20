package de.crispda.sola.multitester.scenario;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class OwncloudClickButton extends OwncloudInteraction {
    private static final long serialVersionUID = 1L;
    private final Owncloud.Button button;

    public OwncloudClickButton(Owncloud.Button button) {
        this.button = button;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        owncloud.clickButton(button.getButtonText());
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.id("canvas"))).perform();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + button.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwncloudClickButton that = (OwncloudClickButton) o;
        return button == that.button;
    }

    @Override
    public int hashCode() {
        return Objects.hash(button);
    }
}
