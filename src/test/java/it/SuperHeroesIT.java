package it;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.List;

import static graphql.client.GraphQlClient.graphQlClient;
import static org.assertj.core.api.BDDAssertions.then;

public class SuperHeroesIT {

    public interface SuperHeroesApi {
        List<SuperHero> allHeroesIn(String location);

        SuperHero findHeroByName(String name);

        Team team(String name);

        List<Team> allTeams();
    }

    @Getter @Setter @ToString
    public static class SuperHero {
        private String name;
        private List<String> superPowers;
    }

    @Getter @Setter @ToString
    public static class Team {
        private String name;
        private int size;
        private List<SuperHero> members;
    }

    @Test void shouldGetIronMan() {
        SuperHero ironMan = api.findHeroByName("Iron Man");

        then(ironMan.name).isEqualTo("Iron Man");
        then(ironMan.superPowers).containsExactly("wealth", "engineering");
    }

    @Test void shouldGetAllTeams() {
        List<Team> all = api.allTeams();

        then(all.size()).isEqualTo(3);
        thenIsAvengers(all.get(0));
    }

    @Test void shouldGetTeamByName() {
        Team avengers = api.team("Avengers");

        thenIsAvengers(avengers);
    }

    private void thenIsAvengers(Team avengers) {
        then(avengers.name).isEqualTo("Avengers");
        then(avengers.size).isEqualTo(3);
        then(avengers.members.get(0).name).isEqualTo("Iron Man");
    }

    @Test void shouldGetSuperHeroesInOuterSpace() {
        List<SuperHero> found = api.allHeroesIn("Outer Space");

        then(found.size()).isEqualTo(1);
        then(found.get(0).name).isEqualTo("Starlord");
    }

    private final SuperHeroesApi api = graphQlClient(SuperHeroesApi.class);
}
