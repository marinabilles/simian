package de.crispda.sola.multitester.scenario;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import de.crispda.sola.multitester.Exchanges;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.Exchanger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GDocsCursorSender extends GDocsInteraction implements Exchanges {
    private transient Exchanger<Point> exchanger;
    private static final long serialVersionUID = 1L;
    private static final Pattern pxPattern = Pattern.compile("^([0-9]*)px$");

    @Override
    public void setExchanger(Exchanger<Point> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        Point cursorPoint = new WebDriverWait(driver, 10)
                .ignoring(StaleElementReferenceException.class)
                .until(new ExpectedCondition<Point>() {
            @Override
            public Point apply(WebDriver driver) {
                WebElement cursor = driver.findElement(By.xpath("//div[contains(concat(' ', @class, ' '), " +
                        "' kix-appview-editor ')]/div[2]"));
                String cursorStyle = cursor.getAttribute("style");
                InputSource source = new InputSource(new StringReader(cursorStyle));
                CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
                CSSStyleDeclaration decl;
                try {
                    decl = parser.parseStyleDeclaration(source);
                } catch (IOException e) {
                    return null;
                }
                String leftPos = decl.getPropertyValue("left");
                String topPos = decl.getPropertyValue("top");
                int left = 0, top = 0;
                Matcher leftMatcher = pxPattern.matcher(leftPos);
                if (leftMatcher.find())
                    left = Integer.parseInt(leftMatcher.group(1));
                Matcher topMatcher = pxPattern.matcher(topPos);
                if (topMatcher.find())
                    top = Integer.parseInt(topMatcher.group(1));

                return new Point(left, top);
            }
        });

        exchanger.exchange(cursorPoint);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
