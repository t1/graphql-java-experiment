package com.example.addressbook.model;

import lombok.Value;

public @Value class EmailAddress {
    String value;

    public static EmailAddress email(String value) { return new EmailAddress(value); }
}
