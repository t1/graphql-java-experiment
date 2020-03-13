package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.eclipse.microprofile.graphql.Type;

@Type
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @With
public class Person {
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private Address address;
}
