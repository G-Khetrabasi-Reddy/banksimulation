package org.bank.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173"); //React dev server
        // --- -------------------------- ---

        // Specifies the allowed HTTP headers for requests
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");

        // Allows the browser to include credentials (like cookies) in the request
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // Specifies the allowed HTTP methods
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // The OPTIONS method is a preflight request sent by browsers to check CORS settings.
        // We can return an OK status immediately for these requests.
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            responseContext.setStatus(200);
        }
    }
}