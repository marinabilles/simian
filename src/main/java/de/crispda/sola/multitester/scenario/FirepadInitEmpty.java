package de.crispda.sola.multitester.scenario;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.TestInit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FirepadInitEmpty extends TestInit {
    @Override
    protected void init() throws InterruptedException {
        new WebDriverWait(driver, 30)
                .until(ExpectedConditions.presenceOfElementLocated(By.className("CodeMirror-scroll")));
        Interaction click = new FirepadClick();
        click.setDriver(driver);
        try {
            click.perform();

            ((JavascriptExecutor) driver).executeAsyncScript(Resources.toString(
                    Resources.getResource("clearText.js"), Charsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
