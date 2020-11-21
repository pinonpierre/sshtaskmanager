package com.xesnet.sshtaskmanager.ws.endpoint;

import com.xesnet.sshtaskmanager.context.AppContext;
import com.xesnet.sshtaskmanager.model.Run;
import com.xesnet.sshtaskmanager.model.SshRun;
import com.xesnet.sshtaskmanager.model.SshServer;
import com.xesnet.sshtaskmanager.registry.TokenRegistry;
import com.xesnet.sshtaskmanager.server.Secured;
import com.xesnet.sshtaskmanager.ws.filter.TokenFilter;
import com.xesnet.sshtaskmanager.yaml.YamlContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("runs")
@Secured
public class WsRuns {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Run postRun(@Context AppContext appContext, @Context ContainerRequestContext containerRequestContext, Run run) throws YamlContext.YamlContextException {
        TokenRegistry.TokenInfo tokenInfo = (TokenRegistry.TokenInfo) containerRequestContext.getProperty(TokenFilter.REQUEST_PROPERTY_TOKEN_INFO);

        SshRun sshRun = appContext.getYaml().readRuns().getRuns().stream()
                .filter(sr -> sr.getName().equals(run.getName()))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        SshServer sshServer = appContext.getYaml().readServers().getServers().stream()
                .filter(ss -> ss.getName().equals(sshRun.getServer()))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        return appContext.getRunExecutor().execute(sshRun, sshServer, tokenInfo.getLogin());
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Run getRun(@Context AppContext appContext, @PathParam("id") String id) {
        Run run = appContext.getRunExecutor().getRun(id);

        if (run == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return run;
    }
}
