package com.xesnet.sshtaskmanager;

import com.xesnet.sshtaskmanager.context.AppContext;
import com.xesnet.sshtaskmanager.model.Config;
import com.xesnet.sshtaskmanager.registry.TokenRegistry;
import com.xesnet.sshtaskmanager.server.UIRewriteHandler;
import com.xesnet.sshtaskmanager.server.WsApplication;
import com.xesnet.sshtaskmanager.yaml.YamlDao;
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
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.xesnet.sshtaskmanager.util.PathUtil.getJarDir;


public class Application {

    private final static Logger LOG = Logger.getLogger(Application.class.getName());

    private static final String CONFIG_DIRECTORY = "config";
    private static final String WEBAPP_DIRECTORY = "/webapp";

    //TODO: Filtered SSH return
    //TODO: Command template (docker management, machine status)
    //TODO: Buttons: Simple Run or sequence
    //TODO: Button: Progression based on the critical path (Previous level numbers and max remaining level numbers)
    //TODO: Buttons: States based on exec or on cmd?
    //TODO: Publish Docker Image: Tag version instead of just "latest"
    //TODO: Improve Documentation about config files
    //TODO: Condition Else
    //TODO: Process/Sequence variables

    public static void main(String[] args) {
        //Init Log
        System.setProperty("org.eclipse.jetty.util.log.announce", "false");
        System.setProperty("org.eclipse.jetty.LEVEL", "WARN");

        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s%6$s%n");

        String path = args.length > 0 ? args[0] : getJarDir(Application.class) + File.separator + CONFIG_DIRECTORY;

        LOG.info("[APP] Application start...");

        try {
            new Application(path);
        } catch (YamlContext.YamlContextException | ApplicationProperties.ApplicationPropertiesException e) {
            LOG.log(Level.SEVERE, "[APP] Initialization failed", e);
        }
    }

    private Application(String path) throws YamlContext.YamlContextException, ApplicationProperties.ApplicationPropertiesException {
        //Init YamlDao
        YamlDao yamlDao = new YamlDao(Paths.get(path));
        yamlDao.init();

        //Init Backend
        Backend backend = new Backend(yamlDao);

        //Load Configuration
        Config config = yamlDao.readConfig();

        //Init Log
        Level level = Level.parse(config.getLogLevel());
        Logger rootLogger = Logger.getLogger(Application.class.getPackageName());
        rootLogger.setLevel(level);

        Optional.of(Logger.getLogger("").getHandlers())
                .map(handlers -> handlers[0])
                .filter(handler -> handler instanceof ConsoleHandler)
                .map(ConsoleHandler.class::cast)
                .ifPresent(consoleHandler -> consoleHandler.setLevel(level));

        LOG.fine("[APP] Log Level: " + level.getName());

        //Init TokenRegistry
        TokenRegistry tokenRegistry = new TokenRegistry(config.getTokenTimeout());

        //Init Run Manager
        RunManager runManager = new RunManager(config.getRunManager(), backend);
        runManager.init();

        //Init Application Properties
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.init();

        //AppContext
        AppContext appContext = new AppContext();
        appContext.setBackend(backend);
        appContext.setTokenRegistry(tokenRegistry);
        appContext.setRunManager(runManager);
        appContext.setApplicationProperties(applicationProperties);

        //Start Server
        startServer(config.getHost(), config.getPort(), appContext);
    }

    private void startServer(String host, Integer port, AppContext appContext) {
        //API
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/api");

        servletContextHandler.addServlet(new ServletHolder(new ServletContainer(new WsApplication(appContext))), "/*");

        //UI
        RewriteHandler rewriteHandler = null;
        URL webAppUrl = Application.class.getResource(WEBAPP_DIRECTORY);
        if (webAppUrl != null) {
            WebAppContext mainWebAppContext = new WebAppContext();
            mainWebAppContext.setContextPath("/");
            mainWebAppContext.setResourceBase(webAppUrl.toString());
            mainWebAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

            mainWebAppContext.setParentLoaderPriority(true);

            rewriteHandler = new UIRewriteHandler(mainWebAppContext);
            rewriteHandler.setRewriteRequestURI(true);
            rewriteHandler.setRewritePathInfo(false);
            rewriteHandler.setOriginalPathAttribute("requestedPath");
            rewriteHandler.setHandler(mainWebAppContext);

            RewriteRegexRule rewriteRegexRule = new RewriteRegexRule();
            rewriteRegexRule.setRegex(".*");
            rewriteRegexRule.setReplacement("/index.html");
            rewriteHandler.addRule(rewriteRegexRule);
        }

        //Handlers
        Handler[] handlers = Stream.of(servletContextHandler, rewriteHandler)
                .filter(Objects::nonNull)
                .toArray(Handler[]::new);

        //Server
        Server server = new Server(port);
        server.setHandler(new HandlerList(handlers));

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
                    LOG.info("[APP] Server started: http://" + (serverConnector.getHost() == null ? "0.0.0.0" : serverConnector.getHost()) + ":" + serverConnector.getPort())
            );

            server.join();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[APP] Sever start failed", e);
        } finally {
            server.destroy();
        }
    }
}
