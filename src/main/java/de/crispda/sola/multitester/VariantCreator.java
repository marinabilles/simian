package de.crispda.sola.multitester;

import java.util.Collection;
import java.util.List;

public interface VariantCreator {
    void put(MultiTest<Test> test);
    void putAll(Collection<MultiTest<Test>> tests);
    List<ScheduleVariant<?>> createVariants();
}
