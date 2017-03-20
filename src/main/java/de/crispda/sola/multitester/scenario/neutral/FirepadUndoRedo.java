package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.FirepadInteraction;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class FirepadUndoRedo extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebElement textarea = driver.findElement(By.tagName("textarea"));
        WebActions.sendKeysJSControl(textarea, "z");
        Thread.sleep(300);
        WebActions.sendKeysJSControlShift(textarea, "z");
        Thread.sleep(300);
        firepad.focus();
        Thread.sleep(300);
        firepad.deselect();
        Thread.sleep(2000);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
