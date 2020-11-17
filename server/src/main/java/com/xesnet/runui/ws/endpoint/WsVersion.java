package com.xesnet.runui.ws.endpoint;

import com.xesnet.runui.server.Secured;
import com.xesnet.runui.context.AppContext;
import com.xesnet.runui.model.Version;
import com.xesnet.runui.ApplicationProperties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


/**
 * @author Pierre PINON
 */
@Secured
@Path("version")
public class WsVersion {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Version getVersion(@Context AppContext appContext) {
        ApplicationProperties applicationProperties = appContext.getApplicationProperties();

        Version version = new Version();
        version.setVersion(applicationProperties.getProperty(ApplicationProperties.PROPERTY_APPLICATION_VERSION));
        version.setVersionTimestamp(applicationProperties.getProperty(ApplicationProperties.PROPERTY_APPLICATION_VERSIONTIMESTAMP));

        return version;
    }
}
