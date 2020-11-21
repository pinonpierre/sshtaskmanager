package com.xesnet.sshtaskmanager.context;

import com.xesnet.sshtaskmanager.ApplicationProperties;
import com.xesnet.sshtaskmanager.RunManager;
import com.xesnet.sshtaskmanager.model.Config;
import com.xesnet.sshtaskmanager.registry.TokenRegistry;
import com.xesnet.sshtaskmanager.yaml.Yaml;


public class AppContext {

    private Config config;
    private Yaml yaml;
    private TokenRegistry tokenRegistry;
    private ApplicationProperties applicationProperties;
    private RunManager runManager;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Yaml getYaml() {
        return yaml;
    }

    public void setYaml(Yaml yaml) {
        this.yaml = yaml;
    }

    public TokenRegistry getTokenRegistry() {
        return tokenRegistry;
    }

    public void setTokenRegistry(TokenRegistry tokenRegistry) {
        this.tokenRegistry = tokenRegistry;
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public RunManager getRunManager() {
        return runManager;
    }

    public void setRunManager(RunManager runManager) {
        this.runManager = runManager;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}
