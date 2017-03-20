package de.crispda.sola.multitester;

import java.util.Collection;
import java.util.Set;

public interface MultiTestGenerator {
    void put(Test t);
    void putAll(Collection<Test> ts);
    Set<MultiTest<Test>> generateTests();
}
