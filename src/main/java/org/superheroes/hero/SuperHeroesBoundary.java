package org.superheroes.hero;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.GraphQLException;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;
import org.superheroes.config.Resolver;
import org.superheroes.repository.Repository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.superheroes.config.CollectionUtils.single;

@Slf4j
@Stateless
@GraphQLApi
public class SuperHeroesBoundary {
    @Inject Repository repository;

    @Description("returns all heroes")
    @Query public List<SuperHero> heroes() {
        return repository.superHeroes().collect(toList());
    }

    @Query public List<SuperHero> heroesIn(@Valid @Size(min = 3, max = 256) String location) {
        return repository.superHeroesWith(hero -> location.equals(hero.getPrimaryLocation()));
    }

    @Query public List<SuperHero> heroesWithPower(String power) {
        return repository.superHeroesWith(hero -> hero.getSuperPowers().contains(power));
    }

    @Query public SuperHero findHeroByName(@Valid @SuperHeroName String name) {
        List<SuperHero> heroes = repository.superHeroesWith(hero -> hero.getName().equals(name));
        return single(heroes, "hero named " + name);
    }

    @Resolver public String realName(@Source SuperHero superHero) {
        return repository.realNameOf(superHero.getName());
    }

    @Resolver public String currentLocation(@Source SuperHero hero) throws GraphQLException {
        if (hero.getSuperPowers().contains("Location-Blocking")) {
            throw new GraphQLException("Unable to determine location for " + hero.getName());
        }
        return repository.getCurrentLocationOfHero(hero);
    }

    @Mutation public SuperHero createNewHero(@Valid @ConvertGroup(to = SupersDefault.class) SuperHero newHero) {
        repository.addHero(newHero);
        return findHeroByName(newHero.getName());
    }

    private interface SupersDefault extends Supers, Default {}

    @Mutation public SuperHero removeHeroByName(@Valid @SuperHeroName String name) {
        return repository.removeHeroByName(name);
    }

    // @Mutation public Team addHeroToTeam(String heroName, String teamName) {
    //     return repository.getTeam(teamName)
    //         .addMembers(repository.getHero(heroName));
    // }

    // @Mutation public Team removeHeroFromTeam(String heroName, String teamName) {
    //     return repository.getTeam(teamName)
    //         .removeMembers(repository.getHero(heroName));
    // }
}
