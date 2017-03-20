package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.FirepadInteraction;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class FirepadOpenCloseDeveloperTools extends FirepadInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        WebElement scroll = driver.findElement(By.className("CodeMirror-scroll"));
        scroll.sendKeys(Keys.F12);
        Thread.sleep(1000);
        scroll.sendKeys(Keys.F12);
        Thread.sleep(1000);
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
