package org.bank.config;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.bank.model.Customer;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    public static final String SESSION_USER_PROPERTY = "sessionUser"; // Key to store user info

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        if (path.startsWith("auth/login") || path.equals("auth/signup")) {
            return;
        }

        // Get the sessionId cookie
        Cookie sessionCookie = requestContext.getCookies().get("sessionId");
        if (sessionCookie == null) {
            abort(requestContext);
            return;
        }

        String sessionId = sessionCookie.getValue();
        Customer customer = SessionManager.getCustomer(sessionId);

        // Validate session
        if (customer == null) {
            abort(requestContext);
            return;
        }

        // Store user info in request context for controllers to use
        requestContext.setProperty(SESSION_USER_PROPERTY, customer);
    }

    private void abort(ContainerRequestContext requestContext) {
        // Expire cookie on unauthorized
        NewCookie expiredCookie = new NewCookie(
                "sessionId", "", "/", null, null, 0, false, true
        );

        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .cookie(expiredCookie)
                        .entity("{\"error\": \"Unauthorized: Please log in.\"}")
                        .build()
        );
    }
}

