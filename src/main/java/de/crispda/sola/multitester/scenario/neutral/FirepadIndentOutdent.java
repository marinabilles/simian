package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.FirepadInteraction;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;

public class FirepadIndentOutdent extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.className("firepad-tb-indent-increase")));
        Thread.sleep(300);
        WebActions.click(driver.findElement(By.className("firepad-tb-indent-decrease")));
        Thread.sleep(300);
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.tagName("body"))).perform();
        firepad.focus();
        Thread.sleep(500);
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
