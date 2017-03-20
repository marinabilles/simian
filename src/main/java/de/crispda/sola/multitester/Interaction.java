package de.crispda.sola.multitester;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.Serializable;

public interface Interaction extends Serializable {
    void setDriver(WebDriver driver);
    void perform() throws IOException, InterruptedException;
}
