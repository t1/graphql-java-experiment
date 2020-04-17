package it;

import io.smallrye.graphql.client.api.GraphQlClientBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

public class SuperHeroesIT {

    private final SuperHeroesApi api = GraphQlClientBuilder.newBuilder()
        .build(SuperHeroesApi.class);

    public interface SuperHeroesApi {
        List<SuperHero> allHeroesIn(String location);

        SuperHero findHeroByName(String name);

        @Query("findHeroByName")
        SuperHeroWithTeams findHeroWithTeamsByName(String name);

        @Query("findHeroByName")
        SuperHeroWithRealName findHeroWithRealNameByName(String name);

        Team team(String name);

        List<Team> allTeams();

        @Mutation("createNewHero") SuperHero add(SuperHero newHero);
    }

    @Getter @Setter @ToString @Builder @AllArgsConstructor @NoArgsConstructor
    public static class SuperHero {
        private String name;
        private @Singular List<String> superPowers;
    }

    @Getter @Setter @ToString
    public static class SuperHeroWithRealName {
        private String name;
        @Name("realName") private String secretIdentity;
    }

    @Getter @Setter @ToString
    public static class SuperHeroWithTeams {
        private String name;
        private List<String> superPowers;
        private List<TeamWithoutMembers> teamAffiliations;
    }

    @Getter @Setter @ToString
    public static class Team {
        private String name;
        private List<SuperHero> members;
    }

    @Getter @Setter @ToString
    public static class TeamWithoutMembers {
        private String name;
        private int size;
    }

    @Test void shouldGetIronMan() {
        SuperHero ironMan = api.findHeroByName("Iron Man");

        then(ironMan.name).isEqualTo("Iron Man");
        then(ironMan.superPowers).containsExactly("wealth", "engineering");
    }

    @Test void shouldGetSpiderManWithRealName() {
        SuperHeroWithRealName spiderMan = api.findHeroWithRealNameByName("Spider Man");

        then(spiderMan.name).isEqualTo("Spider Man");
        then(spiderMan.secretIdentity).isEqualTo("Peter Parker");
    }

    @Test void shouldGetIronManWithTeams() {
        SuperHeroWithTeams ironMan = api.findHeroWithTeamsByName("Iron Man");

        then(ironMan.name).isEqualTo("Iron Man");
        then(ironMan.superPowers).containsExactly("wealth", "engineering");
        then(ironMan.teamAffiliations).hasSize(1);
        then(ironMan.teamAffiliations.get(0).name).isEqualTo("Avengers");
        then(ironMan.teamAffiliations.get(0).size).isEqualTo(3);
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
        then(avengers.members).hasSize(3);
        then(avengers.members.get(0).name).isEqualTo("Iron Man");
    }

    @Test void shouldGetSuperHeroesInOuterSpace() {
        List<SuperHero> found = api.allHeroesIn("Outer Space");

        then(found.size()).isEqualTo(1);
        then(found.get(0).name).isEqualTo("Starlord");
    }

    @Test void shouldAddSuperHero() {
        SuperHero created = api.add(SuperHero.builder().name("Groot").build());

        then(created.name).isEqualTo("Groot");
        then(created.superPowers).containsExactly();
    }
}
