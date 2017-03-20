package de.crispda.sola.multitester.util;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.net.URI;

public class WebServer {
    private final Server server;
    private URI serverURI;

    public WebServer(String resourceBase) throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto port selection
        server.addConnector(connector);

        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(resourceBase);
        context.setHandler(resourceHandler);
        server.setHandler(context);

        server.start();
        String host = connector.getHost();
        if (host == null)
            host = "localhost";
        int port = connector.getLocalPort(); // get automatically selected port
        serverURI = new URI(String.format("http://%s:%d", host, port));
    }

    public String get(String path) {
        return String.format("%s/%s", serverURI.toString(), path);
    }

    public void stop() throws Exception {
        server.stop();
    }
}
