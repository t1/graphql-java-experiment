package org.superheroes.config;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Path("/")
public class GraphiQlBoundary {
    @Path("/graphiql.html") @Produces(TEXT_HTML)
    public @GET InputStream graphiql() {
        return Config.class.getResourceAsStream("/graphiql.html");
    }
}
