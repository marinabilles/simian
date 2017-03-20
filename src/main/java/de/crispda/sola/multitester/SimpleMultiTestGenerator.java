package de.crispda.sola.multitester;

import java.util.*;

public class SimpleMultiTestGenerator implements MultiTestGenerator {
    private final List<Test> scenarios;

    public SimpleMultiTestGenerator() {
        scenarios = new ArrayList<>();
    }

    @Override
    public void put(Test t) {
        scenarios.add(t);
    }

    @Override
    public void putAll(Collection<Test> ts) {
        scenarios.addAll(ts);
    }

    @Override
    public Set<MultiTest<Test>> generateTests() {
        Set<MultiTest<Test>> result = new HashSet<>();
        for (int i = 0; i < scenarios.size(); i++) {
            for (int j = i + 1; j < scenarios.size(); j++) {
                result.add(new MultiTest<>(scenarios.get(i), scenarios.get(j)));
            }
        }
        return result;
    }
}
