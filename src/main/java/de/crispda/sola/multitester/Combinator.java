package de.crispda.sola.multitester;

import java.io.Serializable;
import java.util.List;

public interface Combinator extends Serializable {
    CombinedTest combine(final List<Interaction> scenarios);
    CombinedTest combineSequential(final List<Interaction> scenarios);
}
