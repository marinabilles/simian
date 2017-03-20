package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Combinator;
import de.crispda.sola.multitester.CombinedTest;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.util.DebugAdapter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GDocsFailsafeCombinator extends Adaptable implements Combinator {
    private final boolean shouldWaitForEmpty;
    private boolean hideCursor = true;
    private static final long serialVersionUID = 5133444986583476131L;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public GDocsFailsafeCombinator() {
        this.shouldWaitForEmpty = false;
    }

    public GDocsFailsafeCombinator(boolean shouldWaitForEmpty) {
        this.shouldWaitForEmpty = shouldWaitForEmpty;
    }

    public void setHideCursor(boolean hideCursor) {
        this.hideCursor = hideCursor;
    }

    @Override
    public CombinedTest combine(List<Interaction> scenarios) {
        return combine(scenarios, 9000);
    }

    @Override
    public CombinedTest combineSequential(List<Interaction> scenarios) {
        return combine(scenarios, 500);
    }

    private CombinedTest combine(List<Interaction> scenarios, final long sleepDuration) {
        if (scenarios.stream().anyMatch(s -> !(s instanceof GDocsInteraction)))
            throw new IllegalArgumentException("Non-GDocs interaction passed to GDocsFailsafeCombinator");
        List<GDocsInteraction> gDocsInteractionList =
                scenarios.stream().map(s -> (GDocsInteraction) s).collect(Collectors.toList());
        final int maybeWaitCount = scenarios.size() + 1;
        DebugAdapter adapter = debug(a -> {}).orElse(null);

        CombinedTest combinedTest = new CombinedTest() {
            @Override
            public List<Interaction> getInteractions() {
                return scenarios;
            }

            @Override
            public void test() throws Exception {
                GDocs gDocs = new GDocs(driver);
                debug(gDocs::setDebugAdapter);
                debug(a -> a.write("entering test"));
                if (shouldWaitForEmpty) {
                    new WebDriverWait(driver, 10)
                            .ignoring(StaleElementReferenceException.class)
                            .until(new GDocs.IsEmpty(true));
                }

                if (hideCursor)
                    gDocs.hideCursor();
                maybeWait();
                debug(a -> a.write("after maybewait"));
                for (GDocsInteraction scenario : gDocsInteractionList) {
                    debug(a -> a.write(scenario.toString()));
                    scenario.setDriver(driver);
                    try {
                        scenario.perform();
                    } catch (Exception e) {
                        logger.warning(ExceptionUtils.getStackTrace(e));
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
                return GDocs.url;
            }
        };

        combinedTest.setDebugAdapter(adapter);
        return combinedTest;
    }
}
