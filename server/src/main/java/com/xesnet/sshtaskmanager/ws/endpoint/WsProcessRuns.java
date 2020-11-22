package com.xesnet.sshtaskmanager.ws.endpoint;

import com.xesnet.sshtaskmanager.context.AppContext;
import com.xesnet.sshtaskmanager.model.Process;
import com.xesnet.sshtaskmanager.model.ProcessRun;
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


@Path("processRuns")
@Secured
public class WsProcessRuns {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ProcessRun postRun(@Context AppContext appContext, @Context ContainerRequestContext containerRequestContext, ProcessRun processRun) throws YamlContext.YamlContextException {
        TokenRegistry.TokenInfo tokenInfo = (TokenRegistry.TokenInfo) containerRequestContext.getProperty(TokenFilter.REQUEST_PROPERTY_TOKEN_INFO);

        Process process = appContext.getYaml().readProcesses().getProcesses().stream()
                .filter(p -> p.getName().equals(processRun.getName()))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        return appContext.getRunManager().execute(process, tokenInfo.getLogin()).getProcessRun();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProcessRun getRun(@Context AppContext appContext, @PathParam("id") String id) {
        ProcessRun processRun = appContext.getRunManager().getProcessRun(id);

        if (processRun == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return processRun;
    }
}
