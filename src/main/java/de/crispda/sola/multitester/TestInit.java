package de.crispda.sola.multitester;

import org.openqa.selenium.WebDriver;

import java.io.Serializable;

public abstract class TestInit implements Serializable {
    protected transient WebDriver driver;

    public void run(WebDriver driver) {
        this.driver = driver;
        try {
            init();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void init() throws InterruptedException;
}
