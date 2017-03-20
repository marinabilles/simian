package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;

import java.io.IOException;

public class GDocsInteractionTableAddRow extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.className(GDocs.cellClass)));
        Thread.sleep(100);
        WebActions.click(driver.findElement(By.id("docs-table-menu")));
        Thread.sleep(100);
        WebActions.click(driver.findElement(By.cssSelector("[aria-label=\"Insert row below, b;\"]")));
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
