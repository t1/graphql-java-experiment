package org.superheroes.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.superheroes.hero.SuperHero;
import org.superheroes.team.Team;

import javax.annotation.PostConstruct;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.superheroes.config.CollectionUtils.single;

@Slf4j
public class Repository {

    private List<SuperHero> heroes;
    private List<Team> teams;
    private Map<String, List<Team>> teamAffiliations;
    private Map<String, String> realNames;

    @PostConstruct private void init() {
        Type superheroListType = new ArrayList<SuperHeroData>() {}.getClass().getGenericSuperclass();
        InputStream stream = Repository.class.getResourceAsStream("/superheroes.json");
        List<SuperHeroData> data = JSONB.fromJson(stream, superheroListType);

        this.heroes = data.stream().map(this::toSuperHero).collect(toList());
        this.teams = allTeams(data);
        this.teamAffiliations = this.heroes.stream().collect(toMap(SuperHero::getName, new TeamCollector(data)::teams));
        this.realNames = data.stream().collect(toMap(SuperHeroData::getName, SuperHeroData::getRealName));
    }

    @Getter @Setter @ToString
    public static class SuperHeroData {
        private String name;
        private String realName;
        private String primaryLocation;
        private List<String> superPowers;
        private List<String> teamAffiliations;

        public Stream<String> teamAffiliations() { return teamAffiliations.stream(); }
    }

    private List<Team> allTeams(List<SuperHeroData> data) {
        return data.stream()
            .flatMap(SuperHeroData::teamAffiliations)
            .distinct()
            .map(Team::named)
            .collect(toList());
    }

    @RequiredArgsConstructor private class TeamCollector {
        private final List<SuperHeroData> data;

        public List<Team> teams(SuperHero hero) {
            SuperHeroData heroData = heroData(hero);
            return teams.stream()
                .filter(team -> heroData.teamAffiliations.contains(team.getName()))
                .collect(toList());
        }

        private SuperHeroData heroData(SuperHero hero) {
            return single(data.stream()
                .filter(superHeroData -> superHeroData.getName().equals(hero.getName()))
                .collect(toList()), "hero named " + hero.getName());
        }
    }

    private SuperHero toSuperHero(SuperHeroData item) {
        SuperHero superHero = new SuperHero();
        superHero.setName(item.getName());
        superHero.setPrimaryLocation(item.getPrimaryLocation());
        superHero.setSuperPowers(new ArrayList<>(item.getSuperPowers()));
        return superHero;
    }

    public Stream<SuperHero> allSuperHeroes() { return heroes.stream(); }

    public List<SuperHero> superHeroesWith(Predicate<SuperHero> predicate) {
        return allSuperHeroes().filter(predicate).collect(toList());
    }

    public String realNameOf(String heroName) { return realNames.get(heroName); }

    public Stream<Team> allTeams() { return teams.stream(); }

    public List<Team> teamsWith(Predicate<Team> predicate) {
        return allTeams().filter(predicate).collect(toList());
    }

    public List<Team> getTeamAffiliations(String superHeroName) { return teamAffiliations.get(superHeroName); }

    private static final Jsonb JSONB = JsonbBuilder.create();
}
