package com.example.boundary;

import com.example.model.Address;
import com.example.model.Person;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Path("/")
@GraphQLApi
public class Boundary {
    public @GET @Query Person person() {
        return new Person()
            .withFirstName("Jane")
            .withLastName("Doe")
            .withAge(73)
            .withAddress(new Address()
                .withHouseNumber("12345")
                .withStreet("Main Street")
                .withZipCode("55555")
                .withCity("Demo City")
            );
    }

    @Path("/graphiql.html") @Produces(TEXT_HTML)
    public @GET InputStream graphiql() {
        return Boundary.class.getResourceAsStream("/graphiql.html");
    }
}
