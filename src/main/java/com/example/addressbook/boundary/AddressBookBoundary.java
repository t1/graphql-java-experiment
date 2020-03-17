package com.example.addressbook.boundary;

import com.example.addressbook.model.Address;
import com.example.addressbook.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;
import java.util.Map;

import static com.example.addressbook.model.EmailAddress.email;
import static com.example.addressbook.model.PhoneNumber.phoneNumber;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Slf4j
@Path("/")
@GraphQLApi
public class AddressBookBoundary {
    private static final Person JANE = Person.builder()
        .id("1")
        .firstName("Jane")
        .lastName("Doe")
        .age(73)
        .address(Address.builder()
            .id("101")
            .houseNumber("12345")
            .street("Main Street")
            .zipCode("55555")
            .city("Demo City")
            .build())
        .address(Address.builder()
            .id("102")
            .houseNumber("6789")
            .street("Side Street")
            .zipCode("66666")
            .city("Doe Town")
            .build())
        .email(email("jane.doe@example.com"))
        .email(email("jdoe@example.com"))
        .phoneNumber(phoneNumber("+49721123456"))
        .phoneNumber(phoneNumber("+49721888000"))
        .build();
    private static final Person JOE = Person.builder()
        .id("2")
        .firstName("Joe")
        .lastName("Doe")
        .age(59)
        .address(Address.builder()
            .id("101")
            .houseNumber("12345")
            .street("Main Street")
            .zipCode("55555")
            .city("Demo City")
            .build())
        .email(email("joe.doe@example.com"))
        .phoneNumber(phoneNumber("+49721123456"))
        .build();

    public static final Map<String, Person> REPOSITORY = Map.of(JANE.getId(), JANE, JOE.getId(), JOE);

    public @GET @Query Person person(@DefaultValue String id) {
        log.info("person({})", id);
        if (id == null || id.isEmpty())
            id = JANE.getId();
        Person person = REPOSITORY.get(id);
        if (person == null)
            throw new NotFoundException("no person with id " + id + " found");
        return person;
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
