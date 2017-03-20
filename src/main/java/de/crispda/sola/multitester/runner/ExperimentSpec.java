package de.crispda.sola.multitester.runner;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.List;

public abstract class ExperimentSpec implements Serializable {
    private final String name;
    public final List<Rectangle> exclusionRectangles;

    protected ExperimentSpec(String name, List<Rectangle> exclusionRectangles) {
        this.name = name;
        this.exclusionRectangles = exclusionRectangles;
    }

    public String getName() {
        return name;
    }

    public static List<ExperimentSpec> getExperimentSpecs() {
        return ExperimentSpecs.specList;
    }

    public static ExperimentSpec forName(String name) {
        return ExperimentSpec.getExperimentSpecs().stream()
                .filter(s -> s.getName().equals(name)).findFirst()
                .orElseThrow(() -> new RuntimeException("Spec not found."));
    }
}
