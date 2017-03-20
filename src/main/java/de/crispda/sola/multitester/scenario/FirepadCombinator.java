package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Combinator;
import de.crispda.sola.multitester.CombinedTest;
import de.crispda.sola.multitester.Interaction;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

public class FirepadCombinator extends Adaptable implements Combinator {
    private final String url;
    private final boolean hideCursor;

    public FirepadCombinator(String url) {
        this.url = url;
        this.hideCursor = true;
    }

    public FirepadCombinator(String url, boolean hideCursor) {
        this.url = url;
        this.hideCursor = hideCursor;
    }

    @Override
    public CombinedTest combine(List<Interaction> scenarios) {
        return combine(scenarios, 7000);
    }

    @Override
    public CombinedTest combineSequential(List<Interaction> scenarios) {
        return combine(scenarios, 500);
    }

    private CombinedTest combine(List<Interaction> scenarios, final long sleepDuration) {
        if (scenarios.stream().anyMatch(s -> !(s instanceof FirepadInteraction)))
            throw new IllegalArgumentException("Non-Firepad interaction passed to FirepadCombinator");
        List<FirepadInteraction> firepadInteractions =
                scenarios.stream().map(s -> (FirepadInteraction) s).collect(Collectors.toList());
        final int maybeWaitCount = scenarios.size() + 1;
        return new CombinedTest() {
            @Override
            public List<Interaction> getInteractions() {
                return scenarios;
            }

            @Override
            public void test() throws Exception {
                (new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(
                        By.className("CodeMirror-scroll")));

                new WebDriverWait(driver, 10).until(new Firepad.IsReady());
                Firepad firepad = new Firepad(driver);
                if (hideCursor)
                    firepad.hideCursor();
                Interaction click = new FirepadClick();
                click.setDriver(driver);
                try {
                    click.perform();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                maybeWait();
                for (FirepadInteraction scenario : firepadInteractions) {
                    debug(a -> a.write(scenario.toString()));
                    scenario.setDriver(driver);
                    try {
                        scenario.perform();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(sleepDuration);
                    maybeWait();
                }
            }

            @Override
            public int getMaybeWaitCount() {
                return maybeWaitCount;
            }

            @Override
            public String getInitialURL() {
                return url;
            }
        };
    }
}
