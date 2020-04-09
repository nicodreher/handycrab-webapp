package de.dhbw.handycrab.server.rest.cors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        String host = request.getHeader("Host");
        if(host != null && (host.equalsIgnoreCase("localhost") || host.equalsIgnoreCase("handycrab.nico-dreher.de") || host.equalsIgnoreCase("127.0.0.1") || host.equalsIgnoreCase("::1"))) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://" + host);
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

            if(requestContext.getMethod().equals("OPTIONS")) {
                responseContext.setStatus(200);
            }
        }
    }
}
