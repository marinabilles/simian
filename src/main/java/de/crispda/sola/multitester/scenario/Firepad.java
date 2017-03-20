package de.crispda.sola.multitester.scenario;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.scenario.neutral.FirepadIndentOutdent;
import de.crispda.sola.multitester.scenario.neutral.FirepadOpenCloseDeveloperTools;
import de.crispda.sola.multitester.scenario.neutral.FirepadUndoRedo;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.web.WebActions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Firepad {
    public static final String url = Paths.get("firepadUrl");
    public static final List<Rectangle> exclusionRectangles = new ArrayList<>();
    public static final List<Interaction> neutralEvents = Lists.newArrayList(
            new FirepadIndentOutdent(),
            new FirepadUndoRedo(),
            new FirepadOpenCloseDeveloperTools()
    );
    public static final Set<Interaction> actionSet = createSet();

    private static Set<Interaction> createSet() {
        Set<Interaction> set = Sets.newHashSet(
                new FirepadGotoHome(),
                new FirepadGotoEnd()
        );

        List<CharSequence[]> writes = new ArrayList<>();
        Lists.newArrayList(
                "a", "b", "c", "text", "test", " ", " This", ".", "z", "t", "more text", "-------"
        ).forEach(sw -> writes.add(chars(sw)));

        Lists.newArrayList(
                Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT, Keys.HOME, Keys.END,
                Keys.RETURN, Keys.BACK_SPACE, Keys.DELETE, Keys.TAB
        ).forEach(cw -> writes.add(chars(cw)));

        Lists.newArrayList(
                chars("text", Keys.RETURN),
                chars(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                chars(Keys.BACK_SPACE, "a")
        ).forEach(writes::add);

        writes.forEach(w -> set.add(new FirepadWrite(w)));

        List<Font> fonts = Arrays.asList(Font.values());
        List<Modification> modifications = Arrays.asList(Modification.values());
        for (Selection selection : Selection.values()) {
            fonts.forEach(f -> set.add(new FirepadMakeFont(selection, f)));
            Lists.newArrayList("12", "14", "24").forEach(fs -> set.add(new FirepadMakeFontSize(selection, fs)));
            Lists.newArrayList("black", "red", "blue").forEach(c -> set.add(new FirepadMakeFontColor(selection, c)));
            modifications.forEach(m -> set.add(new FirepadApplyModification(selection, m)));
            set.add(new FirepadDelete(selection));
        }
        Arrays.stream(Button.values()).forEach(b -> set.add(new FirepadClickButton(b)));
        set.add(new FirepadInsertImage());

        return set;
    }

    private static CharSequence[] chars(CharSequence... sequence) {
        return sequence;
    }

    private final WebDriver driver;

    public Firepad(WebDriver driver) {
        this.driver = driver;
    }

    public void hideCursor() {
        WebElement bodyElement = driver.findElement(By.tagName("body"));
        try {
            ((JavascriptExecutor) driver).executeScript(Resources.toString(
                    Resources.getResource("insertFirepadStyle.js"),
                    Charsets.UTF_8), bodyElement);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void focus() throws IOException, InterruptedException {
        WebElement toolbar = driver.findElement(By.className("firepad-toolbar"));
        Actions builder = new Actions(driver);
        builder.moveToElement(toolbar, 0, 0).click().perform();

        ((JavascriptExecutor) driver).executeScript(Resources.toString(Resources.getResource("firepadFocus.js"),
                Charsets.UTF_8));
    }

    public void select(Selection selection) throws IOException, InterruptedException {
        selection.select(driver.findElement(By.tagName("textarea")));
    }

    public void deselect() throws IOException, InterruptedException {
        WebElement textarea = driver.findElement(By.tagName("textarea"));
        WebActions.sendKeysJS(textarea, Keys.RIGHT);
    }

    public enum Button {
        increaseIndent ("firepad-tb-indent-increase"),
        decreaseIndent ("firepad-tb-indent-decrease"),
        bulletList ("firepad-tb-list-2"),
        numberedList ("firepad-tb-numbered-list"),
        checkList ("firepad-tb-list"),
        paragraphLeft ("firepad-tb-paragraph-left"),
        paragraphCenter ("firepad-tb-paragraph-center"),
        paragraphRight ("firepad-tb-paragraph-right"),
        undo ("firepad-tb-undo"),
        redo ("firepad-tb-redo");

        private final String className;
        Button(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
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

    public enum Modification {
        bold ("firepad-tb-bold"),
        italic ("firepad-tb-italic"),
        underlined ("firepad-tb-underline"),
        strikethrough ("firepad-tb-strikethrough");

        private final String className;
        Modification(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }

    static class IsReady implements ExpectedCondition<Boolean> {
        @Override
        public Boolean apply(WebDriver driver) {
            return (Boolean) ((JavascriptExecutor) driver).executeScript("return isfirepadready;");
        }
    }
}
