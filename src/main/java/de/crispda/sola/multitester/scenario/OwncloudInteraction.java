package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Interaction;
import org.openqa.selenium.WebDriver;

public abstract class OwncloudInteraction implements Interaction {
    private static final long serialVersionUID = 1L;
    protected transient WebDriver driver;
    protected transient Owncloud owncloud;

    @Override
    public void setDriver(WebDriver driver) {
        this.driver = driver;
        this.owncloud = new Owncloud(driver);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
