package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class GDocsInteractionInsertTable extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.id("docs-table-menu")));
        Thread.sleep(500);
        Actions builder = new Actions(driver);
        // "#\\:97 .goog-menuitem-content"
        builder.moveToElement(driver.findElement(By.cssSelector("[aria-label=\"Insert table, i;\"]"))).perform();

        Thread.sleep(500);
        //By condition = By.xpath("//div[@id = ':pv']/div[1]");
        By condition = By.cssSelector(".goog-dimension-picker-highlighted");
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(condition));
        WebActions.click(driver.findElement(condition));
        builder.moveToElement(gDocs.getLastLine()).perform();
        Thread.sleep(200);
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
