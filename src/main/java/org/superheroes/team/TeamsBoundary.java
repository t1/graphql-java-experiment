package org.superheroes.team;

import io.smallrye.graphql.client.api.GraphQlClientApi;
import io.smallrye.graphql.client.api.GraphQlClientHeader;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;
import org.superheroes.hero.SuperHero;
import org.superheroes.repository.Repository;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.superheroes.config.CollectionUtils.single;

@Stateless
@GraphQLApi
public class TeamsBoundary {
    @Inject Repository repository;

    @Produces @RequestScoped public GraphQlClientHeader authorization() {
        return new GraphQlClientHeader("S.H.I.E.L.D.-Clearance", "TOP-SECRET");
    }

    @GraphQlClientApi
    public interface SuperHeroesApi {
        List<NamedHero> allHeroes();
    }

    @Getter @Setter @ToString public static class NamedHero {
        private String name;
        private List<String> superPowers;
    }

    @Inject SuperHeroesApi superHeroesApi;

    @Query public Team getTeam(String name) {
        List<Team> teams = repository.teamsWith(team -> team.getName().equals(name));
        return single(teams, "team named " + name);
    }

    @Query public List<Team> allTeams() {
        return repository.allTeams().collect(toList());
    }

    @Query public int size(@Source Team team) {
        return members(team).size();
    }

    @Query public List<NamedHero> members(@Source Team team) {
        List<NamedHero> allHeroes = superHeroesApi.allHeroes();
        return allHeroes.stream()
            .filter(hero -> teamAffiliationNames(hero).contains(team.getName()))
            .collect(toList());
    }

    private List<String> teamAffiliationNames(NamedHero hero) {
        return repository.getTeamAffiliations(hero.getName()).stream().map(Team::getName).collect(toList());
    }

    @Query public List<Team> teamAffiliations(@Source SuperHero superHero) {
        return repository.getTeamAffiliations(superHero.getName());
    }
}
