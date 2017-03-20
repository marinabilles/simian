package de.crispda.sola.multitester.scenario;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EqualityTest {
    @Test
    public void equalityTest() throws Exception {
        Assert.assertTrue(new GDocsWrite("a").equals(new GDocsWrite("a")));
    }
}
