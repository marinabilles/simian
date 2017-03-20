package de.crispda.sola.multitester.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class PropertyCondition implements ExpectedCondition<Boolean> {
    private final WebElement element;
    private final String property;
    private final String expected;

    public PropertyCondition(final WebElement element, final String property,
                             final String expected) {
        this.element = element;
        this.property = property;
        this.expected = expected;
    }

    @Override
    public Boolean apply(WebDriver webDriver) {
        return WebElements.getProperty(element, property).equals(expected);
    }
}
