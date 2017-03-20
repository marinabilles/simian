package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;

public class GDocsInteractionIncreaseIndent extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.id("moreButton")));
        Thread.sleep(500);
        WebActions.click(driver.findElement(By.id("indentButton")));
        Thread.sleep(1000);
        WebActions.click(driver.findElement(By.id("moreButton")));
        Thread.sleep(500);
        Actions builder = new Actions(driver);
        builder.moveToElement(gDocs.getLastLine()).build().perform();
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
