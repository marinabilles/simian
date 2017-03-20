package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.GDocsInteraction;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;

public class GDocsInteractionBoldButtonUndo extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(gDocs.getLastLine());
        gDocs.sendKeysControl("a");
        Thread.sleep(500);

        WebActions.click(driver.findElement(By.id("boldButton")));
        Thread.sleep(500);
        WebActions.pressRelease(driver.findElement(By.id("undoButton")));
        Thread.sleep(500);
        gDocs.sendKeysControlShift("a");
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.className("kix-appview-editor"))).perform();
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
