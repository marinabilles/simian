package de.crispda.sola.multitester.web;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import de.crispda.sola.multitester.TestFixture;
import de.crispda.sola.multitester.scenario.GDocs;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.io.StringReader;

public class WebElementsTest extends TestFixture {
    @Test
    public void getStyle() throws Exception {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        try {
            driver.get(GDocs.url);
            (new WebDriverWait(driver, 15)).until(ExpectedConditions.presenceOfElementLocated(
                    By.className(GDocs.lineClass)));
            GDocs gDocs = new GDocs(driver);
            WebElement lastLine = gDocs.getLastLine();
            WebElement lineView = lastLine.findElement(
                    By.xpath(".//ancestor::div[contains(concat(' ', @class, ' ')," +
                    " ' kix-lineview-content ')]"));
            String style = lineView.getAttribute("style");
            InputSource source = new InputSource(new StringReader(style));
            CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
            CSSStyleDeclaration decl = parser.parseStyleDeclaration(source);
            System.out.println(style);
            System.out.println("margin-left: " + decl.getPropertyValue("margin-left"));
            Thread.sleep(500);
        } finally {
            driver.quit();
        }
    }
}