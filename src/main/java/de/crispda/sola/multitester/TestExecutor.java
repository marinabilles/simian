package de.crispda.sola.multitester;

import java.util.Collection;

public interface TestExecutor<V> {
    void scheduleAll(Collection<V> tests);

    void execute();
}
