package org.superheroes.team;

import io.smallrye.graphql.client.typesafe.api.GraphQlClientApi;
import io.smallrye.graphql.client.typesafe.api.Header;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;
import org.superheroes.config.Resolver;
import org.superheroes.hero.SuperHero;
import org.superheroes.repository.Repository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.superheroes.config.CollectionUtils.single;
import static org.superheroes.team.TeamsBoundary.Clearance.SECRET;

@Stateless
@GraphQLApi
public class TeamsBoundary {
    @Inject Repository repository;

    @GraphQlClientApi
    @Header(name = "S.H.I.E.L.D.-Clearance", method = "establishShieldClearance")
    public interface SuperHeroesApi {
        List<NamedHero> heroes();
    }

    @SuppressWarnings("unused")
    public static Clearance establishShieldClearance() { return SECRET; }

    @SuppressWarnings("unused")
    public enum Clearance {
        PUBLIC, INTERNAL, SECRET, TOP_SECRET
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

    @Query public List<Team> teams() {
        return repository.teams().collect(toList());
    }

    @Resolver public int size(@Source Team team) {
        return heroes(team).size();
    }

    @Resolver public List<NamedHero> heroes(@Source Team team) {
        List<NamedHero> heroes = superHeroesApi.heroes();
        return heroes.stream()
            .filter(hero -> teamAffiliationNames(hero).contains(team.getName()))
            .collect(toList());
    }

    private List<String> teamAffiliationNames(NamedHero hero) {
        return repository.getTeamAffiliations(hero.getName()).stream().map(Team::getName).collect(toList());
    }

    @Resolver public List<Team> teamAffiliations(@Source SuperHero superHero) {
        return repository.getTeamAffiliations(superHero.getName());
    }
}
