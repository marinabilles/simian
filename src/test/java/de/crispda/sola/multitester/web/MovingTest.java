package de.crispda.sola.multitester.web;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import de.crispda.sola.multitester.util.Paths;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovingTest {
    @Test
    public void movingTest() throws Exception {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        try {
            driver.get(Paths.get("gDocsUrl"));
            WebElement cursor = driver.findElement(By.xpath("//div[contains(concat(' ', @class, ' '), " +
                    "' kix-appview-editor ')]/div[2]"));
            String cursorStyle = cursor.getAttribute("style");
            InputSource source = new InputSource(new StringReader(cursorStyle));
            CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
            CSSStyleDeclaration decl = parser.parseStyleDeclaration(source);
            Pattern pxPattern = Pattern.compile("^([0-9]*)px$");
            String leftPos = decl.getPropertyValue("left");
            String topPos = decl.getPropertyValue("top");
            int left = 0, top = 0;
            Matcher leftMatcher = pxPattern.matcher(leftPos);
            if (leftMatcher.find())
                left = Integer.parseInt(leftMatcher.group(1));
            Matcher topMatcher = pxPattern.matcher(topPos);
            if (topMatcher.find())
                top = Integer.parseInt(topMatcher.group(1));

            System.out.println("position: (left: " + left + ", top: " + top + ")");

            WebActions.moveToTopLeft(driver.findElement(By.className("kix-appview-editor")))
                    .moveByOffset(199, 105)
                    .click()
                    .perform();

            Thread.sleep(10000);
        } finally {
            driver.quit();
        }
    }
}
