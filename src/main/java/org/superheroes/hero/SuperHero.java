package org.superheroes.hero;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.graphql.Description;

import java.util.List;

@Getter @Setter @ToString
public class SuperHero {
    @Description("the publicly known and widely used pseudonym of the hero")
    private String name;
    @Description("where the hero is mainly 'at home'")
    private String primaryLocation;
    @Description("the super power that the hero can wield")
    private List<String> superPowers;
}
