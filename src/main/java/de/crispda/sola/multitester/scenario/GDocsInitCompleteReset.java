package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.TestInit;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class GDocsInitCompleteReset extends TestInit {
    @Override
    protected void init() throws InterruptedException {
        if (!new WebDriverWait(driver, 10)
                .ignoring(StaleElementReferenceException.class)
                .until((ExpectedCondition<Optional<WebElement>>) GDocs::isLineEmpty).isPresent()) {
            (new WebDriverWait(driver, 10))
                    .ignoring(StaleElementReferenceException.class)
                    .ignoring(UnhandledAlertException.class)
                    .until(new ExpectedCondition<Boolean>() {
                        private int count = 0;

                        @Override
                        public Boolean apply(WebDriver driver) {
                            System.out.println("Execution " + count);
                            count++;
                            List<WebElement> lines = driver.findElements(By.className(GDocs.lineClass));
                            if (lines.isEmpty())
                                return false;
                            WebElement line = lines.get(lines.size() - 1);
                            WebActions.click(line);

                            Actions actions = new Actions(driver);
                            actions.keyDown(Keys.CONTROL)
                                    .sendKeys("a")
                                    .keyUp(Keys.CONTROL)
                                    .perform();

                            GDocs gDocs = new GDocs(driver);
                            try {
                                gDocs.sendKeys(Keys.BACK_SPACE);
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            }
                            return true;
                        }
                    });
        }

        (new WebDriverWait(driver, 10))
                .ignoring(StaleElementReferenceException.class)
                .until(new GDocs.IsEmpty());

        GDocs gDocs = new GDocs(driver);
        boolean buttonsReset = false;
        Instant before = Instant.now();
        while (!buttonsReset) {
            try {
                for (GDocs.Modification m : GDocs.Modification.values()) {
                    WebElement button = driver.findElement(By.id(m.get()));
                    if (Optional.ofNullable(button.getAttribute("aria-pressed")).map(
                            at -> at.equals("true")).orElse(false)) {
                        WebActions.click(button);
                    }
                }
                buttonsReset = GDocs.isButtonsReset(driver);
            } catch (Exception ignored) {
            }

            if (!buttonsReset && Duration.between(before, Instant.now())
                    .compareTo(Duration.ofSeconds(10)) > 0) {
                throw new TimeoutException();
            }
        }

        waitPerform(driver, new GDocsMakeFontSize(Selection.LineAfter, "11"));
        waitPerform(driver, new GDocsMakeFont(Selection.LineAfter, GDocs.Font.arial));

        Actions builder = new Actions(driver);
        WebElement lastLine = gDocs.getLastLine();
        builder.moveToElement(lastLine).build().perform();

        Optional<Integer> marginLeft = (new WebDriverWait(driver, 10)
                .ignoring(StaleElementReferenceException.class)
                .until(new GDocs.LastLineLeftMarginCondition()));
        if (marginLeft.isPresent()) {
            int times = new Double(Math.ceil(((double) marginLeft.get()) / 48.0f)).intValue();
            for (int i = 0; i < times; i++) {
                try {
                    gDocs.sendKeys(Keys.BACK_SPACE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void waitPerform(WebDriver driver, Interaction interaction) {
        new WebDriverWait(driver, 10)
                .until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        interaction.setDriver(driver);
                        try {
                            interaction.perform();
                        } catch (IOException | InterruptedException e) {
                            return false;
                        }

                        return true;
                    }
                });
    }
}
