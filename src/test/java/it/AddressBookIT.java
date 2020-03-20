package it;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import static it.GraphQlClient.graphQlClient;
import static org.assertj.core.api.BDDAssertions.then;

public class AddressBookIT {

    public interface PersonApi {
        Person person(String id);
    }

    @Getter @Setter @ToString
    public static class Person {
        String firstName;
        String lastName;
    }

    @Test void shouldGetJane() {
        Person person = api.person("1");

        then(person.firstName).isEqualTo("Jane");
        then(person.lastName).isEqualTo("Doe");
    }

    private final PersonApi api = graphQlClient(PersonApi.class);
}
