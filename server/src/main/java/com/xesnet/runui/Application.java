package com.xesnet.runui;

import com.xesnet.runui.context.AppContext;
import com.xesnet.runui.model.Config;
import com.xesnet.runui.registry.TokenRegistry;
import com.xesnet.runui.server.UIRewriteHandler;
import com.xesnet.runui.server.WsApplication;
import com.xesnet.runui.yaml.Yaml;
import com.xesnet.runui.yaml.YamlContext;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xesnet.runui.util.PathUtil.getJarDir;


public class Application {

    private static Logger LOG;

    private static final String CONFIG_DIRECTORY = "config";

    //TODO: SSH (Password, Private Key, Password, Command, Interactive)
    //TODO: Interface (React? Other?)
    //TODO: UI: Button (Info + Action) => Form
    //TODO: Build, Docker
    //TODO: Include webapp in jar or put webapp directory next to the jar file?

    public static void main(String[] args) {
        //Init Log
        System.setProperty("org.eclipse.jetty.util.log.announce", "false");
        System.setProperty("org.eclipse.jetty.LEVEL", "WARN");

        System.setProperty("java.util.logging.ConsoleHandler.level", "INFO");
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOG = Logger.getLogger(Application.class.getName());

        String path = args.length > 0 ? args[0] : getJarDir(Application.class) + File.separator + CONFIG_DIRECTORY;

        LOG.info("Application start...");

        try {
            new Application(path);
        } catch (YamlContext.YamlContextException | ApplicationProperties.ApplicationPropertiesException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    private Application(String path) throws YamlContext.YamlContextException, ApplicationProperties.ApplicationPropertiesException {
        Yaml yaml = new Yaml(Paths.get(path));

        Config config = yaml.readConfig();
        TokenRegistry tokenRegistry = new TokenRegistry(config.getTokenTimeout());

        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.init();

        AppContext appContext = new AppContext();
        appContext.setYaml(yaml);
        appContext.setTokenRegistry(tokenRegistry);
        appContext.setApplicationProperties(applicationProperties);

        startServer(config.getPort(), appContext);
    }

    private void startServer(Integer port, AppContext appContext) {
        //API
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/api");

        //        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/*");
        //        servletHolder.setInitParameter("jersey.config.server.provider.packages", "com.xesnet.runui.ws");
        //        servletHolder.setInitParameter("javax.ws.rs.Application", "com.xesnet.runui.ws.InjectApplication");

        servletContextHandler.addServlet(new ServletHolder(new ServletContainer(new WsApplication(appContext))), "/*");

        //UI
        WebAppContext mainWebAppContext = new WebAppContext();
        mainWebAppContext.setContextPath("/");
        mainWebAppContext.setResourceBase(Application.class.getResource("/webapp").toString());
        mainWebAppContext.setParentLoaderPriority(true);

        RewriteHandler rewriteHandler = new UIRewriteHandler(mainWebAppContext);
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(false);
        rewriteHandler.setOriginalPathAttribute("requestedPath");
        rewriteHandler.setHandler(mainWebAppContext);

        RewriteRegexRule rewriteRegexRule = new RewriteRegexRule();
        rewriteRegexRule.setRegex(".*");
        rewriteRegexRule.setReplacement("/index.html");
        rewriteHandler.addRule(rewriteRegexRule);

        //Handler List
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { servletContextHandler, rewriteHandler });

        //Server
        Server server = new Server(port);
        server.setHandler(handlers);

        try {
            server.start();

            Optional.of(server.getConnectors())
                    .map(connectors -> connectors[0])
                    .filter(connector -> connector instanceof ServerConnector)
                    .map(connector -> (ServerConnector) connector)
                    .ifPresent(serverConnector -> {
                        LOG.info("Server started: http://" + (serverConnector.getHost() == null ? "0.0.0.0" : serverConnector.getHost()) + ":" + serverConnector.getPort());
                    });

            server.join();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {
            server.destroy();
        }
    }
}
