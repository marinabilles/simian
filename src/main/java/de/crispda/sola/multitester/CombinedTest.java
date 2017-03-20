package de.crispda.sola.multitester;

import java.util.List;

public abstract class CombinedTest extends Test {
    public abstract List<Interaction> getInteractions();
}
