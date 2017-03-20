package de.crispda.sola.multitester.web;

import org.openqa.selenium.WebDriver;

public interface DriverSupplier {
    WebDriver get(final int number) throws InterruptedException;
}
