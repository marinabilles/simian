package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Interaction;
import org.openqa.selenium.WebDriver;

public abstract class FirepadInteraction implements Interaction {
    private static final long serialVersionUID = 1L;
    protected transient WebDriver driver;
    protected transient Firepad firepad;

    @Override
    public void setDriver(WebDriver driver) {
        this.driver = driver;
        this.firepad = new Firepad(driver);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
