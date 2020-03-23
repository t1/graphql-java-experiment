package org.superheroes.hero;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.superheroes.repository.Repository;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.superheroes.config.CollectionUtils.single;

@GraphQLApi
public class SuperHeroesBoundary {
    @Inject Repository repository;

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

    // public SuperHero createNewHero(SuperHero newHero) {
    //     repository.addHero(newHero);
    //     return repository.getHero(newHero.getName());
    // }

    // public Team addHeroToTeam(String heroName, String teamName) {
    //     return repository.getTeam(teamName)
    //         .addMembers(repository.getHero(heroName));
    // }

    // public Team removeHeroFromTeam(String heroName, String teamName) {
    //     return repository.getTeam(teamName)
    //         .removeMembers(repository.getHero(heroName));
    // }
}
