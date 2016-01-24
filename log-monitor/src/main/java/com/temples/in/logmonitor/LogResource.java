package com.temples.in.logmonitor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("logresource")
@Produces(MediaType.APPLICATION_JSON)
public class LogResource {

    @GET
    public String getIt() {
        return "Got it!";
    }
}
