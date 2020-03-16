package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Type;

import java.util.List;

@Type
@AllArgsConstructor @NoArgsConstructor
@Builder(toBuilder = true)
@Getter @Setter @With
public class Person {
    private @Id String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private List<Address> addresses;
    private List<EmailAddress> emails;
    private List<PhoneNumber> phoneNumbers;
}
