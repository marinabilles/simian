package de.crispda.sola.multitester.web;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.IOException;
import java.util.List;

public class WebElements {
    private static JavascriptExecutor javascriptExecutorFor(WebElement element) {
        RemoteWebElement remoteElement = (RemoteWebElement) element;
        return (JavascriptExecutor) remoteElement.getWrappedDriver();
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getChildNodes(WebElement element) throws IOException {
        return (List<Object>) javascriptExecutorFor(element).executeScript(
                Resources.toString(Resources.getResource("getChildNodes.js"), Charsets.UTF_8),
                element);
    }

    public static Object getProperty(WebElement element, String property) {
        try {
            return javascriptExecutorFor(element).executeScript(
                    Resources.toString(Resources.getResource("getProperty.js"), Charsets.UTF_8),
                    element,
                    property);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
