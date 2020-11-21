package com.xesnet.sshtaskmanager.ws.endpoint;

import com.xesnet.sshtaskmanager.context.AppContext;
import com.xesnet.sshtaskmanager.model.Token;
import com.xesnet.sshtaskmanager.model.User;
import com.xesnet.sshtaskmanager.model.Users;
import com.xesnet.sshtaskmanager.registry.TokenRegistry;
import com.xesnet.sshtaskmanager.server.Secured;
import com.xesnet.sshtaskmanager.ws.filter.TokenFilter;
import com.xesnet.sshtaskmanager.yaml.YamlContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("tokens")
public class WsTokens {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Token postToken(@Context AppContext appContext, Token token) throws YamlContext.YamlContextException {
        Users users = appContext.getYaml().readUsers();

        User user = users.getUsers().stream().filter(u -> u.getLogin().equals(token.getLogin()) && u.getPassword().equals(token.getPassword())).findFirst().orElse(null);
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } else {
            TokenRegistry.TokenInfo tokenInfo = appContext.getTokenRegistry().login(token.getLogin());

            Token responseToken = new Token();
            responseToken.setLogin(token.getLogin());
            responseToken.setId(tokenInfo.getId());

            return responseToken;
        }
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Token getToken(@Context ContainerRequestContext containerRequestContext) {
        TokenRegistry.TokenInfo tokenInfo = (TokenRegistry.TokenInfo) containerRequestContext.getProperty(TokenFilter.REQUEST_PROPERTY_TOKEN_INFO);

        Token responseToken = new Token();
        responseToken.setLogin(tokenInfo.getLogin());
        responseToken.setId(tokenInfo.getId());

        return responseToken;
    }

    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Token deleteToken(@Context AppContext appContext, @Context HttpServletRequest request, @Context ContainerRequestContext containerRequestContext) {
        TokenRegistry.TokenInfo tokenInfo = (TokenRegistry.TokenInfo) containerRequestContext.getProperty(TokenFilter.REQUEST_PROPERTY_TOKEN_INFO);

        appContext.getTokenRegistry().logout(tokenInfo.getId());

        Token responseToken = new Token();
        responseToken.setLogin(tokenInfo.getLogin());
        responseToken.setId(tokenInfo.getId());

        return responseToken;
    }
}
