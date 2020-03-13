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
public class Address {
    private String street;
    private String houseNumber;
    private String zipCode;
    private String city;
}
