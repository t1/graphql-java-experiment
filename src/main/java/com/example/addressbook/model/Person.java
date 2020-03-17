package com.example.addressbook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Type;

import java.util.List;

@Type
@AllArgsConstructor @NoArgsConstructor
@Builder(toBuilder = true)
@Getter @Setter
public class Person {
    private @Id String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private @Singular("address") List<Address> addresses;
    private @Singular List<EmailAddress> emails;
    private @Singular List<PhoneNumber> phoneNumbers;
}
