package com.xesnet.runui.ws.endpoint;

import com.xesnet.runui.context.AppContext;
import com.xesnet.runui.model.Run;
import com.xesnet.runui.model.SshRun;
import com.xesnet.runui.model.SshServer;
import com.xesnet.runui.server.Secured;
import com.xesnet.runui.yaml.YamlContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("runs")
@Secured
public class WsRuns {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Run postRun(@Context AppContext appContext, Run run) throws YamlContext.YamlContextException {
        SshRun sshRun = appContext.getYaml().readRuns().getRuns().stream()
                .filter(sr -> sr.getName().equals(run.getRunName()))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        SshServer sshServer = appContext.getYaml().readServers().getServers().stream()
                .filter(ss -> ss.getName().equals(sshRun.getServer()))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        return appContext.getRunExecutor().execute(sshRun, sshServer);
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
