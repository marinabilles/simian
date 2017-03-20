package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.Keys;

import java.io.IOException;

public class GDocsInteractionDeleteAll extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(gDocs.getLastLine());
        gDocs.sendKeysControl("a");
        Thread.sleep(500);
        gDocs.sendKeys(Keys.BACK_SPACE);
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
