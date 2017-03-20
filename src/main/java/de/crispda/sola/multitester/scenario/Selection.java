package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public enum Selection {
    WordBefore,
    LineBefore,
    WordAfter,
    LineAfter;

    public void select(WebElement element) throws IOException, InterruptedException {
        switch (this) {
            case WordBefore:
                WebActions.sendKeysJSControlShift(element, Keys.LEFT);
                break;
            case WordAfter:
                WebActions.sendKeysJSControlShift(element, Keys.RIGHT);
                break;
            case LineBefore:
                WebActions.sendKeysJSShift(element, Keys.HOME);
                break;
            case LineAfter:
                WebActions.sendKeysJSShift(element, Keys.END);
                break;
        }
    }
}
