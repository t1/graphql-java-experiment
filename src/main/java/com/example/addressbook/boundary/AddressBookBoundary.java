package com.example.addressbook.boundary;

import com.example.addressbook.model.AddressBook;
import com.example.addressbook.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;
import java.util.List;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Slf4j
@Path("/")
@GraphQLApi
public class AddressBookBoundary {

    @Inject AddressBook addressBook;

    public @Query List<Person> persons() {
        return addressBook.getAllPersons();
    }

    public @GET @Query Person person(String id) {
        log.info("person({})", id);
        return addressBook.getPersonById(id);
    }

    @Mutation
    public Person birthday(Person diff) {
        Person person = person(diff.getId());
        person.setAge(diff.getAge());
        return person;
    }

    @Path("/graphiql.html") @Produces(TEXT_HTML)
    public @GET InputStream graphiql() {
        return AddressBookBoundary.class.getResourceAsStream("/graphiql.html");
    }
}
