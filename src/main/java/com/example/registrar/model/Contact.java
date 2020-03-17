package com.example.registrar.model;

import com.example.addressbook.model.Address;
import com.example.addressbook.model.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static lombok.AccessLevel.PUBLIC;

@Builder @Getter @Setter
@AllArgsConstructor(access = PUBLIC)
public class Contact {
    private String personId;
    private Person person;
    private String addressId;
    private Address address;
    private String email;
    private String phoneNumber;
}
