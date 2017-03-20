package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.TestInit;
import de.crispda.sola.multitester.web.WebActions;
import de.crispda.sola.multitester.web.WebElements;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class OwncloudInitEmpty extends TestInit {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Pattern pxPattern = Pattern.compile("^(\\d*(?:\\.\\d*)?)px$");

    @Override
    protected void init() throws InterruptedException {
        Owncloud owncloud = new Owncloud(driver);
        List<WebElement> canvases = driver.findElements(By.id("canvas"));
        if (canvases.isEmpty()) {
            try {
                owncloud.login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new WebDriverWait(driver, 10)
                .ignoring(StaleElementReferenceException.class)
                .until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                WebElement canvas = driver.findElement(By.id("canvas"));
                try {
                    WebActions.sendKeysJSControlShift(canvas, "a");
                    WebActions.sendKeys(canvas, Keys.BACK_SPACE);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
        });

        if (!Optional.ofNullable(new Owncloud.LeftCondition().apply(driver)).orElse(false)) {
            new WebDriverWait(driver, 10)
                    .ignoring(StaleElementReferenceException.class)
                    .until(new ExpectedCondition<Boolean>() {
                        @Override
                        public Boolean apply(WebDriver driver) {
                            Interaction click = new OwncloudClickButton(Owncloud.Button.alignLeft);
                            click.setDriver(driver);
                            try {
                                click.perform();
                            } catch (IOException | InterruptedException e) {
                                return false;
                            }
                            return true;
                        }
                    });

            try {
                new WebDriverWait(driver, 10)
                        .ignoring(StaleElementReferenceException.class)
                        .until(new Owncloud.LeftCondition());
            } catch (TimeoutException e) {

                /*
                double margin = 0d;
                boolean found = false;
                WebElement caretOverlay = ((WebElement) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].parentNode;",
                                driver.findElement(By.id("eventTrap"))));
                String style = caretOverlay.getAttribute("style");
                InputSource source = new InputSource(new StringReader(style));
                CSSOMParser parser = new CSSOMParser(new SACParserCSS3());

                try {
                    CSSStyleDeclaration decl = parser.parseStyleDeclaration(source);
                    String marginLeftValue = decl.getPropertyValue("left");
                    Matcher matcher = pxPattern.matcher(marginLeftValue);
                    if (matcher.find()) {
                        margin = Double.parseDouble(matcher.group(1));
                        found = true;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (found)
                    logger.info(String.format("The left margin was: %f", margin));
                else
                    logger.info("The left margin was: undefined");
                    */
                throw e;
            }
        }

        new WebDriverWait(driver, 15)
                .ignoring(StaleElementReferenceException.class)
                .until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        WebElement toolbar = driver.findElement(By.id("toolbar"));
                        WebElement numberSpinner = toolbar.findElement(
                                By.xpath(".//input[@name='FontPicker']/ancestor::table[@role='listbox']" +
                                        "/following-sibling::div[1]"));
                        WebElement spinbutton = numberSpinner.findElement(By.xpath(".//input[@role='spinbutton']"));

                        Actions builder = new Actions(driver);
                        WebElement spinButtonInput = numberSpinner.findElement(
                                By.xpath(".//input[@role='spinbutton']"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].value = 14",
                                spinButtonInput);
                        Owncloud owncloud = new Owncloud(driver);
                        spinButtonInput.click();
                        try {
                            owncloud.deselect();
                        } catch (IOException | InterruptedException e) {
                            return false;
                        }

                        for (Owncloud.Modification mod : Owncloud.Modification.values()) {
                            if (Optional.ofNullable(owncloud.buttonState(mod.getButtonText())).map(
                                    s -> s.equals("true")).orElse(false)) {
                                owncloud.clickButton(mod.getButtonText());
                            }
                        }

                        WebElement comboBox = toolbar.findElement(
                                By.xpath(".//input[@name='FontPicker']/ancestor::table[@role='listbox']"));
                        WebElement fontValueElement = comboBox.findElement(
                                By.xpath(".//td[contains(concat(' ', @class, ' '), ' dijitButtonContents ')]/div[1]" +
                                "/span[1]/span[1]"));
                        String fontValue = (String) ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].textContent;", fontValueElement);
                        if (Optional.ofNullable(fontValue).map(v -> !v.equals("Arial")).orElse(true)) {
                            builder.clickAndHold(comboBox).perform();
                            try {
                                String widgetid = comboBox.getAttribute("widgetid");

                                Thread.sleep(250);
                                WebElement menuItem = driver.findElement(
                                                By.xpath("//div[@dijitpopupparent='" + widgetid + "']" +
                                                        "/descendant::td[contains(concat(' ', @class, ' '), " +
                                                        "' dijitMenuItemLabel ') and span='Arial']/parent::*"
                                                )
                                        );

                                builder.moveToElement(menuItem).release().perform();
                            } catch (Exception e) {
                                builder.release().perform();
                                return false;
                            }
                        }

                        builder.moveToElement(driver.findElement(By.id("canvas"))).perform();
                        return true;
                    }
                });

        new WebDriverWait(driver, 10)
                .ignoring(StaleElementReferenceException.class)
                .until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                List<WebElement> lines = driver.findElements(By.tagName("text:p"));
                if (lines.size() != 1)
                    return false;

                WebElement line = lines.get(0);
                try {
                    List<Object> childNodes = WebElements.getChildNodes(line);
                    for (Object childNode : childNodes) {
                        if (childNode instanceof WebElement) {
                            WebElement childElement = (WebElement) childNode;
                            String tagName = childElement.getTagName();
                            boolean skip = false;
                            switch (tagName) {
                                case "editinfo":
                                case "cursor":
                                case "text:span":
                                    skip = true;
                            }
                            if (skip)
                                continue;
                            else
                                return false;
                        }

                        if (childNode instanceof String && !childNode.equals(""))
                            return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
        });
    }
}
