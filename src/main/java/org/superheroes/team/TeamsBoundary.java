package org.superheroes.team;

import com.github.t1.graphql.client.api.GraphQlClientBuilder;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;
import org.superheroes.hero.SuperHero;
import org.superheroes.repository.Repository;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.superheroes.config.CollectionUtils.single;

@GraphQLApi
public class TeamsBoundary {
    @Inject Repository repository;

    public interface SuperHeroesApi {
        List<SuperHero> allHeroes();
    }

    private SuperHeroesApi superHeroesApi = GraphQlClientBuilder.newBuilder()
        .endpoint("http://localhost:8080/graphql-java-experiment/graphql")
        .build(SuperHeroesApi.class);

    @Query public Team getTeam(String name) {
        List<Team> teams = repository.teamsWith(team -> team.getName().equals(name));
        return single(teams, "team named " + name);
    }

    @Query public List<Team> allTeams() {
        return repository.allTeams().collect(toList());
    }

    @SuppressWarnings("unused")
    public int size(@Source Team team) {
        return members(team).size();
    }

    @SuppressWarnings("unused")
    public List<SuperHero> members(@Source Team team) {
        List<SuperHero> allHeroes = superHeroesApi.allHeroes();
        return allHeroes.stream()
            .filter(hero -> teamAffiliationNames(hero).contains(team.getName()))
            .collect(toList());
    }

    private List<String> teamAffiliationNames(SuperHero hero) {
        return teamAffiliations(hero).stream().map(Team::getName).collect(toList());
    }

    @SuppressWarnings("unused")
    public List<Team> teamAffiliations(@Source SuperHero superHero) {
        return repository.getTeamAffiliations(superHero.getName());
    }
}
