package com.temples.in.query_interface.resources;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.web.filter.RequestContextFilter;

public class ApplicationResourceConfig extends ResourceConfig {

    /**
     * Register JAX-RS application components.
     */
    public ApplicationResourceConfig() {
        register(RequestContextFilter.class);
        register(TempleResource.class);
    }
}
