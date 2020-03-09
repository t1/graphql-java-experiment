package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class Boundary {
    public @GET Person get() {
        return new Person("Jane", "Doe");
    }
}
