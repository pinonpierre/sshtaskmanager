package com.xesnet.sshtaskmanager.context;

import org.glassfish.hk2.api.Factory;


public class AppContextFactory implements Factory<AppContext> {

    private final AppContext appContext;

    public AppContextFactory(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public AppContext provide() {
        return appContext;
    }

    @Override
    public void dispose(AppContext appContext) {
    }
}
