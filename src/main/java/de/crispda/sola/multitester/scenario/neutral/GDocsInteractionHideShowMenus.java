package de.crispda.sola.multitester.scenario.neutral;

import de.crispda.sola.multitester.scenario.GDocsInteraction;

import java.io.IOException;

public class GDocsInteractionHideShowMenus extends GDocsInteraction {
    private static final long serialVersionUID = 1L;
    @Override
    public void perform() throws IOException, InterruptedException {
        gDocs.sendKeysControlShift("f");
        Thread.sleep(500);
        gDocs.sendKeysControlShift("f");
        Thread.sleep(500);
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
