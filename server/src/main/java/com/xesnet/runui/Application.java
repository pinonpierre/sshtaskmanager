package com.xesnet.runui;

import com.xesnet.runui.model.Config;
import com.xesnet.runui.yaml.Yaml;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Application {

    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : null;

        new Application(path);
    }

    private Application(String path) {
        Yaml yaml = Yaml.init(Paths.get(path));

        Config config = yaml.readConfig();
        TokenRegistry.init(config.getTokenTimeout());

        startServer(config.getPath(), config.getPort());
    }

    private void startServer(String contextPath, Integer port) {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath(contextPath);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "com.xesnet.runui.ws");

        Server server = new Server(port);
        server.setHandler(servletContextHandler);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            server.destroy();
        }
    }
}
