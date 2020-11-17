package com.xesnet.runui.context;

import com.xesnet.runui.ApplicationProperties;
import com.xesnet.runui.registry.TokenRegistry;
import com.xesnet.runui.yaml.Yaml;


public class AppContext {

    private Yaml yaml;
    private TokenRegistry tokenRegistry;
    private ApplicationProperties applicationProperties;

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

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}
