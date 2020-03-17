package com.example.addressbook.model;

import lombok.Value;

public @Value class PhoneNumber {
    String value;

    public static PhoneNumber phoneNumber(String value) { return new PhoneNumber(value); }
}
