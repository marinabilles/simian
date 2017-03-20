package de.crispda.sola.multitester;

import de.crispda.sola.multitester.util.WebServer;

public class TestWebServer extends WebServer {

    public TestWebServer() throws Exception {
        super("./src/test/resources");
    }
}
