package de.crispda.sola.multitester;

import java.util.ArrayList;

public class AppearanceList extends ArrayList<Appearance> {
    public boolean isConflicted() {
        return this.stream().anyMatch(a -> a.conflict);
    }
}
