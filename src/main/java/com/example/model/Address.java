package com.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Type;

@Type
@Builder
@Getter @Setter
public class Address {
    private @Id String id;
    private String street;
    private String houseNumber;
    private String zipCode;
    private String city;
}
