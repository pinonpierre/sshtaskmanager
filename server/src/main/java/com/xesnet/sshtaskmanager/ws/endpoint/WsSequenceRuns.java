package com.xesnet.sshtaskmanager.ws.endpoint;

import com.xesnet.sshtaskmanager.context.AppContext;
import com.xesnet.sshtaskmanager.model.Sequence;
import com.xesnet.sshtaskmanager.model.SequenceRun;
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


@Path("sequenceRuns")
@Secured
public class WsSequenceRuns {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SequenceRun postRun(@Context AppContext appContext, @Context ContainerRequestContext containerRequestContext, SequenceRun sequenceRun) throws YamlContext.YamlContextException {
        TokenRegistry.TokenInfo tokenInfo = (TokenRegistry.TokenInfo) containerRequestContext.getProperty(TokenFilter.REQUEST_PROPERTY_TOKEN_INFO);

        Sequence sequence = appContext.getYaml().readSequences().getSequences().stream()
                .filter(s -> s.getName().equals(sequenceRun.getName()))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        return appContext.getRunManager().execute(sequence, tokenInfo.getLogin());
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SequenceRun getRun(@Context AppContext appContext, @PathParam("id") String id) {
        SequenceRun sequenceRun = appContext.getRunManager().getSequenceRun(id);

        if (sequenceRun == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return sequenceRun;
    }
}
