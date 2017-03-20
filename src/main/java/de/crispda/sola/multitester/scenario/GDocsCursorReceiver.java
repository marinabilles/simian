package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Exchanges;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;

import java.io.IOException;
import java.util.concurrent.Exchanger;

public class GDocsCursorReceiver extends GDocsInteraction implements Exchanges {
    private transient Exchanger<Point> exchanger;
    private static final long serialVersionUID = 1L;

    @Override
    public void setExchanger(Exchanger<Point> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        Point p = exchanger.exchange(null);
        WebActions.moveToTopLeft(driver.findElement(By.className("kix-appview-editor")))
                .moveByOffset(p.x, p.y)
                .click()
                .perform();
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
