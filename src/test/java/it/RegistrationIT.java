package it;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import static it.GraphQlClient.graphQlClient;
import static org.assertj.core.api.BDDAssertions.then;

public class RegistrationIT {

    public interface RegistrationApi {
        Registration registration(String id);
    }

    @Getter @Setter @ToString
    public static class Registration {
        private String id;
        private String domain;
        private RegistrationContact admin;
    }

    @Getter @Setter @ToString
    public static class RegistrationContact {
        private String email;
        private String phoneNumber;
        private Person person;
        private Address address;
    }

    @Getter @Setter @ToString
    public static class Person {
        private String firstName;
        private String lastName;
    }

    @Getter @Setter @ToString
    public static class Address {
        private String street;
        private String houseNumber;
        private String zipCode;
        private String city;
    }

    @Test void shouldGetJaneOrg() {
        Registration registration = api.registration("10001");

        then(registration.id).isEqualTo("10001");
        then(registration.domain).isEqualTo("jane.org");
        then(registration.admin.email).isEqualTo("jane.doe@example.com");
        then(registration.admin.phoneNumber).isEqualTo("+49721123456");
        then(registration.admin.person.firstName).isEqualTo("Jane");
        then(registration.admin.person.lastName).isEqualTo("Doe");
        then(registration.admin.address.houseNumber).isEqualTo("12345");
        then(registration.admin.address.street).isEqualTo("Main Street");
        then(registration.admin.address.zipCode).isEqualTo("55555");
        then(registration.admin.address.city).isEqualTo("Demo City");
    }

    private final RegistrationApi api = graphQlClient(RegistrationApi.class);
}
