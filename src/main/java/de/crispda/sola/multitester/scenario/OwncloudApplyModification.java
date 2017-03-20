package de.crispda.sola.multitester.scenario;

import java.io.IOException;
import java.util.Objects;

public class OwncloudApplyModification extends OwncloudInteraction {
    private static final long serialVersionUID = 1L;
    private final Selection selection;
    private final Owncloud.Modification modification;

    public OwncloudApplyModification(Selection selection, Owncloud.Modification modification) {
        this.selection = selection;
        this.modification = modification;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        owncloud.select(selection);
        owncloud.clickButton(modification.getButtonText());
        owncloud.deselect();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + selection.toString() + ", " + modification.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwncloudApplyModification that = (OwncloudApplyModification) o;
        return selection == that.selection &&
                modification == that.modification;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selection, modification);
    }
}
