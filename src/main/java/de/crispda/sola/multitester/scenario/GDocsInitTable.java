package de.crispda.sola.multitester.scenario;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.TestInit;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

public class GDocsInitTable extends TestInit {
    @Override
    protected void init() throws InterruptedException {
        final GDocs gDocs = new GDocs(driver);
        WebElement lastLine = gDocs.getLastLine();
        WebActions.click(lastLine);
        try {
            gDocs.sendKeysControl("a");
            Thread.sleep(500);
            gDocs.sendKeys(Keys.BACK_SPACE);
            Thread.sleep(500);
            lastLine = gDocs.getLastLine();
            WebActions.click(lastLine);
            gDocs.unbold();

            List<GDocsInteraction> actions = Lists.newArrayList(
                    new GDocsInteractionInsertTable(),
                    new GDocsInteractionTableAddColumn(),
                    new GDocsInteractionTableAddColumn(),
                    new GDocsInteractionTableAddColumn(),
                    new GDocsInteractionTableAddRow(),
                    new GDocsInteractionTableAddRow()
            );
            for (GDocsInteraction action : actions) {
                action.setDriver(driver);
                action.perform();
            }
            Thread.sleep(500);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
