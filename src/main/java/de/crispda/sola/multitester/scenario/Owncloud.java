package de.crispda.sola.multitester.scenario;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.util.DatabaseConnector;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.web.WebActions;
import de.crispda.sola.multitester.web.WrappedDriver;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Owncloud {
    public static final String url = Paths.get("owncloudUrl");
    private final WebDriver driver;
    public static final List<Rectangle> exclusionRectangles = Lists.newArrayList(
            new Rectangle(883, 45, 77, 784),
            new Rectangle(0, 0, 960, 78));
    public static final Set<Interaction> actionSet = createSet();
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Pattern pxPattern = Pattern.compile("^(\\d*(?:\\.\\d*)?)px$");

    private static Set<Interaction> createSet() {
        Set<Interaction> set = Sets.newHashSet(
                new OwncloudGotoHome(),
                new OwncloudGotoEnd()
        );

        List<CharSequence[]> writes = new ArrayList<>();
        Lists.newArrayList(
                "a", "b", "c", "text", "test", " ", " This", ".", "z", "t", "more text", "-------"
        ).forEach(sw -> writes.add(chars(sw)));

        Lists.newArrayList(
                Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT, Keys.HOME, Keys.END,
                Keys.RETURN, Keys.BACK_SPACE, Keys.DELETE, Keys.TAB
        ).forEach(cw -> writes.add(chars(cw)));

        writes.addAll(Lists.newArrayList(
                chars("text", Keys.RETURN),
                chars(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                chars(Keys.BACK_SPACE, "a")
        ));

        writes.forEach(w -> set.add(new OwncloudWrite(w)));

        List<Font> fonts = Arrays.asList(Font.values());
        List<Modification> modifications = Arrays.asList(Modification.values());
        for (Selection selection : Selection.values()) {
            fonts.forEach(f -> set.add(new OwncloudMakeFont(selection, f)));
            modifications.forEach(m -> set.add(new OwncloudApplyModification(selection, m)));
            set.add(new OwncloudDelete(selection));
            set.add(new OwncloudIncreaseFontSize(selection));
            set.add(new OwncloudDecreaseFontSize(selection));
        }
        Arrays.stream(Button.values())
                .forEach(b -> set.add(new OwncloudClickButton(b)));
        set.add(new FirepadInsertImage());

        return set;
    }

    private static CharSequence[] chars(CharSequence... sequence) {
        return sequence;
    }

    public Owncloud(WebDriver driver) {
        this.driver = driver;
    }

    public void login() throws InterruptedException, IOException {
        List<WebElement> canvases = driver.findElements(By.id("canvas"));
        if (!canvases.isEmpty())
            // already logged in
            return;


        // log in

        if (!(driver instanceof WrappedDriver)) {
            throw new IllegalStateException("driver is not WrappedDriver");
        }
        int number = ((WrappedDriver) driver).getNumber();
        String username = number == 1 ? Paths.get("owncloudUser1") : Paths.get("owncloudUser2");

        if (number == 1) {
            try {
                DatabaseConnector.execute("delete from `oc_documents_session`");
            } catch (SQLException e) {
                logger.warning(ExceptionUtils.getStackTrace(e));
            }
        }

        WebElement user = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("user")));
        user.sendKeys(username);

        String password = number == 1 ? Paths.get("owncloudPass1") : Paths.get("owncloudPass2");
        driver.findElement(By.id("password")).sendKeys(password);
        Thread.sleep(200);
        driver.findElement(By.id("submit")).click();

        // navigate to documents app

        WebElement appnameContainer = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("header-appname-container")));
        appnameContainer.click();

        WebElement documents = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("a[href=\"/owncloud/index.php/apps/documents/index\"]")));
        documents.click();

        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(
                By.className("app-documents")));

        // select file

        String title = number == 1 ? Paths.get("owncloudFilePath1") : Paths.get("owncloudFilePath2");
        WebElement documentLink = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("a[original-title=\"" + title + "\"]")));
        documentLink.click();

        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("editor")));

        WebElement body = driver.findElement(By.tagName("body"));
        ((JavascriptExecutor) driver).executeScript(Resources.toString(
                Resources.getResource("insertOwncloudStyle.js"), Charsets.UTF_8), body);

        (new WebDriverWait(driver, 60)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("office:body")));
        WebElement canvas = driver.findElement(By.id("canvas"));
        WebActions.sendKeysJSControl(canvas, "a");
        Thread.sleep(300);
        WebActions.sendKeys(canvas, Keys.BACK_SPACE);
    }

    public void logout() {
        List<WebElement> closeBs = driver.findElements(By.id("odf-close"));
        if (!closeBs.isEmpty()) {
            closeBs.get(0).click();
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(
                    By.className("app-documents")));
        }

        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(
                By.id("expand"))).click();
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(
                By.id("logout"))).click();

        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("body-login")));
    }

    public void select(Selection selection) throws IOException, InterruptedException {
        selection.select(driver.findElement(By.id("canvas")));
    }

    public void deselect() throws IOException, InterruptedException {
        WebElement textarea = driver.findElement(By.tagName("textarea"));
        WebActions.sendKeysJS(textarea, Keys.RIGHT);
    }

    public void clickButton(String buttonText) {
        List<WebElement> elements = driver.findElements(By.className("dijitButtonText"));
        boolean found = false;
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        for (WebElement element : elements) {
            String textContent = (String) executor.executeScript("return arguments[0].textContent", element);

            if (textContent.equals(buttonText)) {
                found = true;
                WebElement parentNode = (WebElement) executor.executeScript(
                        "return arguments[0].parentNode", element);
                parentNode.click();
                break;
            }
        }
        if (!found) {
            throw new WebDriverException("Button " + buttonText + " not found");
        }
    }

    public String buttonState(String buttonText) {
        List<WebElement> elements = driver.findElements(By.className("dijitButtonText"));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        for (WebElement element : elements) {
            String textContent = (String) executor.executeScript("return arguments[0].textContent", element);

            if (textContent.equals(buttonText)) {
                WebElement parentNode = (WebElement) executor.executeScript(
                        "return arguments[0].parentNode", element);

                return parentNode.getAttribute("aria-pressed");
            }
        }

        throw new WebDriverException("Button " + buttonText + " not found");
    }

    public enum Modification {
        bold ("Bold"),
        italic ("Italic"),
        underlined ("Underline"),
        strikethrough ("Strikethrough");

        private final String buttonText;
        Modification(String buttonText) {
            this.buttonText = buttonText;
        }

        public String getButtonText() {
            return buttonText;
        }
    }

    public enum Font {
        arial ("Arial"),
        timesNewRoman ("Times New Roman"),
        helveticaNeue ("Helvetica Neue"),
        verdana ("Verdana");

        private final String fontName;
        Font(String fontName) {
            this.fontName = fontName;
        }

        public String getFontName() {
            return fontName;
        }
    }

    public enum Button {
        alignLeft ("Align Left"),
        alignCenter ("Center"),
        alignRight ("Align Right"),
        justify ("Justify"),
        decreaseIndent ("Decrease Indent"),
        increaseIndent ("Increase Indent");

        private final String buttonName;
        Button(String buttonName) {
            this.buttonName = buttonName;
        }

        public String getButtonText() {
            return buttonName;
        }
    }

    public static class LeftCondition implements ExpectedCondition<Boolean> {
        @Override
        public Boolean apply(WebDriver driver) {
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
                    double left = Double.parseDouble(matcher.group(1));
                    return left < 80;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    }
}
