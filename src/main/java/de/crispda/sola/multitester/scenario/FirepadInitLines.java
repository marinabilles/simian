package de.crispda.sola.multitester.scenario;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.TestInit;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

public class FirepadInitLines extends TestInit {
    @Override
    protected void init() throws InterruptedException {
        Interaction click = new FirepadClick();
        click.setDriver(driver);
        try {
            click.perform();

            ((JavascriptExecutor) driver).executeAsyncScript(Resources.toString(
                    Resources.getResource("clearText.js"), Charsets.UTF_8));

            Thread.sleep(3000);
            FirepadInteraction interaction = new FirepadWrite("a long text", Keys.RETURN, " and another one");
            interaction.setDriver(driver);
            interaction.perform();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
