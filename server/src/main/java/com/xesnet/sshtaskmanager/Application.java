package com.xesnet.sshtaskmanager;

import com.xesnet.sshtaskmanager.context.AppContext;
import com.xesnet.sshtaskmanager.model.Config;
import com.xesnet.sshtaskmanager.registry.TokenRegistry;
import com.xesnet.sshtaskmanager.server.UIRewriteHandler;
import com.xesnet.sshtaskmanager.server.WsApplication;
import com.xesnet.sshtaskmanager.yaml.Yaml;
import com.xesnet.sshtaskmanager.yaml.YamlContext;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
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

import static com.xesnet.sshtaskmanager.util.PathUtil.getJarDir;


public class Application {

    private static Logger LOG;

    private static final String CONFIG_DIRECTORY = "config";
    private static final String WEBAPP_DIRECTORY = "/webapp";

    //TODO: SSH Auth by Public Key
    //TODO: Filtered SSH return
    //TODO: Allows to make a sequence of runs based on return code / output

    public static void main(String[] args) {
        //Init Log
        System.setProperty("org.eclipse.jetty.util.log.announce", "false");
        System.setProperty("org.eclipse.jetty.LEVEL", "WARN");

        System.setProperty("java.util.logging.ConsoleHandler.level", "INFO");
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s%6$s%n");
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

        //Allows to create file if not exists
        yaml.readUsers();

        Config config = yaml.readConfig();

        TokenRegistry tokenRegistry = new TokenRegistry(config.getTokenTimeout());

        RunExecutor commandExecutor = new RunExecutor(config.getNumberOfThreads(), config.getOutputPollInterval(), config.getRunCleanInterval(), config.getRunRetention());
        commandExecutor.init();

        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.init();

        AppContext appContext = new AppContext();
        appContext.setConfig(config);
        appContext.setYaml(yaml);
        appContext.setTokenRegistry(tokenRegistry);
        appContext.setRunExecutor(commandExecutor);
        appContext.setApplicationProperties(applicationProperties);

        startServer(config.getHost(), config.getPort(), appContext);
    }

    private void startServer(String host, Integer port, AppContext appContext) {
        //API
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/api");

        servletContextHandler.addServlet(new ServletHolder(new ServletContainer(new WsApplication(appContext))), "/*");

        //UI
        WebAppContext mainWebAppContext = new WebAppContext();
        mainWebAppContext.setContextPath("/");
        mainWebAppContext.setResourceBase(Application.class.getResource(WEBAPP_DIRECTORY).toString());
        mainWebAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

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

        //Don't Send Server Version
        Optional.of(server.getConnectors())
                .map(connectors -> connectors[0])
                .map(Connector::getConnectionFactories)
                .map(connectionFactories -> connectionFactories.stream()
                        .filter(connector -> connector instanceof HttpConnectionFactory)
                        .findFirst()
                        .orElse(null)
                )
                .map(connector -> (HttpConnectionFactory) connector)
                .ifPresent(httpConnectionFactory -> httpConnectionFactory.getHttpConfiguration().setSendServerVersion(false));

        Optional<ServerConnector> optionalServerConnector = Optional.of(server.getConnectors())
                .map(connectors -> connectors[0])
                .filter(connector -> connector instanceof ServerConnector)
                .map(connector -> (ServerConnector) connector);

        //Set Host if necessary
        optionalServerConnector.ifPresent(serverConnector -> {
            if (host != null) {
                serverConnector.setHost(host);
            }
        });

        try {
            server.start();

            //Log Server Info
            optionalServerConnector.ifPresent(serverConnector ->
                    LOG.info("Server started: http://" + (serverConnector.getHost() == null ? "0.0.0.0" : serverConnector.getHost()) + ":" + serverConnector.getPort())
            );

            server.join();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {
            server.destroy();
        }
    }
}