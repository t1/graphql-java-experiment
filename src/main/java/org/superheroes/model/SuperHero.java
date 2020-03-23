package org.superheroes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class SuperHero {
    private String name;
    private String realName;
    private String primaryLocation;
    private List<String> superPowers;
    private List<Team> teamAffiliations;
}
