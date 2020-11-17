package com.xesnet.runui.ws.endpoint;

import com.xesnet.runui.registry.TokenRegistry;
import com.xesnet.runui.context.AppContext;
import com.xesnet.runui.model.Token;
import com.xesnet.runui.model.User;
import com.xesnet.runui.yaml.YamlContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("token")
public class WsToken {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Token login(@Context AppContext appContext, @Context HttpServletRequest request, Token token) throws YamlContext.YamlContextException {
        List<User> users = appContext.getYaml().readUsers();

        User user = users.stream().filter(u -> u.getLogin().equals(token.getLogin()) && u.getPassword().equals(token.getPassword())).findFirst().orElse(null);
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } else {
            TokenRegistry.TokenInfo tokenInfo = appContext.getTokenRegistry().login(token.getLogin());

            Token responseToken = new Token();
            responseToken.setLogin(token.getLogin());
            responseToken.setToken(tokenInfo.getToken());

            return responseToken;
        }
    }
}
