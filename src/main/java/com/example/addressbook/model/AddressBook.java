package com.example.addressbook.model;

import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.addressbook.model.EmailAddress.email;
import static com.example.addressbook.model.PhoneNumber.phoneNumber;

@Singleton
public class AddressBook {
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

    private static final Map<String, Person> REPOSITORY = Map.of(JANE.getId(), JANE, JOE.getId(), JOE);

    public Person getPersonById(String id) {
        return findPersonById(id)
            .orElseThrow(() -> new NotFoundException("no person with id " + id + " found"));
    }

    public Optional<Person> findPersonById(String id) {
        return Optional.ofNullable(REPOSITORY.get(id));
    }

    public List<Person> getAllPersons() {
        return new ArrayList<>(REPOSITORY.values());
    }
}
