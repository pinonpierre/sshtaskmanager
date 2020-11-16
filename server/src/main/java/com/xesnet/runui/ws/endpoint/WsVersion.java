package com.xesnet.runui.ws.endpoint;

import com.xesnet.runui.Secured;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * @author Pierre PINON
 */
@Secured
@Path("version")
public class WsVersion {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getVersion() {
        return "1.0.0";
    }
}
