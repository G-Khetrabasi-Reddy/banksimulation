package org.bank.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        packages("org.bank"); // Scan your controllers, services, and exceptions
        register(CORSFilter.class);
        register(CustomJacksonProvider.class); // <-- Register the custom provider
    }
}
