package org.superheroes.hero;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class SuperHero {
    private String name;
    private String primaryLocation;
    private List<String> superPowers;
}
