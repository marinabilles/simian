package de.crispda.sola.multitester.util;

import org.testng.annotations.Test;

public class PathsTest {

    @Test(expectedExceptions = PropertyNotFoundException.class)
    public void pathsTest() throws Exception {
        System.out.println(Paths.get("nonexistant"));
    }
}
