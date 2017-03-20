package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.GDocsInteraction;

import java.io.IOException;

public class GDocsInteractionUndoRedo extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        gDocs.sendKeysControl("z");
        Thread.sleep(500);
        gDocs.sendKeysControl("y");
        Thread.sleep(250);
        gDocs.deselect();
        Thread.sleep(2000);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
