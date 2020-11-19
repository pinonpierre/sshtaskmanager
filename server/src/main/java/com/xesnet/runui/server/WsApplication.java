package com.xesnet.runui.server;

import com.xesnet.runui.context.AppContext;
import com.xesnet.runui.context.AppContextFactory;
import com.xesnet.runui.ws.filter.ResponseFilter;
import com.xesnet.runui.ws.filter.TokenFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;


public class WsApplication extends ResourceConfig {

    public WsApplication(AppContext appContext) {
        AppContextFactory appContextFactory = new AppContextFactory(appContext);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(appContextFactory)
                        .to(AppContext.class)
                        .in(RequestScoped.class);
            }
        });
        register(new TokenFilter(appContext.getTokenRegistry()));
        register(new ResponseFilter());
        packages(true, "com.xesnet.runui.ws");
    }
}
