package com.xesnet.sshtaskmanager.ws.endpoint;

import com.xesnet.sshtaskmanager.server.Secured;
import com.xesnet.sshtaskmanager.context.AppContext;
import com.xesnet.sshtaskmanager.model.Version;
import com.xesnet.sshtaskmanager.ApplicationProperties;

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
