package org.bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Scan both controllers and exception handler
        packages("org.bank.controller", "org.bank.exception");

        // Register Jackson for JSON + Java 8 Date/Time support
        register(JacksonFeature.class);
        register(new ObjectMapper().registerModule(new JavaTimeModule()));
    }
}