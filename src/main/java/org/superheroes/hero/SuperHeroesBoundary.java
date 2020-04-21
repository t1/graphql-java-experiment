package org.superheroes.hero;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.GraphQLException;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.superheroes.repository.Repository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.superheroes.config.CollectionUtils.single;

@Slf4j
@Stateless
@GraphQLApi
@Timed
public class SuperHeroesBoundary {
    @Inject Repository repository;

    @Description("returns all heroes")
    @Query public List<SuperHero> allHeroes() {
        return repository.allSuperHeroes().collect(toList());
    }

    @Query public List<SuperHero> allHeroesIn(String location) {
        return repository.superHeroesWith(hero -> location.equals(hero.getPrimaryLocation()));
    }

    @Query public List<SuperHero> allHeroesWithPower(String power) {
        return repository.superHeroesWith(hero -> hero.getSuperPowers().contains(power));
    }

    @Query public SuperHero findHeroByName(String name) {
        List<SuperHero> heroes = repository.superHeroesWith(hero -> hero.getName().equals(name));
        return single(heroes, "hero named " + name);
    }

    @Query public String realName(@Source SuperHero superHero) {
        return repository.realNameOf(superHero.getName());
    }

    @Query public String currentLocation(@Source SuperHero hero) throws GraphQLException {
        if (hero.getSuperPowers().contains("Location-Blocking")) {
            throw new GraphQLException("Unable to determine location for " + hero.getName());
        }
        return repository.getCurrentLocationOfHero(hero);
    }

    @Mutation public SuperHero createNewHero(SuperHero newHero) {
        repository.addHero(newHero);
        return findHeroByName(newHero.getName());
    }

    @Mutation public SuperHero removeHeroByName(String name) {
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
