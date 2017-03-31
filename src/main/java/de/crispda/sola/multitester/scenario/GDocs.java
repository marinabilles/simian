package de.crispda.sola.multitester.scenario;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.scenario.neutral.GDocsInteractionIncreaseDecreaseIndent;
import de.crispda.sola.multitester.scenario.neutral.GDocsInteractionOpenCloseMenu;
import de.crispda.sola.multitester.scenario.neutral.GDocsInteractionUndoRedo;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.web.WebActions;
import de.crispda.sola.multitester.web.WebElements;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GDocs extends Adaptable {
    public static final String url = Paths.get("gDocsUrl");
    public static final String lineClass = "kix-wordhtmlgenerator-word-node";
    public static final String contentClass = "kix-page-content-wrapper";
    public static final String eventTargetFrameClass = "docs-texteventtarget-iframe";
    static final String caretClass = "kix-cursor-caret";
    static final String cursorClass = "kix-cursor";
    static final String cursorTopClass = "kix-cursor-top";
    static final String cursorNameClass = "kix-cursor-name";
    static final String selectionClass = "kix-selection-overlay";
    public static final String cellClass = "kix-cellrenderer";
    private static final Pattern pxPattern = Pattern.compile("^([0-9]*)px$");

    public static final List<Rectangle> exclusionRectangles = ImmutableList.of(new Rectangle(0, 0, 960, 111));
    public static final List<Interaction> neutralEvents = ImmutableList.of(
            // new GDocsInteractionAddCommentCancel(),
            new GDocsInteractionIncreaseDecreaseIndent(),
            new GDocsInteractionOpenCloseMenu(),
            new GDocsInteractionUndoRedo()
    );

    public static final Set<Interaction> actionSet = createSet();

    private static Set<Interaction> createSet() {
        Set<Interaction> set = Sets.newHashSet(
                new GDocsInteractionInsertTable(),
                new GDocsInteractionTableAddColumn()
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

        writes.forEach(w -> set.add(new GDocsWrite(w)));

        List<Modification> modifications = Arrays.asList(Modification.values());
        for (Selection selection : Selection.values()) {
            modifications.forEach(m -> set.add(new GDocsApplyModification(selection, m)));
            set.add(new GDocsDelete(selection));
        }
        Arrays.stream(Button.values()).forEach(b -> set.add(new GDocsButtonClick(b)));

        return set;
    }

    private static CharSequence[] chars(CharSequence... sequence) {
        return sequence;
    }

    private final WebDriver driver;

    public GDocs(WebDriver driver) {
        this.driver = driver;
    }

    public void hideCursor() {
        debug(a -> a.write("Running hideCursor"));
        WebElement bodyElement = driver.findElement(By.tagName("body"));
        try {
            ((JavascriptExecutor) driver).executeScript(Resources.toString(Resources.getResource("insertGDocsStyle.js"),
                    Charsets.UTF_8), bodyElement);
            debug(a -> a.write("Inserted stylesheet"));
            WebActions.click(driver.findElement(By.id("docs-view-menu")));
            By spellingSelector = By.cssSelector("[aria-label=\"Show spelling suggestions, s;\"]");
            WebElement spelling = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(
                    spellingSelector));
            WebActions.click(spelling);
            new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(
                    spellingSelector));
        } catch (IOException e) {
            debug(a -> a.write("Threw exception in hideCursor"));
            throw new RuntimeException(e);
        }
    }

    public Optional<String> getLineText(WebElement line) throws IOException {
        List<Object> childNodes = WebElements.getChildNodes(line);
        if (childNodes.isEmpty())
            return Optional.empty();
        Object firstChildNode = childNodes.get(0);
        if (!(firstChildNode instanceof String))
            return Optional.empty();
        return Optional.of((String) firstChildNode);
    }

    public WebElement getLastLine() {
        WebElement lastLine = null;
        boolean done = false;
        Instant before = Instant.now();
        while (!done) {
            try {
                lastLine = getLastLineInternal();
                done = true;
            } catch (WebDriverException e) {
                // try again....
                if (Duration.between(before, Instant.now()).compareTo(Duration.ofSeconds(10)) > 0) {
                    throw e;
                }
            }
        }

        return lastLine;
    }

    private WebElement getLastLineInternal() {
        List<WebElement> lines = driver.findElements(
                By.xpath("//div[contains(concat(' ', @class, ' '), ' " + contentClass +
                        " ')]/div[1]/descendant::span[contains(concat(' ', @class, ' '), ' " +
                        GDocs.lineClass + " ')]"));
        if (!lines.isEmpty())
            return lines.get(lines.size() - 1);
        return null;
    }

    public boolean hasSelection() {
        List<WebElement> selections = driver.findElements(By.className(selectionClass));
        return !selections.isEmpty();
    }

    private WebElement getTextbox() {
        WebElement eventTargetFrame = driver.findElement(By.className(eventTargetFrameClass));
        driver.switchTo().frame(eventTargetFrame);
        return driver.findElement(By.cssSelector("[role=\"textbox\"]"));
    }

    public void sendKeys(CharSequence... keys) throws IOException, InterruptedException {
        WebActions.sendKeysJS(getTextbox(), keys);
        driver.switchTo().defaultContent();
    }

    public void sendKeysControl(CharSequence... keys) throws IOException, InterruptedException {
        WebActions.sendKeysJSControl(getTextbox(), keys);
        driver.switchTo().defaultContent();
    }

    public void sendKeysControlShift(CharSequence... keys) throws IOException, InterruptedException {
        WebActions.sendKeysJSControlShift(getTextbox(), keys);
        driver.switchTo().defaultContent();
    }

    public void sendKeysShift(CharSequence... keys) throws IOException, InterruptedException {
        WebActions.sendKeysJSShift(getTextbox(), keys);
        driver.switchTo().defaultContent();
    }

    public void unbold() throws InterruptedException {
        WebElement boldButton = driver.findElement(By.id("boldButton"));
        if (boldButton.getAttribute("aria-pressed").equals("true")) {
            WebActions.click(boldButton);
            Actions builder = new Actions(driver);
            builder.moveToElement(getLastLine()).build().perform();
            Thread.sleep(500);
        }
    }

    public void select(Selection selection) throws IOException, InterruptedException {
        selection.select(getTextbox());
        driver.switchTo().defaultContent();
    }

    public void deselect() throws IOException, InterruptedException {
        WebActions.sendKeysJS(getTextbox(), Keys.RIGHT);
        driver.switchTo().defaultContent();
    }

    public Optional<Integer> getLeftMargin(WebElement line) {
        WebElement lineView = line.findElement(
                By.xpath(".//ancestor::div[contains(concat(' ', @class, ' ')," +
                        " ' kix-lineview-content ')]"));
        String style = lineView.getAttribute("style");
        InputSource source = new InputSource(new StringReader(style));
        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());

        Optional<Integer> leftMargin = Optional.empty();
        try {
            CSSStyleDeclaration decl = parser.parseStyleDeclaration(source);
            String marginLeftValue = decl.getPropertyValue("margin-left");
            Matcher matcher = pxPattern.matcher(marginLeftValue);
            if (matcher.find()) {
                leftMargin = Optional.of(Integer.parseInt(matcher.group(1)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return leftMargin;
    }

    public enum Modification {
        bold ("boldButton"),
        italic ("italicButton"),
        underlined ("underlineButton");

        private final String mod;
        Modification(String mod) {
            this.mod = mod;
        }
        public String get() {
            return mod;
        }
    }

    public enum Button {
        undo ("undoButton"),
        redo("redoButton");

        private final String buttonId;
        Button(String buttonId) {
            this.buttonId = buttonId;
        }
        public String get() {
            return buttonId;
        }
    }

    public static class IsEmpty implements ExpectedCondition<Boolean> {
        private boolean secondStep = false;

        public IsEmpty() {
        }

        public IsEmpty(boolean secondStep) {
            this.secondStep = secondStep;
        }

        @Override
        public Boolean apply(WebDriver driver) {
            if (secondStep) {
                Optional<WebElement> lastLine = isLineEmpty(driver);
                return lastLine.isPresent() && isButtonsReset(driver)
                        && isIntendationReset(driver, lastLine.get());
            } else {
                return isLineEmpty(driver).isPresent();
            }
        }

        private boolean isIntendationReset(WebDriver driver, WebElement lastLine) {
            GDocs gDocs = new GDocs(driver);
            Optional<Integer> margin = gDocs.getLeftMargin(lastLine);
            return margin.isPresent() && margin.get() == 0;
        }
    }

    // returns empty optional if the line is not empty
    // otherwise returns WebElement object representing the empty line
    public static Optional<WebElement> isLineEmpty(WebDriver driver) {
        List<WebElement> lines = driver.findElements(
                By.xpath("//div[contains(concat(' ', @class, ' '), ' " + contentClass +
                        " ')]/div[1]/descendant::span[contains(concat(' ', @class, ' '), ' " +
                        GDocs.lineClass + " ')]"));
        if (lines.isEmpty())
            return Optional.empty();

        if (lines.size() > 1)
            return Optional.empty();

        WebElement line = lines.get(0);
        String textContent = (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].textContent;", line);

        if (textContent.equals("\u00A0")) {  // == &nbsp;
            WebElement parent = line.findElement(By.xpath(".//parent::*"));
            String style = parent.getAttribute("style");
            InputSource source = new InputSource(new StringReader(style));
            CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
            try {
                CSSStyleDeclaration decl = parser.parseStyleDeclaration(source);
                String paddingLeftValue = decl.getPropertyValue("padding-left");
                Matcher pxMatcher = pxPattern.matcher(paddingLeftValue);
                if (pxMatcher.find()) {
                    int padding = Integer.parseInt(pxMatcher.group(1));
                    if (padding != 0)
                        return Optional.empty();
                } else {
                    return Optional.empty();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
            return Optional.of(line);
        } else {
            return Optional.empty();
        }
    }

    public static boolean isButtonsReset(WebDriver driver) {
        for (GDocs.Modification m : GDocs.Modification.values()) {
            WebElement button = driver.findElement(By.id(m.get()));
            if (Optional.ofNullable(button.getAttribute("aria-pressed"))
                    .map(at -> at.equals("true")).orElse(false)) {
                return false;
            }
        }

        return true;
    }

    public static class LastLineLeftMarginCondition implements ExpectedCondition<Optional<Integer>> {
        @Override
        public Optional<Integer> apply(WebDriver driver) {
            GDocs gDocs = new GDocs(driver);
            return gDocs.getLeftMargin(gDocs.getLastLine());
        }
    }

    public enum Font {
        comicSans ("Comic Sans MS"),
        courierNew ("Courier New"),
        arial ("Arial"),
        timesNewRoman ("Times New Roman"),
        verdana ("Verdana");

        private final String fontName;
        Font(String fontName) {
            this.fontName = fontName;
        }

        public String getFontName() {
            return fontName;
        }
    }
}
