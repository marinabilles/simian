package de.crispda.sola.multitester;

import com.google.common.collect.Sets;
import de.crispda.sola.multitester.scenario.*;
import de.crispda.sola.multitester.web.Drivers;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.ArrayList;

public class ProbabilisticExplorerTest {
    @org.testng.annotations.Test
    public void runFirepad() throws Exception {
        ProbabilisticExplorer explorer = new ProbabilisticExplorer(
                Sets.newHashSet(
                        new FirepadWrite("a"),
                        new FirepadWrite("b"),
                        new FirepadWrite(Keys.LEFT),
                        new FirepadWrite(Keys.RIGHT),
                        new FirepadWrite(Keys.HOME),
                        new FirepadWrite(Keys.END),
                        new FirepadWrite(" text "),
                        new FirepadWrite(Keys.RETURN),
                        new FirepadDelete(Selection.LineBefore)
                ),
                new SeleniumRunner(Drivers.remoteDriver(), new FirepadInitEmpty(),
                        new ArrayList<>(), Firepad.neutralEvents, true),
                new FirepadCombinator(Firepad.url));

        explorer.explore(5, Duration.ofMinutes(10));
    }
}
