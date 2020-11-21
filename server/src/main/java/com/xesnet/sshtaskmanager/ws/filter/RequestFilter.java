package com.xesnet.sshtaskmanager.ws.filter;

import com.xesnet.sshtaskmanager.registry.TokenRegistry;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.text.MessageFormat;
import java.util.logging.Logger;


/**
 * @author Pierre PINON
 */
public class RequestFilter implements ContainerRequestFilter {

    private final Logger LOG = Logger.getLogger(RequestFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        TokenRegistry.TokenInfo tokenInfo = (TokenRegistry.TokenInfo) requestContext.getProperty(TokenFilter.REQUEST_PROPERTY_TOKEN_INFO);

        LOG.finer(MessageFormat.format("[WS]{0} #{1} {2}", tokenInfo == null ? "" : " [" + tokenInfo.getLogin() + "]", requestContext.getMethod(), requestContext.getUriInfo().getPath()));
    }
}
