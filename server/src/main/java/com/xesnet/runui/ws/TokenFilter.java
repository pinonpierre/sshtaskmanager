package com.xesnet.runui.ws;

import com.xesnet.runui.Secured;
import com.xesnet.runui.TokenRegistry;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;


/**
 * @author Pierre PINON
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class TokenFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Token";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        //Check If authorizationHeader is present and valid
        if (authorizationHeader == null || !authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
            abortWithUnauthorized(requestContext);
            return;
        }

        //Check if token is valid
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        TokenRegistry.TokenInfo tokenInfo = TokenRegistry.getInstance().getTokenInfo(token);

        if (tokenInfo == null) {
            abortWithUnauthorized(requestContext);
            return;
        }

        //Replace the SecurityContext
        SecurityContext currentSecurityContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
                return tokenInfo::getLogin;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true;
            }

            @Override
            public boolean isSecure() {
                return currentSecurityContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return AUTHENTICATION_SCHEME;
            }
        });
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME).build());
    }
}
