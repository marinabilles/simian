package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.TransitionSequence;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.ArrayList;
import java.util.List;

public class IndependenceRun {
    private final List<StringProperty> independencePropertyList;
    private final StringProperty sequence;

    public IndependenceRun(int id, TransitionSequence sequence, List<Boolean> independence) {
        independencePropertyList = new ArrayList<>();
        this.sequence = new SimpleStringProperty(String.format("%d - %s", id, sequence));
        independence.forEach(i -> independencePropertyList.add(new SimpleStringProperty(i ? "" : "x")));
    }

    public List<StringProperty> getIndependencePropertyList() {
        return independencePropertyList;
    }

    @SuppressWarnings("unused")
    public StringProperty sequenceProperty() {
        return sequence;
    }
}
