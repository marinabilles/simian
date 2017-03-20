package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.Objects;

public class FirepadClickButton extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    private final Firepad.Button button;

    public FirepadClickButton(Firepad.Button button) {
        this.button = button;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.className(button.getClassName())));
        Thread.sleep(300);
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.tagName("body"))).perform();
        firepad.focus();
        Thread.sleep(500);
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
        FirepadClickButton that = (FirepadClickButton) o;
        return button == that.button;
    }

    @Override
    public int hashCode() {
        return Objects.hash(button);
    }
}
