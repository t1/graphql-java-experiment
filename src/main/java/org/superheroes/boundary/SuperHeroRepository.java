package org.superheroes.boundary;

import org.eclipse.microprofile.graphql.Source;
import org.superheroes.model.SuperHero;
import org.superheroes.model.Team;

import javax.annotation.PostConstruct;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class SuperHeroRepository {
    private static final Jsonb JSONB = JsonbBuilder.create();
    private static final Type SUPERHERO_LIST_TYPE = new ArrayList<SuperHero>() {}
        .getClass().getGenericSuperclass();

    private List<SuperHero> heroes;
    private final List<Team> teams = new ArrayList<>();

    @PostConstruct private void init() {
        InputStream stream = SuperHeroRepository.class.getResourceAsStream("/superheroes.json");
        this.heroes = JSONB.fromJson(stream, SUPERHERO_LIST_TYPE);
        createRealTeams();
    }

    /** The teams are duplicated by their names */
    private void createRealTeams() {
        for (SuperHero hero : this.heroes) {
            List<Team> realAffiliations = new ArrayList<>();
            for (Team rawTeam : hero.getTeamAffiliations()) {
                Team realTeam = realTeam(rawTeam.getName())
                    .orElseGet(() -> createRealTeam(rawTeam.getName()));
                realAffiliations.add(realTeam);
            }
            hero.setTeamAffiliations(realAffiliations);
        }
    }

    private Optional<Team> realTeam(String name) {
        return this.teams.stream().filter(team -> team.getName().equals(name)).findAny();
    }

    private Team createRealTeam(String name) {
        Team team = new Team();
        team.setName(name);
        this.teams.add(team);
        return team;
    }

    public List<SuperHero> with(Predicate<SuperHero> predicate) {
        return heroes.stream().filter(predicate).collect(toList());
    }

    public List<Team> teamsWith(Predicate<Team> predicate) {
        return teams.stream().filter(predicate).collect(toList());
    }

    @SuppressWarnings("unused")
    public int size(@Source Team team) {
        return members(team).size();
    }

    @SuppressWarnings("unused")
    public List<SuperHero> members(@Source Team team) {
        List<SuperHero> result = new ArrayList<>();
        this.heroes.forEach(hero -> hero.getTeamAffiliations().stream()
            .filter(t -> t.getName().equals(team.getName()))
            .map(t -> hero)
            .forEach(result::add));
        return result;
    }
}
