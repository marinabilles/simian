package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.Interaction;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

public class InteractionTest {
    @Test
    public void toStringTest() throws Exception {
        Interaction interaction = new FirepadDelete(Selection.LineAfter);
        System.out.println(interaction.toString());

        Interaction font = new FirepadMakeFont(Selection.LineBefore, Firepad.Font.arial);
        System.out.println(font.toString());

        CharSequence returns = Keys.RETURN;
        System.out.println(((Keys) returns).name());
    }
}
