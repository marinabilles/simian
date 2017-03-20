package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.GDocsInteraction;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.io.IOException;

public class GDocsInteractionAddCommentCancel extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.id("insertCommentButton")));
        Thread.sleep(500);
        WebActions.sendKeys(driver.findElement(By.className("docos-input-textarea")), Keys.ESCAPE);
        Thread.sleep(1500);
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
