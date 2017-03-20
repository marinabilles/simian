package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class FirepadInsertImage extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebActions.click(driver.findElement(By.className("firepad-tb-insert-image")));
        WebElement imgText = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(
                By.id("img")));
        imgText.sendKeys("image.png");
        Thread.sleep(200);
        driver.findElement(By.id("submitbtn")).click();
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
