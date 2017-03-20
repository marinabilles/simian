package de.crispda.sola.multitester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleVariantCreator implements VariantCreator {
    private final List<MultiTest<Test>> testList;

    public SimpleVariantCreator() {
        testList = new ArrayList<>();
    }

    @Override
    public void put(MultiTest<Test> test) {
        testList.add(test);
    }

    @Override
    public void putAll(Collection<MultiTest<Test>> tests) {
        testList.addAll(tests);
    }

    @Override
    public List<ScheduleVariant<?>> createVariants() {
        return testList.stream()
                .map(ScheduleVariant::createInitial).collect(Collectors.toList());
    }
}
