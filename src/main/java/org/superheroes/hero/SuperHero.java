package org.superheroes.hero;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.graphql.Description;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter @Setter @ToString
public class SuperHero {
    @Description("the publicly known and widely used pseudonym of the hero")
    private @SuperHeroName String name;

    @Description("where the hero is mainly 'at home'")
    private @Pattern(regexp = "\\w+") String primaryLocation;

    @Description("the super power that the hero can wield")
    private /*@NotEmpty(groups = Supers.class)*/ List<String> superPowers;

    private List<@Valid Score> scores;
}
