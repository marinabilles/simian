package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Combinator;
import de.crispda.sola.multitester.CombinedTest;
import de.crispda.sola.multitester.Interaction;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OwncloudCombinator implements Combinator {
    protected final String url;
    private boolean logOut = true;
    private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public OwncloudCombinator(String url) {
        this.url = url;
    }

    @Override
    public CombinedTest combine(List<Interaction> scenarios) {
        return combine(scenarios, 12000);
    }

    @Override
    public CombinedTest combineSequential(List<Interaction> scenarios) {
        return combine(scenarios, 500);
    }

    private CombinedTest combine(List<Interaction> scenarios, final long sleepDuration) {
        if (scenarios.stream().anyMatch(s -> !(s instanceof OwncloudInteraction)))
            throw new IllegalArgumentException("Non-Owncloud interaction passed to OwncloudCombinator");
        List<OwncloudInteraction> owncloudInteractions =
                scenarios.stream().map(s -> (OwncloudInteraction) s).collect(Collectors.toList());
        final int maybeWaitCount = scenarios.size() + 1;
        return new CombinedTest() {
            @Override
            public List<Interaction> getInteractions() {
                return scenarios;
            }

            @Override
            public void test() throws Exception {
                Owncloud owncloud = new Owncloud(driver);

                Exception ex = null;
                try {
                    owncloud.login();
                    maybeWait();
                    for (OwncloudInteraction scenario : owncloudInteractions) {
                        scenario.setDriver(driver);
                        scenario.perform();
                        Thread.sleep(sleepDuration);
                        maybeWait();
                    }
                } catch (Exception e) {
                    ex = e;
                    throw e;
                } finally {
                    if (logOut) {
                        try {
                            owncloud.logout();
                        } finally {
                            if (ex != null)
                                logger.warning("Original exception before logout:\n" +
                                        ExceptionUtils.getStackTrace(ex));
                        }
                    }
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

    public void setLogOut(boolean logOut) {
        this.logOut = logOut;
    }
}
