package com.xesnet.runui.context;

import com.xesnet.runui.ApplicationProperties;
import com.xesnet.runui.RunExecutor;
import com.xesnet.runui.model.Config;
import com.xesnet.runui.registry.TokenRegistry;
import com.xesnet.runui.yaml.Yaml;


public class AppContext {

    private Config config;
    private Yaml yaml;
    private TokenRegistry tokenRegistry;
    private ApplicationProperties applicationProperties;
    private RunExecutor runExecutor;

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

    public RunExecutor getRunExecutor() {
        return runExecutor;
    }

    public void setRunExecutor(RunExecutor runExecutor) {
        this.runExecutor = runExecutor;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}
