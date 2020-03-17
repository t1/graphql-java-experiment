package com.example.registrar.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true) @Getter @Setter
public class Registration {
    String id;
    String domain;
    Contact admin;
}
