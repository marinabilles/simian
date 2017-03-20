package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Exchanges;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.concurrent.Exchanger;

public class OwncloudCursorSender extends OwncloudInteraction implements Exchanges {
    private transient Exchanger<Point> exchanger;
    private static final long serialVersionUID = 1L;

    @Override
    public void setExchanger(Exchanger<Point> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        Point p = new WebDriverWait(driver, 10)
                .ignoring(StaleElementReferenceException.class)
                .until(new ExpectedCondition<Point>() {
            @Override
            public Point apply(WebDriver driver) {
                Point p = ((WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].parentNode;",
                        driver.findElement(By.id("eventTrap")))).findElement(By.className("caret")).getLocation();
                System.out.println("Sending point: " + p);
                return p;
            }
        });

        exchanger.exchange(p);
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
