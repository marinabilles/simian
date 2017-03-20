package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.*;

import java.util.List;

public class GDocsInteractions implements Combinator {
    @Override
    public CombinedTest combine(final List<Interaction> scenarios) {
        return create(scenarios);
    }

    @Override
    public CombinedTest combineSequential(List<Interaction> scenarios) {
        return combine(scenarios);
    }

    public static CombinedTest create(final List<Interaction> scenarios) {
        if (scenarios.stream().anyMatch(s -> !(s instanceof GDocsInteraction)))
            throw new IllegalArgumentException("Non-GDocs interaction passed to GDocsInteractions");
        final int maybeWaitCount = scenarios.size() + 1;
        return new CombinedTest() {
            @Override
            public List<Interaction> getInteractions() {
                return scenarios;
            }

            @Override
            public void test() throws Exception {
                GDocs gDocs = new GDocs(driver);
                gDocs.hideCursor();
                maybeWait();
                for (Interaction scenario : scenarios) {
                    scenario.setDriver(driver);
                    scenario.perform();
                    Thread.sleep(7000);
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
    }
}
