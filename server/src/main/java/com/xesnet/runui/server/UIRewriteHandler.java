package com.xesnet.runui.server;

import org.eclipse.jetty.http.pathmap.MappedResource;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author Pierre PINON
 */
public class UIRewriteHandler extends RewriteHandler {

    private final WebAppContext webAppContext;

    public UIRewriteHandler(WebAppContext webAppContext) {
        this.webAppContext = webAppContext;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (isStarted()) {
            final MappedResource<ServletHolder> mappedServlet = webAppContext.getServletHandler().getMappedServlet(target);

            boolean fileExists = (mappedServlet.getResource().getServlet().getServletConfig().getServletContext().getResource(target) != null);
            boolean isDefaultServlet = mappedServlet.getResource().getName().equals("default");

            //Do not interfere with urls that either :
            //1) Map to an existing file or resource
            //2) Fall under other servlet scopes (only default servlet is targeted by this rewrite handler)
            if (isDefaultServlet && !fileExists) {
                // Apply rewrite rules
                super.handle(target, baseRequest, request, response);
            } else {
                //Pass along unchanged
                if (!baseRequest.isHandled()) {
                    Handler handler = _handler;
                    if (handler != null) {
                        handler.handle(target, baseRequest, request, response);
                    }
                }
            }
        }
    }
}
