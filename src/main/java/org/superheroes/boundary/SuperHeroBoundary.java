package org.superheroes.boundary;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.superheroes.model.SuperHero;
import org.superheroes.model.Team;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

import static org.superheroes.utils.Utils.single;

@GraphQLApi
@Path("/superheroes")
public class SuperHeroBoundary {
    @Inject SuperHeroRepository repository;

    private static boolean ALL(Object o) { return true; }

    @GET @Query public List<SuperHero> allHeroes() {
        return repository.with(SuperHeroBoundary::ALL);
    }

    @Query public List<SuperHero> allHeroesIn(String location) {
        return repository.with(hero -> location.equals(hero.getPrimaryLocation()));
    }

    @Query public List<SuperHero> allHeroesWithPower(String power) {
        return repository.with(hero -> hero.getSuperPowers().contains(power));
    }

    @Query public SuperHero findHeroByName(String name) {
        List<SuperHero> heroes = repository.with(hero -> hero.getName().equals(name));
        return single(heroes, "hero named " + name);
    }

    @Query public Team getTeam(String name) {
        List<Team> teams = repository.teamsWith(team -> team.getName().equals(name));
        return single(teams, "team named " + name);
    }

    @Query public List<Team> allTeams() {
        return repository.teamsWith(SuperHeroBoundary::ALL);
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
