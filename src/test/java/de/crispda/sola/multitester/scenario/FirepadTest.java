package de.crispda.sola.multitester.scenario;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.Combinator;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.SingleThreadedExecutor;
import de.crispda.sola.multitester.TestInit;
import de.crispda.sola.multitester.web.Drivers;
import de.crispda.sola.multitester.web.Firefox;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import java.util.List;

public class FirepadTest {
    @Test
    public void firepadTest() throws Exception {
        testFirepadInteractions(Lists.newArrayList(
                new FirepadClick(),
                new FirepadGotoHome(),
                new FirepadWrite("Some text", Keys.RETURN),
                new FirepadWrite(Keys.LEFT),
                new FirepadGotoEnd(),
                new FirepadWrite("second text"),
                new FirepadWrite(Keys.BACK_SPACE),
                new FirepadWrite(Keys.BACK_SPACE),
                new FirepadDelete(Selection.LineBefore)
        ));
    }

    @Test
    public void comicSansTest() throws Exception {
        testFirepadInteractions(Lists.newArrayList(
                new FirepadWrite("Some example text"),
                new FirepadDelete(Selection.WordBefore),
                new FirepadDelete(Selection.WordBefore),
                new FirepadDelete(Selection.WordBefore)
        ));
    }

    @Test
    public void ac5test() throws Exception {
        testFirepadInteractions(Lists.newArrayList(
                new FirepadWrite("Some example text"),
                new FirepadMakeFontSize(Selection.LineBefore, "18"),
                new FirepadMakeFont(Selection.LineBefore, Firepad.Font.verdana)
        ));
    }

    private void testFirepadInteractions(List<Interaction> interactions) throws InterruptedException {
        Combinator combinator = new FirepadCombinator(Firepad.url, false);
        de.crispda.sola.multitester.Test test = combinator.combine(interactions);

        SingleThreadedExecutor executor = new SingleThreadedExecutor(Drivers.firefoxDriver(Firefox.ESR));
        executor.scheduleAll(Lists.newArrayList(test));
        executor.setInit(new FirepadInitEmpty());
        executor.execute();
        Thread.sleep(2000);
    }

    @Test
    public void firepadFocusTest() throws Exception {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        try {
            driver.get(Firepad.url + "?footer");
            Firepad firepad = new Firepad(driver);
            Thread.sleep(10000);
            //WebElement footer = driver.findElement(By.id("footer"));
            //footer.click();
            Actions builder = new Actions(driver);
            WebElement toolbar = driver.findElement(By.className("firepad-toolbar"));
            builder.moveToElement(toolbar, 0, 0).click().perform();
            firepad.focus();
            Thread.sleep(10000);
        } finally {
            driver.quit();
        }
    }

    @Test
    public void firepadInitEmpty() throws Exception {
        WebDriver driver = Drivers.firefoxDriver(Firefox.ESR).get(1);
        try {
            driver.get(Firepad.url + "?footer");
            TestInit init = new FirepadInitEmpty();
            init.run(driver);
            Thread.sleep(5000);
        } finally {
            driver.quit();
        }
    }
}
