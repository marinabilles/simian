package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Interaction;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

public abstract class GDocsInteraction implements Interaction {
    private static final long serialVersionUID = 1L;
    protected transient WebDriver driver;
    protected transient GDocs gDocs;

    public void setDriver(WebDriver driver) {
        gDocs = new GDocs(driver);
        this.driver = driver;
    }

    public abstract void perform() throws IOException, InterruptedException;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
