package com.xesnet.sshtaskmanager.context;

import com.xesnet.sshtaskmanager.ApplicationProperties;
import com.xesnet.sshtaskmanager.Backend;
import com.xesnet.sshtaskmanager.RunManager;
import com.xesnet.sshtaskmanager.registry.TokenRegistry;


public class AppContext {

    private Backend backend;
    private TokenRegistry tokenRegistry;
    private ApplicationProperties applicationProperties;
    private RunManager runManager;

    public Backend getBackend() {
        return backend;
    }

    public void setBackend(Backend backend) {
        this.backend = backend;
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
