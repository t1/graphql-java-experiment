package com.example.boundary;

import com.example.model.Address;
import com.example.model.EmailAddress;
import com.example.model.Person;
import com.example.model.PhoneNumber;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Path("/")
@GraphQLApi
public class Boundary {
    private static final Person JANE = new Person()
        .withId("1")
        .withFirstName("Jane")
        .withLastName("Doe")
        .withAge(73)
        .withAddresses(asList(new Address()
                .withHouseNumber("12345")
                .withStreet("Main Street")
                .withZipCode("55555")
                .withCity("Demo City")
            , new Address()
                .withHouseNumber("6789")
                .withStreet("Side Street")
                .withZipCode("66666")
                .withCity("Doe Town")
        ))
        .withEmails(asList(
            new EmailAddress("jane.doe@example.com"),
            new EmailAddress("jdoe@example.com")
        ))
        .withPhoneNumbers(asList(
            new PhoneNumber("+49721123456"),
            new PhoneNumber("+49721888000")
        ));
    private static final Person JOE = new Person()
        .withId("2")
        .withFirstName("Joe")
        .withLastName("Doe")
        .withAge(59)
        .withAddresses(singletonList(new Address()
            .withHouseNumber("12345")
            .withStreet("Main Street")
            .withZipCode("55555")
            .withCity("Demo City")
        ))
        .withEmails(singletonList(new EmailAddress("joe.doe@example.com")))
        .withPhoneNumbers(singletonList(
            new PhoneNumber("+49721123456")
        ));

    private static final Map<String, Person> REPOSITORY = Map.of(JANE.getId(), JANE, JOE.getId(), JOE);

    public @GET @Query Person person(String id) {
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

    // @SuppressWarnings("unused")
    // public List<Score> scores(@Source Person person) {
    //     return asList(
    //         new Score().withId("111").withName("Driving").withScore(98),
    //         new Score().withId("222").withName("Math").withScore(76)
    //     );
    // }

    @Path("/graphiql.html") @Produces(TEXT_HTML)
    public @GET InputStream graphiql() {
        return Boundary.class.getResourceAsStream("/graphiql.html");
    }
}
